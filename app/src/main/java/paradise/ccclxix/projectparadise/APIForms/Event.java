package paradise.ccclxix.projectparadise.APIForms;


import paradise.ccclxix.projectparadise.Loaders.EventLoader;

public class Event {

    private String host = null;
    private String event_name = null;
    private String id = null;
    private String privacy = null;
    private String latitude = null;
    private String longitude = null;

    public Event (String name){
        this.event_name = name;
    }

    public Event (String host, String name, String id, String privacy){
        this.host = host;
        this.event_name = name;
        this.id = id;
        this.privacy = privacy;
    }

    public Event (String host, String name, String privacy, String latitude, String longitude){
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
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