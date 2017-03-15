package com.tgrigorov.postbox.utils;

public interface IPredicate<T> {
    boolean filter(T item);
}
