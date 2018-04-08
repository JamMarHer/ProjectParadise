package paradise.ccclxix.projectparadise.APIForms;

import java.util.HashMap;
import java.util.Map;

public class RegistrationResponseForm {

    private boolean status;
    private String id;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public boolean getStatus() {
        return status;
    }

    public boolean setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}