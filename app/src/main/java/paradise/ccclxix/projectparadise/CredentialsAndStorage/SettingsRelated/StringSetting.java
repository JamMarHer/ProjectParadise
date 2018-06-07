package paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsManager;

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
            if(this.name.equals(SettingsManager.PASSWORD_TYPE) || this.name.equals(SettingsManager.EMAIL_TYPE)){
                Log.d("UPDATING_EMAIL_OR_PASS", "Incorrect method to update user pass or email.");
            }else{
                SharedPreferences.Editor editor = this.sharedPreferences.edit();
                editor.putString(this.name, value);
                editor.apply();
                this.value = value;
            }

        }

    }

    public void setValue(String email, String pass, final String newPass) {
        if (this.sharedPreferences != null){
            if(this.name.equals(SettingsManager.PASSWORD_TYPE) || this.name.equals(SettingsManager.EMAIL_TYPE)){

            }else{
                Log.d("UPDATING_SETTING", "Incorrect method to update setting.");
            }

        }

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
