package me.khadija.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String hashedPassword;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
}
