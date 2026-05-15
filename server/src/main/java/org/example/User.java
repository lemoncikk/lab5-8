package org.example;

import lombok.Getter;

public class User {
    @Getter
    final private int id;
    @Getter
    final private String login;
    @Getter
    final private String hash;

    public User(int id, String login, String hash) {
        this.id = id;
        this.login = login;
        this.hash = hash;
    }
}
