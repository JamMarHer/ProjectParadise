package paradise.ccclxix.projectparadise.Loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.ErrorCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.LocationManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Network.NetworkHandler;
import paradise.ccclxix.projectparadise.Network.NetworkResponse;
import paradise.ccclxix.projectparadise.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventLoader extends AsyncTaskLoader<List<Event>> {

    private List<Event> cached;
    final private  List<Event> data = new ArrayList<>();
    public boolean runnig = false;
    public static final String ACTION = "com.loaders.FORCE";
    private NetworkHandler networkHandler;
    private LocationManager locationManager;

    public EventLoader(Context context) {
        super(context);
        networkHandler = new NetworkHandler();
        locationManager = new LocationManager(context);
    }

    @Override
    protected void onStartLoading() {

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter filter = new IntentFilter(ACTION);
        manager.registerReceiver(broadcastReceiver, filter);

        if (cached == null) {
            forceLoad();
        } else {
            super.deliverResult(cached);
        }
    }

    @Override
    public List<Event> loadInBackground() {
        Log.d("TE", "UPdated");

        networkHandler.getEventsNearNetworkRequest(locationManager.getLastFormatedLocation(getContext()));
        Thread getEvents = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    while (networkHandler.isRunning()) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    NetworkResponse networkResponse = networkHandler.getNetworkResponse();
                    switch (networkResponse.getStatus()){
                        case 100:
                            data.addAll(networkResponse.getListEvents());
                            break;
                        case ErrorCodes.FAILED_CONNECTION:
                            Toast.makeText(getContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            }
        };

        getEvents.start();
        try {
            getEvents.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void deliverResult(List<Event> data) {
        cached = data;
        super.deliverResult(data);
    }

    @Override
    public void onReset() {
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };
}