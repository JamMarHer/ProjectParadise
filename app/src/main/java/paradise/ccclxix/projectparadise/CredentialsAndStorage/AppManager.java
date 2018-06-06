package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;

public class AppManager implements Manager {


    private Map<String, Manager> managers = new HashMap<String, Manager>(){
        {
            put("waves", new WaveManager());
            put("credentials", new CredentialsManager());
            put("mode", new ModeManager());
            put("settings", new SettingsManager());
        }
    };;


    private static final String APP_MANAGER = "APP_MANAGER";
    // TODO
    private static final String DESCRIPTION = "TODO";
    private SharedPreferences appManagerSP;

    private boolean initialized = false;

    private Context  context;

    public AppManager(){

    }

    public void initialize(Context context){
        if (!initialized){
            this.context = context;
            this.appManagerSP = this.context.getSharedPreferences(APP_MANAGER, Context.MODE_PRIVATE);
            for (String managerKey : managers.keySet()){
                managers.get(managerKey).initialize(this.context);
            }
            initialized = true;
        }

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
