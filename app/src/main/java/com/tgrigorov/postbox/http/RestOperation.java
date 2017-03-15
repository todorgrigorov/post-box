package com.tgrigorov.postbox.http;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.http.data.Model;
import com.tgrigorov.postbox.services.background.BackgroundTask;
import com.tgrigorov.postbox.services.background.IBackgroundProcess;
import com.tgrigorov.postbox.services.background.IBackgroundResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class RestOperation<T extends Model> implements IRestOperation<T> {
    public RestOperation(Class<T> type, String url, String token) {
        this.type = type;
        this.url = url;
        this.token = token;
    }

    public void get(IBackgroundResult<T> result) {
        executeInBackground(new Callable<T>() {
            public T call() throws Exception {
                String response = execute("GET", null);
                return PostBox.getSerializer().deserialize(type, response);
            }
        }, result);
    }

    public void post(final T data, IBackgroundResult<T> result) {
        executeInBackground(new Callable<T>() {
            public T call() throws Exception {
                String response = execute("POST", PostBox.getSerializer().serialize(data));
                return PostBox.getSerializer().deserialize(type, response);
            }
        }, result);
    }

    public void put(final T data, IBackgroundResult<T> result) {
        executeInBackground(new Callable<T>() {
            public T call() throws Exception {
                String response = execute("PUT", PostBox.getSerializer().serialize(data));
                return PostBox.getSerializer().deserialize(type, response);
            }
        }, result);
    }

    public void patch(final T data, IBackgroundResult<T> result) {
        executeInBackground(new Callable<T>() {
            public T call() throws Exception {
                String response = execute("PATCH", PostBox.getSerializer().serialize(data));
                return PostBox.getSerializer().deserialize(type, response);
            }
        }, result);
    }

    public void delete(IBackgroundResult<T> result) {
        executeInBackground(new Callable<T>() {
            public T call() throws Exception {
                String response = execute("DELETE", null);
                return PostBox.getSerializer().deserialize(type, response);
            }
        }, result);
    }

    private RestOperation() {
    }

    private void executeInBackground(final Callable<T> callable, IBackgroundResult<T> result) {
        new BackgroundTask<>(new IBackgroundProcess() {
            public T run() {
                T result = null;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    PostBox.getExceptionHandler().handle(e);
                }
                return result;
            }
        }, result);
    }

    private String execute(String method, String data) throws UnauthorizedAccessException, ResourceNotFoundException, RemoteServerException {
        String result = null;

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", CONTENT_TYPE);
            connection.setRequestProperty("Authorization", AUTHORIZATION + " " + token);

            if (data != null) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 401) {
                throw new UnauthorizedAccessException();
            } else if (responseCode == 404) {
                throw new ResourceNotFoundException();
            } else if (responseCode == 500) {
                throw new RemoteServerException();
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = reader.readLine();
        } catch (UnauthorizedAccessException | ResourceNotFoundException | RemoteServerException exception) {
            throw exception;
        } catch (IOException exception) {
            PostBox.getExceptionHandler().handle(exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException exception) {
                    PostBox.getExceptionHandler().handle(exception);
                }
            }
        }

        return result;
    }

    private final String CONTENT_TYPE = "application/json";
    private final String AUTHORIZATION = "Bearer";

    private Class<T> type;
    private String url;
    private String token;
}
