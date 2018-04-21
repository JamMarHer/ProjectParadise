package paradise.ccclxix.projectparadise.APIForms;

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

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}