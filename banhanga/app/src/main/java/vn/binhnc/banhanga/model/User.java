package vn.binhnc.banhanga.model;

public class User {
    private int id;
    private String email;
    private String pass;
    private String username;
    private String avatar;

    private String mobile;
    private String token;
    private int status;

    public User() {
    }

    public User(int id, String email, String pass, String username, String avatar, String mobile, String token, int status) {
        this.id = id;
        this.email = email;
        this.pass = pass;
        this.username = username;
        this.avatar = avatar;
        this.mobile = mobile;
        this.token = token;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}