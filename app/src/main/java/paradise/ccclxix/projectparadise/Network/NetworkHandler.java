package paradise.ccclxix.projectparadise.Network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;


import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.APIForms.EventResponse;
import paradise.ccclxix.projectparadise.APIForms.User;
import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHandler {

    private NetworkResponse networkResponse;
    private boolean running = false;
    private boolean serverAlive;
    private boolean internetAlive;
    private final NetworkResponse NO_INTERNET = new NetworkResponse(MessageCodes.NO_INTERNET_CONNECTION);
    private Context context;

    public NetworkHandler(Context context){
        this.context = context;
    }

    public boolean internetConnection(){
        return isInternetAlive();
    }

    public boolean serverConnection(){ return  isServerAlive();}

    public void loginNetworkRequest(final User user){
        if (!isInternetAlive()){
            networkResponse = NO_INTERNET;
            return;
        }
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
                if (response.body().getStatus() == MessageCodes.INCORRECT_LOGIN) {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_LOGIN, response.body());
                } else if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(MessageCodes.FAILED_CONNECTION);
                running = false;
            }
        });
    }


    public void addUserNetworkRequest(final User user){
        if (!isInternetAlive()){
            networkResponse = NO_INTERNET;
            return;
        }
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
                if (response.body().getStatus() == MessageCodes.EMAIL_NOT_AVAILABLE) {
                    networkResponse = new NetworkResponse(MessageCodes.EMAIL_NOT_AVAILABLE, response.body());
                } else if (response.body().getStatus() == MessageCodes.USER_NOT_AVAILABLE) {
                    networkResponse = new NetworkResponse(MessageCodes.USER_NOT_AVAILABLE, response.body());
                } else if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(MessageCodes.FAILED_CONNECTION);
                running = false;
            }
        });
    }


    public void postEventNetworkRequest(final Event event) {
        if (!isInternetAlive()){
            networkResponse = NO_INTERNET;
            return;
        }
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
                if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_FORMAT, response.body());
                }
                running = false;
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(MessageCodes.FAILED_CONNECTION);
                running =  false;
            }
        });
    }

    public void getEventsNearNetworkRequest(final String currentCoordinates) {
        if (!isInternetAlive()){
            networkResponse = NO_INTERNET;
            return;
        }
        running = true;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);
        Call<List<Event>> call = iDaeClient.get_events_nearme(currentCoordinates);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                networkResponse = new NetworkResponse(100, response.body());
                running = false;
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                networkResponse =  new NetworkResponse(MessageCodes.FAILED_CONNECTION);
                running = false;
            }
        });
    }

    public void checkLoggedInNetworkRequest(final User user) {
        if (!isInternetAlive()){
            networkResponse = NO_INTERNET;
            return;
        }
        running = true;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

        Call<UserResponse> call = iDaeClient.check_token(user);


        call.enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.body().getStatus() == 100) {
                    networkResponse = new NetworkResponse(100, response.body());
                } else if (response.body().getStatus() == MessageCodes.INCORRECT_TOKEN) {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_TOKEN, response.body());
                } else {
                    networkResponse = new NetworkResponse(MessageCodes.INCORRECT_FORMAT);
                }
                running = false;
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                networkResponse = new NetworkResponse(MessageCodes.FAILED_CONNECTION);
                running = false;
            }
        });
    }

    public void announceInternetConnection(final Activity activity){
        Thread checkInternet = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    internetAlive = isInternetAlive();
                    while (internetAlive) {
                        sleep(1000);
                        internetAlive = isInternetAlive();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSnackbar("Internet connection reestablished.", activity);
                        }
                    });
                }
            }
        };
        checkInternet.start();
    }

    public void announceServerAlive(final Activity activity){
        Thread checkServer = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    serverAlive = isServerAlive();
                    while (!serverAlive) {
                        sleep(1000);
                        serverAlive = isServerAlive();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSnackbar("Server connection reestablished.", activity);
                        }
                    });
                }
            }
        };
        checkServer.start();
    }

    private void showSnackbar(final String message, Activity activity) {
        Snackbar.make(activity.findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }

    public boolean isRunning(){
        return this.running;
    }

    private boolean isInternetAlive(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }

    private boolean isServerAlive(){
        try {
            SocketAddress sockaddr = new InetSocketAddress(ConnectionUtils.MAIN_SERVER_IP, 80);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            return true;
        } catch(IOException e) {
            return false;
        }
    }

    public NetworkResponse getNetworkResponse(){
        return this.networkResponse;
    }
}
