package paradise.ccclxix.projectparadise;


public class User {

    private String username = null;
    private String email = null;
    private String password = null;
    private String token = null;

    public User (String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User (String email, String token){
        this.email = email;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}