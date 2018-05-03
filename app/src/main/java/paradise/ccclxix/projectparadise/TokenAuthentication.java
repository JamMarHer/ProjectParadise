package paradise.ccclxix.projectparadise;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;

public class TokenAuthentication  extends FirebaseInstanceIdService{

    FirebaseAuth firebaseAuth;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        System.out.println("TOKENNNNNN");
        Log.d("NEW_TOKEN", refreshedToken);
        updateToken(refreshedToken);
    }

    private void updateToken(String newToken){
        CredentialsManager credentialsManager =  new CredentialsManager(getApplicationContext());
        firebaseAuth =  FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getUid() != null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference eventDatabaseReference = database.getReference().child("users").child(firebaseAuth.getUid()).child("token");
            eventDatabaseReference.setValue(newToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d("UPDATED_TOKEN", "New token has been added to db.");
                    }
                }
            });
        }
        credentialsManager.updateToken(newToken);
    }
}
