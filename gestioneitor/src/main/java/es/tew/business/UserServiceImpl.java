package es.tew.business;

import es.tew.model.User;

import java.util.Optional;

public  class UserServiceImpl implements UserService {
    @Override
    public Optional<User> verify(User user) {
        if (user.getUsername().equals("admin") && user.getPassword().equals("password")) {
            user.setName("Admin");
            user.setPassword("");
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
