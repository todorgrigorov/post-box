package com.tgrigorov.postbox.data;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.data.entities.Entity;
import com.tgrigorov.postbox.data.entities.Mail;
import com.tgrigorov.postbox.data.entities.MissingColumnException;
import com.tgrigorov.postbox.data.entities.Person;
import com.tgrigorov.postbox.data.entities.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContext extends SQLiteOpenHelper implements IDbContext {
    public DbContext(Context context) {
        super(context, NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            getContext(Person.class).createTable(db);
            getContext(User.class).createTable(db);
            getContext(Mail.class).createTable(db);
        } catch (MissingColumnException e) {
            PostBox.getExceptionHandler().handle(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        getContext(Mail.class).dropTable(db);
        getContext(User.class).dropTable(db);
        getContext(Person.class).dropTable(db);
        onCreate(db);
    }

    public void configure() {
        database = getWritableDatabase();
    }

    public <T extends Entity> IDbOperation<T> getContext(Class<T> type) {
        return new DbOperation<>(type);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void disconnect() {
        database.close();
        close();
    }

    private static final String NAME = "postbox";

    private SQLiteDatabase database;
}
