package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;

public class AppManager implements Manager {

    private WaveManager wm;
    private CredentialsManager cm;
    private ModeManager mm;
    private SettingsManager sm;


    private static final String APP_MANAGER = "APP_MANAGER";
    // TODO
    private static final String DESCRIPTION = "TODO";
    private SharedPreferences appManagerSP;

    private Context  context;

    public AppManager(Context context){
        this.context = context;
        this.appManagerSP = this.context.getSharedPreferences(APP_MANAGER, Context.MODE_PRIVATE);
    }

    public void initialize(){

        if (wm == null){
            wm = new WaveManager(this.context);
            wm.initialize();
        }
        if (cm == null){
            cm = new CredentialsManager(this.context);
            cm.initialize();
        }
        if (mm == null){
            mm = new ModeManager(this.context);
            mm.initialize();
        }
        if (sm == null){
            sm = new SettingsManager(this.context);
            sm.initialize();
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public String getType() {
        return APP_MANAGER;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
