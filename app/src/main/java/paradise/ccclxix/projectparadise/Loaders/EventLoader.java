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

    public EventLoader(Context context) {
        super(context);
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

        getData();
        try {
            Thread.sleep(1000);
        }catch (Exception e){

        }

        return data;
    }

    private void getData() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);
        Call<List<Event>> call = iDaeClient.get_events_nearme(getCurrentCoor());

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                System.out.println(response.raw());
                data.addAll(response.body());
                System.out.println(response.body().toString());
                runnig = false;
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                System.out.println(t.getMessage());

                runnig = false;
            }
        });
    }

    private String getCurrentCoor() {
        LocationManager lm = new LocationManager(getContext());
        return lm.getFormatedLocation();
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