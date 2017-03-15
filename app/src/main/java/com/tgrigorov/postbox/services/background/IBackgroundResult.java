package com.tgrigorov.postbox.services.background;


public interface IBackgroundResult<T> {
    void success(T data);
    void cancel();
}
