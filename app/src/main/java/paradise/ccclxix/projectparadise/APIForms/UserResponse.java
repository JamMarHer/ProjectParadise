package paradise.ccclxix.projectparadise.APIForms;

public class UserResponse implements APIResponse{

    private int status;
    private String token;

    public UserResponse(int status, String token){
        this.status = status;
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getEvent_id() {
        return null;
    }

    public void setToken(String token){
        this.token = token;
    }
}