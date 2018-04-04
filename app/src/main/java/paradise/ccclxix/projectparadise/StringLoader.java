package paradise.ccclxix.projectparadise;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StringLoader extends AsyncTaskLoader<List<String>> {

    private List<String> cached;
    public static final String ACTION = "com.loaders.FORCE";

    public StringLoader(Context context){
        super(context);
    }

    @Override
    protected void onStartLoading(){

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter filter =  new IntentFilter(ACTION);
        manager.registerReceiver(broadcastReceiver, filter);

        if (cached == null){
            forceLoad();
        }else {
            super.deliverResult(cached);
        }
    }

    @Override
    public List<String> loadInBackground() {
        Log.d("TE","UPdated");
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        ArrayList<String> data = new ArrayList<>();
        data.add("helllo");
        data.add("bye");
        return data;
    }

    @Override
    public void deliverResult(List<String> data) {
        cached = data;
        super.deliverResult(data);
    }

    @Override
    public void onReset(){
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };
}
