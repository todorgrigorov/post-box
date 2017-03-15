package com.tgrigorov.postbox.http.data;


public class ModelFactory implements IModelFactory {
    public <T extends Model> T create(Class<T> type) {
        T result = null;
        try {
            result = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
