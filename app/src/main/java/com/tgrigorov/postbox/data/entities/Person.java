package com.tgrigorov.postbox.data.entities;

public class Person extends Entity {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @DbColumn("NAME")
    private String name;

    @DbColumn("ADDRESS")
    private String address;
}
