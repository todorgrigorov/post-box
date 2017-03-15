package com.tgrigorov.postbox.services.background;

public interface IBackgroundProcess<T> {
    T run();
}
