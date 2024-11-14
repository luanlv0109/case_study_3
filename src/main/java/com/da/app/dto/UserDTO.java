package com.da.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private boolean isAdmin;

    public UserDTO( String username) {
        this.username = username;
    }
}
