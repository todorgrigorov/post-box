package com.tgrigorov.postbox.http;

import com.tgrigorov.postbox.http.data.Model;
import com.tgrigorov.postbox.services.background.IBackgroundResult;

public interface IRestOperation<T extends Model> {
    void get(IBackgroundResult<T> result);
    void post(final T data, IBackgroundResult<T> result);
    void put(final T data, IBackgroundResult<T> result);
    void patch(final T data, IBackgroundResult<T> result);
    void delete(IBackgroundResult<T> result);
}
