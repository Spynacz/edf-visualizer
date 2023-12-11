package org.fhdmma.edf;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {
    private int id; 
    private String username;
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
