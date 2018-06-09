package paradise.ccclxix.projectparadise;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class TokenAuthentication  extends FirebaseInstanceIdService{

    FirebaseBuilder firebase = new FirebaseBuilder();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        firebase = new FirebaseBuilder();
        updateToken(refreshedToken);
    }

    private void updateToken(String newToken){
        CredentialsManager credentialsManager =  new CredentialsManager();
        credentialsManager.initialize(getApplicationContext());
        if (firebase.auth().getCurrentUser() != null && firebase.auth_id() != null){
            DatabaseReference eventDatabaseReference = firebase.get("users", firebase.auth_id(), "token");
            firebase.setValue(eventDatabaseReference, newToken,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("UPDATED_TOKEN", "New token has been added to db.");
                            }
                        }
                    }
            );
        }
        credentialsManager.updateToken(newToken);
    }
}
