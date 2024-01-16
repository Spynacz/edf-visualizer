package org.fhdmma.edf;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {
    private long id;
    private String username;
    private String password;

    public String toString() {
        return "id: " + id + "\nusername: " + username + "\npassword: " + password;
    }
}
