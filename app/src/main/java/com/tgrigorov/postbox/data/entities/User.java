package com.tgrigorov.postbox.data.entities;


public class User extends Entity {
    public User() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @DbColumn("EXTERNAL_ID")
    private String externalId;

    @DbColumn("TOKEN")
    private String token;

    @DbColumn("REFRESH_TOKEN")
    private String refreshToken;

    @DbColumn("CURRENT")
    private boolean current;

    @DbColumn("PERSON_ID")
    @DbForeign()
    private Person person;
}
