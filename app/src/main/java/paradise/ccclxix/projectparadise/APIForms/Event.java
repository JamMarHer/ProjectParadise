package paradise.ccclxix.projectparadise.APIForms;

public class Event {

    private String host = null;
    private String name = null;
    private String id = null;
    private String privacy = null;

    public Event (String host, String name, String id, String privacy){
        this.host = host;
        this.name = name;
        this.id = id;
        this.privacy = privacy;
    }

    public Event (String host, String name, String privacy){
        this.host = host;
        this.name = name;
        this.privacy = privacy;
    }

    public String getHost(){return host;}

    public void setHost(String host){this.host = host;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}