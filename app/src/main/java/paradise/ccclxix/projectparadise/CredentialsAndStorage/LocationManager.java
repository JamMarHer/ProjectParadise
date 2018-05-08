package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Context.MODE_PRIVATE;

public class LocationManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String APP_MODE = "iDaeLocation";
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(APP_MODE, MODE_PRIVATE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

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

    public String getLastLatitude(Context context){
        return this.getLastFormatedLocation(context).split("___")[0];
    }

    public String getLastLongitude(Context context){
        return this.getLastFormatedLocation(context).split("___")[1];
    }


    public String getLastFormatedLocation(Context context) {
        this.context = context;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    System.out.println("Current:  Lat: "+ Double.toString(location.getLatitude())+", Lon: "+Double.toString(location.getLongitude()));
                    setLocation(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));

                }
            }
        });
        return getFormatedLocation();
    }


}
