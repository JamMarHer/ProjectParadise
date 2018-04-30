package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import iDaeAPI.model.Event;

import static android.content.Context.MODE_PRIVATE;

public class EventManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String TEMP_EVENT = "iDaeTempEvent";


    public EventManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
    }

    public EventManager(Context context, Event event){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
        this.updateEvent(event);
    }


    public void updateEvent(Event event){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        System.out.println(event.toString());
        editor.putString("event", gson.toJson(event));
        editor.apply();
    }

    public Event getEvent(){
        Gson gson = new Gson();
        System.out.println(gson.fromJson(sharedPreferences.getString("event", null), Event.class));
        return gson.fromJson(sharedPreferences.getString("event", null), Event.class);
    }

}
