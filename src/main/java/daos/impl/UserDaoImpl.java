package daos.impl;

import java.io.IOException;

import models.User;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.node.Node;

import com.yammer.dropwizard.json.Json;

import daos.UserDao;

public class UserDaoImpl implements UserDao {

    private Node node;
    private Json json;

    public UserDaoImpl(Node node, Json json) {
        this.node = node;
        this.json = json;
    }

    public User findByUsernameAndPassword(String username, String password) {
        User user = findByUsername(username);
        if (user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }

    public User findByUsername(String username) {
        GetResponse getResponse = node.client().prepareGet("users", "user", username).execute().actionGet();
        if (getResponse.isExists()) {
            return toUser(getResponse.getSourceAsString());
        }

        return null;
    }

    public void store(User user) {
        byte[] data = json.writeValueAsBytes(user);
        node.client().prepareIndex("users", "user", user.getUsername()).setSource(data).execute().actionGet();
    }

    private User toUser(String source) {
        try {
            return json.readValue(source, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
