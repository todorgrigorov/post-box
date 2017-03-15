package com.tgrigorov.postbox.data.entities;


import com.tgrigorov.postbox.PostBox;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityFactory implements IEntityFactory {
    public <T extends Entity> T create(Class<T> type) {
        T instance = null;
        try {
            instance = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            PostBox.getExceptionHandler().handle(e);
        }
        return instance;
    }

    public <T extends Entity> T create(String name) {
        T instance = null;
        String entitiesPackage = getClass().getPackage().getName();
        Class<?> entityClass;
        try {
            entityClass = Class.forName(entitiesPackage + "." + name);
            Constructor<?> constructor = entityClass.getConstructor();
            instance = (T)constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            PostBox.getExceptionHandler().handle(e);
        }
        return instance;
    }
}
