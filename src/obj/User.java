package obj;

public class User {
    private String userId;
    private String user;
    private String password;

    public User(String userId, String user, String password) {
        this.userId = userId;
        this.user = user;
        this.password = password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(String username) {
        this.user = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID() {
        return userId;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}