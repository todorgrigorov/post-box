package com.tgrigorov.postbox.data.entities;

public interface IEntityFactory {
    <T extends Entity> T create(Class<T> type);

    <T extends Entity> T create(String name);
}
