package com.tgrigorov.postbox.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.data.entities.DbColumn;
import com.tgrigorov.postbox.data.entities.DbForeign;
import com.tgrigorov.postbox.data.entities.DbNotNull;
import com.tgrigorov.postbox.data.entities.DbPrimary;
import com.tgrigorov.postbox.data.entities.Entity;
import com.tgrigorov.postbox.data.entities.MissingColumnException;
import com.tgrigorov.postbox.utils.FieldUtils;
import com.tgrigorov.postbox.utils.ListUtils;
import com.tgrigorov.postbox.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class DbOperation<T extends Entity> implements IDbOperation<T> {
    public DbOperation(Class<T> type) {
        this.type = type;
    }

    public void createTable(SQLiteDatabase database) throws MissingColumnException {
        T entity = PostBox.getEntityFactory().create(type);
        StringBuilder columns = new StringBuilder();

        List<Field> fields = getFields(entity);
        for (Field field : fields) {
            Type fieldType = field.getType();
            String columnType = null;
            if (fieldType == int.class || fieldType == boolean.class || FieldUtils.isEntityType(field)) {
                columnType = "INTEGER";
            } else if (fieldType == float.class) {
                columnType = "FLOAT";
            } else if (fieldType == String.class || fieldType == Date.class) {
                columnType = "TEXT";
            }

            DbColumn dbColumn = field.getAnnotation(DbColumn.class);
            if (dbColumn == null) {
                throw new MissingColumnException("No DbColumn annotation on field " + field.getName() + " of type " + entity.getClass().getSimpleName());
            }

            String column = dbColumn.value();
            DbPrimary dbPrimary = field.getAnnotation(DbPrimary.class);
            String primary = (dbPrimary != null && dbPrimary.value()) ? "PRIMARY KEY AUTOINCREMENT" : "";
            DbForeign dbForeign = field.getAnnotation(DbForeign.class);
            String foreign = (dbForeign != null && dbForeign.value()) ? "REFERENCES " + column.replace(FOREIGN_KEY_PREFIX, "") + "(ID)" : "";
            DbNotNull dbNotNull = field.getAnnotation(DbNotNull.class);
            String nullable = (dbNotNull != null && !dbNotNull.value()) ? "NOT NULL" : "NULL";
            columns.append(column).append(" ").append(columnType).append(" ").append(primary).append(" ").append(foreign).append(" ").append(nullable).append(",");
        }

        String query = String.format(CREATE_TABLE, getTableName(), trimLastComma(columns));
        database.execSQL(query);
    }

    public void dropTable(SQLiteDatabase database) {
        String query = String.format(DROP_TABLE, getTableName());
        database.execSQL(query);
    }

    public T create(T entity) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Field> fields = getFields(entity);
        for (Field field : fields) {
            // skip the identifier column
            DbPrimary dbPrimary = field.getAnnotation(DbPrimary.class);
            if (dbPrimary == null || !dbPrimary.value()) {
                DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                String column = dbColumn.value();
                columns.append(column).append(",");

                Object value = prepareInsertValue(field, entity, column);
                values.append(value).append(",");
            }
        }

        final String query = String.format(INSERT, getTableName(), trimLastComma(columns), trimLastComma(values));
        executeInTransaction(new Callable<Void>() {
            public Void call() throws Exception {
                PostBox.getDbContext().getDatabase().execSQL(query);
                return null;
            }
        });

        return loadLast();
    }

    public T load(int id) {
        String query = String.format(SELECT_BY_ID, getTableName(), id);
        Cursor cursor = PostBox.getDbContext().getDatabase().rawQuery(query, null);

        List<T> result = extractCursor(cursor);
        return ListUtils.firstOrDefault(result);
    }

    public List<T> list() {
        String query = String.format(SELECT, getTableName());
        Cursor cursor = PostBox.getDbContext().getDatabase().rawQuery(query, null);
        return extractCursor(cursor);
    }

    public T update(T entity) {
        StringBuilder values = new StringBuilder();
        List<Field> fields = getFields(entity);
        for (Field field : fields) {
            // skip the identifier column
            DbPrimary dbPrimary = field.getAnnotation(DbPrimary.class);
            if (dbPrimary == null || !dbPrimary.value()) {
                DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                String column = dbColumn.value();

                Object value = prepareInsertValue(field, entity, column);
                values.append(column).append("=").append(value).append(",");
            }
        }

        final String query = String.format(UPDATE_BY_ID, getTableName(), trimLastComma(values), entity.getId());
        executeInTransaction(new Callable<Void>() {
            public Void call() throws Exception {
                PostBox.getDbContext().getDatabase().execSQL(query);
                return null;
            }
        });

        return load(entity.getId());
    }

    public T delete(int id) {
        T result = load(id);

        final String query = String.format(DELETE_BY_ID, getTableName(), id);
        executeInTransaction(new Callable<Void>() {
            public Void call() {
                PostBox.getDbContext().getDatabase().execSQL(query);
                return null;
            }
        });

        return result;
    }

    public int getCount() {
        String query = String.format(COUNT, getTableName());
        Cursor cursor = PostBox.getDbContext().getDatabase().rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void executeInTransaction(Callable<Void> callable) {
        SQLiteDatabase database = PostBox.getDbContext().getDatabase();
        try {
            database.beginTransaction();
            callable.call();
            database.setTransactionSuccessful();
        } catch (Exception e) {
            PostBox.getExceptionHandler().handle(e);
        } finally {
            if (database != null) {
                database.endTransaction();
            }
        }
    }

    private DbOperation() {
    }

    private String getTableName() {
        return PostBox.getEntityFactory().create(type).getClass().getSimpleName().toUpperCase();
    }

    private String trimLastComma(StringBuilder text) {
        int length = text.length();
        text.delete(text.lastIndexOf(","), length);
        return text.toString().trim();
    }

    private T loadLast() {
        String query = String.format(SELECT_LAST_INSERT, getTableName());
        Cursor cursor = PostBox.getDbContext().getDatabase().rawQuery(query, null);

        List<T> result = extractCursor(cursor);
        return ListUtils.firstOrDefault(result);
    }

    private List<Field> getFields(Object object) {
        List<Field> fields = new LinkedList<>();

        Class type = object.getClass();
        Collections.addAll(fields, type.getSuperclass().getDeclaredFields());
        Collections.addAll(fields, type.getDeclaredFields());

        return fields;
    }

    private Object getFieldValue(Field field, Object object) {
        Object value = null;
        field.setAccessible(true);
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            PostBox.getExceptionHandler().handle(e);
        }
        return value;
    }

    private void setFieldValue(Field field, Object object, Object value) {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            PostBox.getExceptionHandler().handle(e);
        }
    }

    private Object prepareInsertValue(Field field, Entity entity, String column) {
        Object value = getFieldValue(field, entity);
        Type fieldType = field.getType();

        // keep consistency between types
        if (fieldType == Date.class) {
            boolean isNew = entity.isNew();
            if (isNew && (column.equals("CREATED") || column.equals("UPDATED"))) {
                value = new Date(); // set initial timestamps
            } else if (!isNew && column.equals("UPDATED")) {
                value = new Date(); // update timestamp
            }

            value = "\"" + dateFormat.format(value) + "\"";
        } else if (fieldType == String.class) {
            String text = (String) value;
            value = "\"" + StringUtils.encodeUnsafe(text) + "\"";
        } else if (fieldType == boolean.class) {
            if ((boolean) value) {
                value = 1;
            } else {
                value = 0;
            }
        } else if (FieldUtils.isEntityType(field)) {
            value = ((T) value).getId();
        }

        return value;
    }

    private List<T> extractCursor(Cursor cursor) {
        List<T> result = new LinkedList<>();

        if (cursor.moveToFirst()) {
            do {
                T entity = PostBox.getEntityFactory().create(type);

                List<Field> fields = getFields(entity);
                for (Field field : fields) {
                    DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                    String column = dbColumn.value();
                    int columnIndex = cursor.getColumnIndexOrThrow(column);
                    setFieldValue(field, entity, getValueFromCursor(cursor, field, columnIndex));
                }

                result.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    private Object getValueFromCursor(Cursor cursor, Field field, int columnIndex) {
        Class<?> fieldType = field.getType();
        Object value = null;
        if (fieldType == int.class) {
            value = cursor.getInt(columnIndex);
        } else if (fieldType == float.class) {
            value = cursor.getFloat(columnIndex);
        } else if (fieldType == String.class) {
            String text = cursor.getString(columnIndex);
            text = StringUtils.decodeUnsafe(text);
            value = text;
        } else if (fieldType == Date.class) {
            String text = cursor.getString(columnIndex);
            try {
                value = dateFormat.parse(text);
            } catch (ParseException e) {
                PostBox.getExceptionHandler().handle(e);
            }
        } else if (fieldType == boolean.class) {
            value = cursor.getInt(columnIndex) > 0;
        } else if (FieldUtils.isEntityType(field)) {
            String typeName = fieldType.getSimpleName();
            Entity type = PostBox.getEntityFactory().create(typeName);
            int id = cursor.getInt(columnIndex);
            value = PostBox.getDbContext().getContext(type.getClass()).load(id); // cascade getOperation the related entity
        }
        return value;
    }

    private static final String CREATE_TABLE = "CREATE TABLE %s (%s)";
    private static final String DROP_TABLE = "DROP TABLE %s";
    private static final String SELECT = "SELECT * FROM %s";
    private static final String SELECT_BY_ID = SELECT + " WHERE ID = %s";
    private static final String SELECT_LAST_INSERT = SELECT + " ORDER BY ID DESC LIMIT 1";
    private static final String INSERT = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String UPDATE_BY_ID = "UPDATE %s SET %s WHERE ID = %s";
    private static final String DELETE_BY_ID = "DELETE FROM %s WHERE ID = %s";
    private static final String COUNT = "SELECT COUNT(*) FROM %s";
    private static final String FOREIGN_KEY_PREFIX = "_ID";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Class<T> type;
}
