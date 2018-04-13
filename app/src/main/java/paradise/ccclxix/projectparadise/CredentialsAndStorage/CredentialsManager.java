package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class CredentialsManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String CREDENTIALS = "iDaeCredentials";

    public CredentialsManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
    }

    public void registrationSave(String username, String email, String token){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("token", token);
        editor.apply();

    }


    public String getUsername(){
        return sharedPreferences.getString("username",null);
    }

    public String getEmail(){
        return sharedPreferences.getString("email",null);
    }

    public String getToken(){
        return sharedPreferences.getString("token",null);
    }



    public void updateUsername(String username){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public void updateEmail(String email){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public void updateToken(String token){
        SharedPreferences.Editor editor= this.sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}

