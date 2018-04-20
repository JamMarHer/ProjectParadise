package paradise.ccclxix.projectparadise.Network;

import java.util.List;

import paradise.ccclxix.projectparadise.APIForms.APIResponse;
import paradise.ccclxix.projectparadise.APIForms.Event;

public class NetworkResponse {
    private int status;
    private APIResponse response;
    // TODO this is not consistent.
    private List<Event> listResponse;

    public NetworkResponse(int status){
        this.status = status;
    }

    public NetworkResponse(int  status, APIResponse apiResponse){
        this.response = apiResponse;
        this.status = status;
    }

    public NetworkResponse(int  status, List<Event> apiResponse){
        this.listResponse = apiResponse;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public APIResponse getResponse() {
        return response;
    }

    public List<Event> getListEvents() {
        return listResponse;
    }

    public void setListEvents(List<Event> listResponse){
        this.listResponse = listResponse;
    }

    public void setResponse(APIResponse response) {
        this.response = response;
    }

}
