package com.tgrigorov.postbox.data;

import android.database.sqlite.SQLiteDatabase;

import com.tgrigorov.postbox.data.entities.Entity;

public interface IDbContext {
    void configure();

    <T extends Entity> IDbOperation<T> getContext(Class<T> type);

    SQLiteDatabase getDatabase();

    void disconnect();
}
