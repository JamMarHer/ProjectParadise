package paradise.ccclxix.projectparadise.APIForms;

import java.util.List;
import java.util.Map;

public class FullEventResponse implements APIResponse {

    private String token = null;
    private String host = null;
    private String event_name = null;
    private String event_id = null;
    private String privacy = null;
    private String latitude = null;
    private String longitude = null;
    private int status;
    private Map<String, String> meta;
    private List<SecureUser> attendants = null;

    public FullEventResponse(String host, String event_name, String event_id, String privacy,
                             String latitude, String longitude, List<SecureUser> attendants){

        this.host = host;
        this.event_name = event_name;
        this.event_id = event_id;
        this.privacy = privacy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendants = attendants;
    }


    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getEvent_id() {
        return this.event_id;
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

    public void setToken(String token) {
        this.token = token;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public List<SecureUser> getAttendants() {
        return attendants;
    }

    public void setAttendants(List<SecureUser> attendants) {
        this.attendants = attendants;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
