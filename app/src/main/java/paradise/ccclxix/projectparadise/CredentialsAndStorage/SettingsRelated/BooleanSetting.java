package paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated;

import android.content.SharedPreferences;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsManager;

public class BooleanSetting implements Setting {

    private String name;
    private String description;
    private SharedPreferences sharedPreferences;
    private boolean value;

    public BooleanSetting(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public BooleanSetting(String name, String description, SharedPreferences sharedPreferences) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.sharedPreferences = sharedPreferences;
    }

    public void setValue(boolean value) {
        if (this.sharedPreferences != null){
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean(this.name, value);
            editor.apply();
        }
        this.value = value;
    }

    @Override
    public String getType() {
        return "BOOL";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public boolean getValue() {
        return this.sharedPreferences != null && this.sharedPreferences.getBoolean(this.name, false);
    }
}

