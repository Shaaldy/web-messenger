package org.example.webmessenger.java.model;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String email;
    private String name;
    private String avatarUrl;
    private String id;

    public User(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.avatarUrl = picture;
        this.id = HashUtils.hashUserFields(email, name, picture);
    }
}
