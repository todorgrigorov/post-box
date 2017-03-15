package com.tgrigorov.postbox.http.data;

public interface IModelFactory {
    <T extends Model> T create(Class<T> type);
}
