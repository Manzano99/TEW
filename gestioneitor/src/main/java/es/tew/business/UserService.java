package es.tew.business;

import es.tew.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> verify(User user);
}
