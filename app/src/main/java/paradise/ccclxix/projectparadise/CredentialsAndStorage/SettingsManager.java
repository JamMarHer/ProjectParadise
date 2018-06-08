package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.BooleanSetting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.StringSetting;
import paradise.ccclxix.projectparadise.utils.ManagersInfo;

import static android.content.Context.MODE_PRIVATE;

public class SettingsManager implements Manager {

    private Context context;
    private SharedPreferences sharedPreferences;

    public static final String PASSWORD_TYPE = "Account_Password";
    public static final String EMAIL_TYPE = "Account_Email";



    public static final ArrayList<String> PARENT_ORDER = new ArrayList<String>(){
        {
            add("Account");
            add("Notifications");
        }
    };

    private Map<String, Map<String, Setting>> settings = new HashMap<String, Map<String, Setting>>() {
        {
            put("Account", new HashMap<String, Setting>(){
                {
                    put("Account_Password", new StringSetting("Account_Password", "TODO"));
                    put("Account_Email", new StringSetting("Account_Email", "TODO"));
                }
            });
            put("Notifications", new HashMap<String, Setting>(){
                {
                    put("Notifications_All", new BooleanSetting("Notifications_All","TODO"));
                }
            });
        }
    };

    private boolean initialized = false;

    public SettingsManager(){
    }

    @Override
    public Manager initialize(Context context) {
        if (!initialized){
            this.context = context;
            this.sharedPreferences = this.context.getSharedPreferences(ManagersInfo.S_TYPE, MODE_PRIVATE);

            for (String settingTypeKey : settings.keySet()){
                for (String settingkey : settings.get(settingTypeKey).keySet())
                if(settings.get(settingTypeKey).get(settingkey).getType().equals("BOOL")){
                    BooleanSetting bool = new BooleanSetting(settingkey, "TODO", sharedPreferences);
                    settings.get(settingTypeKey).put(settingkey, bool);
                }else if (settings.get(settingTypeKey).get(settingkey).getType().equals("STR")){
                    StringSetting str = new StringSetting(settingkey, "TODO", sharedPreferences);
                    settings.get(settingTypeKey).put(settingkey, str);
                }
            }

            initialized = true;
        }
        return this;
    }

    // TODO some regular expressssssssssssssssssssssssssssssion.
    public static String getSettingParentType(String setting){
        if(setting.contains("_")){
            return setting.split("_")[0];
        }
        return "";
    }

    public static String getSettingChildType(String setting){
        if(setting.contains("_")){
            return setting.split("_")[1];
        }
        return "";
    }

    private boolean isValidSettingName(String setting){
        String parent = getSettingParentType(setting);
        String child = getSettingChildType(setting);
        return settings.containsKey(parent) && settings.get(parent).containsKey(child);
    }



    public Map<String, Map<String, Setting>> getSettings(){
        return settings;
    }

    @Override
    public void logout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public String getType() {
        return ManagersInfo.S_TYPE;
    }

    @Override
    public String getDescription() {
        return ManagersInfo.S_DESCRIPTION;
    }
}
