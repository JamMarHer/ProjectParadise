package paradise.ccclxix.projectparadise.APIForms;

import java.util.List;
import java.util.Map;

public interface APIResponse {

    int getStatus();
    String getToken();
    String getEvent_id();
    List<SecureUser> getAttendants();
    Map<String, String> getMeta();
}
