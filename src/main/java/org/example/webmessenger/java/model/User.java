package org.example.webmessenger.java.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String id;
    private String login;
    private String email;
    private String name;
}
