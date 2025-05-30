package org.example.webmessenger.java.websocket;

import org.example.webmessenger.java.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findByLogin(String login) {
        return users.values().stream()
                .filter(u -> login.equals(u.getEmail()))
                .findFirst();
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Map<String, User> getAll() {
        return users;
    }
}
