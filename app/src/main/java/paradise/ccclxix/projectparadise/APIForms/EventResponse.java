package paradise.ccclxix.projectparadise.APIForms;

public class EventResponse {

    private int status;
    private String eventID;

    public EventResponse(int status, String eventID){
        this.status = status;
        this.eventID = eventID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID){
        this.eventID = eventID;
    }



}