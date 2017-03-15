package com.tgrigorov.postbox.utils;

import com.tgrigorov.postbox.data.entities.Entity;

import java.lang.reflect.Field;

public class FieldUtils {
    public static boolean isEntityType(Field field) {
        return (field.getType().getSuperclass() == Entity.class);
    }
}
