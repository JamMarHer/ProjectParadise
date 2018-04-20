package paradise.ccclxix.projectparadise.Network;

import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;

import paradise.ccclxix.projectparadise.APIForms.APIResponse;
import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.APIForms.EventResponse;
import paradise.ccclxix.projectparadise.APIForms.User;
import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.ErrorCodes;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHandler {

    private NetworkResponse networkResponse;
    private boolean running = false;

    public void loginNetworkRequest(final User user){
        running = true;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

        Call<UserResponse> call = iDaeClient.loginUser(user);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                System.out.println(response.raw());
                if (response.body().getStatus() == ErrorCodes.INCORRECT_LOGIN) {
                    networkResponse = new NetworkResponse(ErrorCodes.INCORRECT_LOGIN, response.body());
                } else if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(ErrorCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(ErrorCodes.FAILED_CONNECTION, null);
                running = false;
            }
        });
    }


    public void addUserNetworkRequest(final User user){
        running = true;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

        Call<UserResponse> call = iDaeClient.addUser(user);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                System.out.println(response.raw());
                if (response.body().getStatus() == ErrorCodes.EMAIL_NOT_AVAILABLE) {
                    networkResponse = new NetworkResponse(ErrorCodes.EMAIL_NOT_AVAILABLE, response.body());
                } else if (response.body().getStatus() == ErrorCodes.USER_NOT_AVAILABLE) {
                    networkResponse = new NetworkResponse(ErrorCodes.USER_NOT_AVAILABLE, response.body());
                } else if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(ErrorCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(ErrorCodes.FAILED_CONNECTION, null);
                running = false;
            }
        });
    }


    public void postEventNetworkRequest(final Event event) {
        running =  true;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

        Call<EventResponse> call = iDaeClient.post_event(event);

        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                System.out.println(response.raw());
                if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(ErrorCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(ErrorCodes.FAILED_CONNECTION, null);
                running =  false;
            }
        });
    }

    public boolean isRunning(){
        return running;
    }

    public NetworkResponse getNetworkResponse(){
        return networkResponse;
    }
}
