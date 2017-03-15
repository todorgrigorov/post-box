package com.tgrigorov.postbox.services;

public class ExceptionHandler implements IExceptionHandler {
    public void handle(Exception exception) {
        exception.printStackTrace();
    }
}
