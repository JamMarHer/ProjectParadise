package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.nio.file.Path;

import static android.content.Context.MODE_PRIVATE;

public class AppModeManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String APP_MODE = "iDaeAppMode";


    public AppModeManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(APP_MODE, MODE_PRIVATE);

    }

    public String getMode(){ return sharedPreferences.getString("mode","explore"); }

    public void  setModeToExplore(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mode","explore");
        editor.apply();
    }

    public void  setModeToHost(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mode","host");
        editor.apply();
    }

    public void  setModeToAttendant(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mode","attendant");
        editor.apply();
    }

    public boolean isHostingMode(){
        return this.getMode().equals("host");
    }
    public boolean isExploreMode(){
        return this.getMode().equals("explore");
    }
    public boolean isAttendantMode(){
        return this.getMode().equals("attendant");
    }
}
