package paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces;

import android.content.SharedPreferences;

public interface Manager {


    public void initialize();
    public void logout();
    public String getType();
    public String getDescription();

}
