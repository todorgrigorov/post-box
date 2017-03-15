package com.tgrigorov.postbox.http.data;

import com.google.gson.Gson;

public class Serializer implements ISerializer {
    public Serializer() {
        serializer = new Gson();
    }

    public <T extends Model> String serialize(T data) {
        return serializer.toJson(data);
    }

    public <T extends Model> T deserialize(Class<T> type, String data) {
        return serializer.fromJson(data, type);
    }

    private Gson serializer;
}
