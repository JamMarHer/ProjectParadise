package paradise.ccclxix.projectparadise.APIForms;


import paradise.ccclxix.projectparadise.Loaders.EventLoader;

public class Event implements  APICall{

    private String token = null;
    private String host = null;
    private String event_name = null;
    private String event_id = null;
    private String privacy = null;
    private String latitude = null;
    private String longitude = null;


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

    public String getHost(){return host;}

    public void setHost(String host){this.host = host;}

    public String getName() {
        return event_name;
    }

    public void setName(String name) {
        this.event_name = name;
    }

    public String getId() {
        return event_id;
    }

    public void setId(String id) {
        this.event_id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}