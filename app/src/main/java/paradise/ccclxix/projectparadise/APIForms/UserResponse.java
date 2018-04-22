package paradise.ccclxix.projectparadise.APIForms;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<SecureUser> getAttendants() {
        return null;
    }

    @Override
    public Map<String, String> getMeta() {
        return null;
    }

    public void setToken(String token){
        this.token = token;
    }
}