package paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated;

import android.content.SharedPreferences;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;

public class StringSetting implements Setting {

    private String name;
    private String description;
    private String value;
    private SharedPreferences sharedPreferences;

    public StringSetting(String name, String description){
        this.name = name;
        this.description = description;
    }

    public StringSetting(String name, String description, SharedPreferences sharedPreferences){
        this.name = name;
        this.description = description;
        this.value = value;
        this.sharedPreferences = sharedPreferences;
    }

    // TODO Maybe throw an exception.
    public String getValue(){
        if (this.sharedPreferences != null){
            return sharedPreferences.getString(this.name, "");
        }
        return "Not Initialized";
    }

    public void setValue(String value) {
        if (this.sharedPreferences != null){
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(this.name, value);
            editor.apply();
        }
        this.value = value;
    }

    @Override
    public String getType() {
        return "STR";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
