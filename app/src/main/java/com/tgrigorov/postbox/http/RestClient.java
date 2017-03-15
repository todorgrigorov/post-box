package com.tgrigorov.postbox.http;

import com.tgrigorov.postbox.http.data.Model;

public class RestClient implements IRestClient {
    @Override
    public <T extends Model> IRestOperation<T> getOperation(Class<T> type, String uri, String token) {
        return new RestOperation(type, BASE_URL + uri, token);
    }

    private final String BASE_URL = "https://graph.microsoft.com/v1.0/me/";
}
