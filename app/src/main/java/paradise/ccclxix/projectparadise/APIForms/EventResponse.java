package paradise.ccclxix.projectparadise.APIForms;

import java.util.List;
import java.util.Map;

public class EventResponse implements APIResponse {

    private int status;
    private String event_id;

    public EventResponse(int status, String event_id){
        this.status = status;
        this.event_id = event_id;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getToken() {
        return null;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getEvent_id() {
        return event_id;
    }

    @Override
    public List<SecureUser> getAttendants() {
        return null;
    }

    @Override
    public Map<String, String> getMeta() {
        return null;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}