package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import iDaeAPI.model.EventAttenEnterRequest;
import iDaeAPI.model.EventAttenEnterResponse;
import iDaeAPI.model.EventCreateResponse;
import paradise.ccclxix.projectparadise.APIForms.Event;

import static android.content.Context.MODE_PRIVATE;

public class EventManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String TEMP_EVENT = "iDaeTempEvent";


    public EventManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
    }

    public EventManager(Context context, EventCreateResponse event){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
        this.updateEventHost(event);
        this.setHostMode();
    }

    public EventManager(Context context, EventAttenEnterResponse event){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
        this.updateEventAttendant(event);
        this.setAttendantMode();
    }

    public void setAttendantMode(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("eventMode", "attendant");
        editor.apply();
    }

    public void setHostMode(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("eventMode", "host");
        editor.apply();
    }

    public String getMode(){
        return  this.sharedPreferences.getString("eventMode", null);
    }

    public void updateEventAttendant(EventAttenEnterResponse event){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("eventAtten", gson.toJson(event));
        editor.apply();
    }

    public void updateEventHost(EventCreateResponse event){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString("eventHost", gson.toJson(event));
        editor.apply();
    }

    public EventCreateResponse getEventHost(){
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString("eventHost", null),EventCreateResponse.class);
    }

    public EventAttenEnterResponse getEventAttendant(){
        Gson gson = new Gson();
        return gson.fromJson(sharedPreferences.getString("eventAtten", null), EventAttenEnterResponse.class);
    }

}
