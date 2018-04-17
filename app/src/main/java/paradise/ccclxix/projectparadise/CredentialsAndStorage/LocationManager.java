package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LocationManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String APP_MODE = "iDaeLocation";


    public LocationManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(APP_MODE, MODE_PRIVATE);

    }

    public String getFormatedLocation(){
        return String.format("%s___%s", sharedPreferences.getString("lat",null), sharedPreferences.getString("lon",null));
    }

    public void  setLocation(String lat, String lon){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lat",lat);
        editor.putString("lon",lon);
        editor.apply();
    }


}
