package com.tgrigorov.postbox.http.data;

public interface ISerializer {
    <T extends Model> String serialize(T data);
    <T extends Model> T deserialize(Class<T> type, String data);
}
