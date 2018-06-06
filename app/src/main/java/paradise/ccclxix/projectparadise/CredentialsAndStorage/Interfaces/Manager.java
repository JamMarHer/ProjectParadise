package paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces;

import android.content.Context;
import android.content.SharedPreferences;

public interface Manager {


    public void initialize(Context context);
    public void logout() throws Exception;
    public String getType();
    public String getDescription();

}
