package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;
import paradise.ccclxix.projectparadise.utils.ManagersInfo;

public class AppManager implements Manager{

    private Map<String, Manager> managers = new HashMap<String, Manager>(){
        {
            put(ManagersInfo.W_TYPE, new WaveManager());
            put(ManagersInfo.C_TYPE, new CredentialsManager());
            put(ManagersInfo.M_TYPE, new ModeManager());
            put(ManagersInfo.S_TYPE, new SettingsManager());
            put(ManagersInfo.L_TYPE, new LocationManager());
        }
    };



    private static final String TYPE = "APP_MANAGER";
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
            this.appManagerSP = this.context.getSharedPreferences(TYPE, Context.MODE_PRIVATE);
            for (String managerKey : managers.keySet()){
                managers.get(managerKey).initialize(this.context);
            }
            initialized = true;

        }
        return this;
    }

    public CredentialsManager getCredentialM(){
        return (CredentialsManager)managers.get(ManagersInfo.C_TYPE);
    }

    public SettingsManager getSettingsM(){
        return (SettingsManager)managers.get(ManagersInfo.S_TYPE);
    }

    public ModeManager getModeM(){
        return (ModeManager) managers.get(ManagersInfo.M_TYPE);
    }

    public WaveManager getWaveM(){
        return (WaveManager) managers.get(ManagersInfo.W_TYPE);
    }

    public LocationManager getLocationM(){
        return (LocationManager) managers.get(ManagersInfo.L_TYPE);
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
        return TYPE;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
