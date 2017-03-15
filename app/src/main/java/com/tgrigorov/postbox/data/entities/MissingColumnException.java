package com.tgrigorov.postbox.data.entities;


public class MissingColumnException extends Exception {
    public MissingColumnException(String message) {
        super(message);
    }
}
