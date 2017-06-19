package com.seapip.thomas.curvermi.shared;

import java.io.Serializable;
import java.util.List;

public class Lobby implements Serializable {
    private String token;
    private String name;
    private User host;
    private List<User> users;
    private boolean password;

    public Lobby(String token, String name, boolean password, User host, List<User> users) {
        this.token = token;
        this.name = name;
        this.host = host;
        this.users = users;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public User getHost() {
        return host;
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean hasPassword() {
        return password;
    }
}
