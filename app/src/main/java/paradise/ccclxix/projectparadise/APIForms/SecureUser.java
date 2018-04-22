package paradise.ccclxix.projectparadise.APIForms;

public class SecureUser {
    private String username = null;
    private String email = null;


    public SecureUser(String username, String email){
        this.username = username;
        this.email = email;
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
}
