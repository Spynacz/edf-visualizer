package org.fhdmma.edf;

import lombok.Getter;

public class User {
    @Getter private int id;
    @Getter private String username;
    private String password;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String toString(){
        return "id: " + id + "\nusername: " + username + "\npassword: " + password;
    }
}
