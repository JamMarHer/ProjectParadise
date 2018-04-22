package paradise.ccclxix.projectparadise.APIForms;


public class Event implements  APICall{

    private String token = null;
    private String host = null;
    private String event_name = null;
    private String event_id = null;
    private String privacy = null;
    private String latitude = null;
    private String longitude = null;


    public Event (){

    }

    public Event setUpLoginEvent(String token, String event_id){
        this.token = token;
        this.event_id = event_id;
        return this;
    }

    public Event (String token, String name){
        this.token = token;
        this.event_name = name;
    }

    public Event (String token, String host, String name, String id, String privacy){
        this.token = token;
        this.host = host;
        this.event_name = name;
        this.event_id = id;
        this.privacy = privacy;
    }

    public Event (String token,String host, String name, String privacy, String latitude, String longitude){
        this.token = token;
        this.host = host;
        this.event_name = name;
        this.privacy = privacy;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event (String token, String event_id,String host, String name, String privacy, String latitude, String longitude){
        this.token = token;
        this.event_id = event_id;
        this.host = host;
        this.event_name = name;
        this.privacy = privacy;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}