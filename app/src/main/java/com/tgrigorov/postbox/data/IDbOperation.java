package com.tgrigorov.postbox.data;

import android.database.sqlite.SQLiteDatabase;

import com.tgrigorov.postbox.data.entities.Entity;
import com.tgrigorov.postbox.data.entities.MissingColumnException;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;

public interface IDbOperation<T extends Entity>  {
    void createTable(SQLiteDatabase database) throws MissingColumnException;
    void dropTable(SQLiteDatabase database);

    T create(T entity);

    T load(int id);
    List<T> list();

    T update(T entity);

    T delete(int id);

    void executeInTransaction(Callable<Void> callable);

    int getCount();
}
