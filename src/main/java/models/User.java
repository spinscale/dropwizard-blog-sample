package models;

public class User {

    // TODO: hash password
    public User() {}

    public User(String name, String password) {
        setUsername(name);
        setPassword(password);
    }

    private String username;
    private String password;
    private String fullname;

    public String getUsername() {
        return username;
    }
    public void setUsername(String name) {
        username = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


}
