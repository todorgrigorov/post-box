package com.tgrigorov.postbox.http;

import com.tgrigorov.postbox.http.data.Model;

public interface IRestClient {
    <T extends Model> IRestOperation<T> getOperation(Class<T> type, String uri, String token);
}
