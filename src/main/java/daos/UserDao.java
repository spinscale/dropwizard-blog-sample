package daos;

import models.User;

public interface UserDao {

    void store(User user);

    User findByUsernameAndPassword(String username, String password);
    User findByUsername(String string);

}
