package services;

import models.User;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

import daos.UserDao;

public class ElasticSearchAuthenticator implements Authenticator<BasicCredentials, User> {

    UserDao userDao;

    public ElasticSearchAuthenticator(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        User user = userDao.findByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        if (user != null) {
            return Optional.of(user);
        }

        return Optional.absent();
    }

}
