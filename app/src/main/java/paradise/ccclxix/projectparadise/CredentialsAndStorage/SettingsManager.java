package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.BooleanSetting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.StringSetting;

import static android.content.Context.MODE_PRIVATE;

public class SettingsManager implements Manager {

    private Context context;
    private SharedPreferences sharedPreferences;
    public static final String TYPE = "SETTINGS";
    //TODO
    private final String DESCRIPTION = "TODO";

    public static final ArrayList<String> PARENT_ORDER = new ArrayList<String>(){
        {
            add("NOTIFICATIONS");
        }
    };

    private Map<String, Map<String, Setting>> settings = new HashMap<String, Map<String, Setting>>() {
        {
            put("NOTIFICATIONS", new HashMap<String, Setting>(){
                {
                    put("NOTIFICATIONS_ALL", new BooleanSetting("NOTIFICATIONS_ALL","TODO"));
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
            this.sharedPreferences = this.context.getSharedPreferences(TYPE, MODE_PRIVATE);

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


    public void updateBoolSetting(String name, boolean value) throws Exception{
        if (!isValidSettingName(name)){
            throw new Exception(String.format("Setting : %s. Does not exist.",name));
        }else{
            BooleanSetting bs = (BooleanSetting)settings.get(getSettingParentType(name)).get(getSettingChildType(name));
            bs.setValue(value);
        }
    }

    public void updateStringSetting(String name, String value) throws Exception{
        if (!isValidSettingName(name)){
            throw new Exception(String.format("Setting : %s. Does not exist.",name));
        }else{
            StringSetting ss = (StringSetting)settings.get(getSettingParentType(name)).get(getSettingChildType(name));
            ss.setValue(value);
        }
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
        return TYPE;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
