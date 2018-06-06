package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.HashMap;


import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;

import static android.content.Context.MODE_PRIVATE;

public class WaveManager implements Manager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String TEMP_EVENT = "WAVE";
    private final String DESCRIPTION = "TODO";

    private HashMap<String, Object> event;
    private boolean working;


    public WaveManager(){
    }

    @Override
    public void initialize(Context context) {
        event = new HashMap<>();
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
    }

    public void logout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }


    public void updatePersonalTimein(Long time){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("personalTimeIn", time);
        editor.apply();
    }

    public void updateEventID(String eventID){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
    }

    public void updateWavePosts(String wave, Long numPosts){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(wave, numPosts);
        editor.apply();
    }

    public void updateEventName(String eventName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventName", eventName);
        editor.apply();
    }

    public void updateWaveLastUpdate(Long time){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putLong("time", time);
        editor.apply();
    }

    public String getEventID(){
        return  sharedPreferences.getString("eventID", null);
    }

    public String getEventName(){ return  sharedPreferences.getString("eventName", null);}

    public Long getWavePosts(String wave){ return  sharedPreferences.getLong(wave, -1);}

    public long getPersonalTimeIn(){
        return this.sharedPreferences.getLong("personalTimeIn", 0);
    }

    public long getWaveUpdateTime(String wave) { return  sharedPreferences.getLong(wave, -1);}




    public HashMap<String, Object> getEvent() {
        return event;
    }
}
