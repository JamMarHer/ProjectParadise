package paradise.ccclxix.projectparadise.Network;

import paradise.ccclxix.projectparadise.APIForms.APIResponse;

public class NetworkResponse {
    private int status;
    private APIResponse response;


    public NetworkResponse(int  status, APIResponse apiResponse){
        this.response = apiResponse;
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

    public void setResponse(APIResponse response) {
        this.response = response;
    }

}
