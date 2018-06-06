package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;

public class AppManager implements Manager {


    private Map<String, Manager> managers = new HashMap<String, Manager>(){
        {
            put(WaveManager.TYPE, new WaveManager());
            put("credentials", new CredentialsManager());
            put("mode", new ModeManager());
            put("settings", new SettingsManager());
        }
    };


    private static final String APP_MANAGER = "APP_MANAGER";
    // TODO
    private static final String DESCRIPTION = "TODO";
    private SharedPreferences appManagerSP;

    private boolean initialized = false;

    private Context  context;

    public AppManager(){

    }

    public Manager initialize(Context context){
        if (!initialized){
            this.context = context;
            this.appManagerSP = this.context.getSharedPreferences(APP_MANAGER, Context.MODE_PRIVATE);
            for (String managerKey : managers.keySet()){
                managers.get(managerKey).initialize(this.context);
            }
            initialized = true;
        }
        return this;
    }

    public CredentialsManager getCredentialM(){
        return (CredentialsManager)managers.get(CredentialsManager.TYPE);
    }

    public SettingsManager getSettingsM(){
        return (SettingsManager)managers.get(SettingsManager.TYPE);
    }

    public ModeManager getModeM(){
        return (ModeManager) managers.get(ModeManager.TYPE);
    }

    public WaveManager getWaveManager(){
        return (WaveManager) managers.get(WaveManager.TYPE);
    }

    @Override
    public void logout() throws Exception{
        if (!initialized)
            throw new Exception("You must initialize the manager");

        for (String managerKey : managers.keySet()){
            managers.get(managerKey).logout();
        }
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
