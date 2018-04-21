package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import paradise.ccclxix.projectparadise.APIForms.Event;

import static android.content.Context.MODE_PRIVATE;

public class EventManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String TEMP_EVENT = "iDaeTempEvent";


    public Event getEvent(){
        return new Event(this.getToken(),this.getID(),this.getEmail(), this.getName(), this.getPrivacy(), this.getLatitude(), this.getLongitude());
    }

    //TODO expand on reason to let the user know
    public boolean checkValidEvent(){
        if(this.getName() == null){
            Log.d("EVENT_INVALID: ", "NAME");
            return false;
        } else if(this.getEmail() == null){
            Log.d("EVENT_INVALID: ", "EMAIL");
            return false;
        } else if(this.getPrivacy() == null){
            Log.d("EVENT_INVALID: ", "PRIVACY");
            return false;
        } else if(this.getLatitude() == null){
            Log.d("EVENT_INVALID: ", "LATITUDE");
            return false;
        } else if(this.getLongitude() == null){
            Log.d("EVENT_INVALID: ", "LONGITUDE");
            return false;
        }
        return true;
    }

    public EventManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
    }

    public void clear(){
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void updateLocation(String location){
        this.updateLocation(location.split("___")[0],location.split("___")[1]);
    }

    public void updateLocation(String latitude, String longitude){
        this.updateLatitude(latitude);
        this.updateLongitude(longitude);
    }

    public void updateLatitude(String latitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", latitude);
        editor.apply();
    }

    public void updateLongitude(String longitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("longitude", longitude);
        editor.apply();
    }

    public void updateEmail(String email){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public void updateName(String name){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        if (name.equals("")){
            return;
        }
        editor.putString("name", name);
        editor.apply();
    }

    public void updateID(String id){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public void updatePrivacy(String privacy){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("privacy", privacy);
        editor.apply();
    }

    public void updateToken(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public String getEmail(){
        return sharedPreferences.getString("email", null);
    }

    public String getName(){
        return sharedPreferences.getString("name",null);
    }

    public String getID(){
        return sharedPreferences.getString("id",null);
    }

    public String getPrivacy(){
        return sharedPreferences.getString("privacy","true");
    }

    public String getLatitude(){ return  sharedPreferences.getString("latitude", null);}

    public String getLongitude(){ return  sharedPreferences.getString("longitude", null);}

    public String getToken(){ return  sharedPreferences.getString("token", null);}

}
