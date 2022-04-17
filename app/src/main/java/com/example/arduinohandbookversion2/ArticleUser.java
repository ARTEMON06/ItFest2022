package com.example.arduinohandbookversion2;

public class ArticleUser {
    String user, id;

    public ArticleUser(String user, String id) {
        this.user = user;
        this.id = id;
    }

    public ArticleUser() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ArticleUser{" +
                "user='" + user + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
