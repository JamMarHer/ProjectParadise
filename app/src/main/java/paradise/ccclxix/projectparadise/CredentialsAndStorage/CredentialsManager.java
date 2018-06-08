package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;
import paradise.ccclxix.projectparadise.FirebaseBuilder;
import paradise.ccclxix.projectparadise.User;
import paradise.ccclxix.projectparadise.utils.ManagersInfo;

import static android.content.Context.MODE_PRIVATE;

public class CredentialsManager  implements Manager{


    private Context context;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseBuilder fb;

    private boolean initialized = false;


    private DataChangedListener listener;

    public interface DataChangedListener{
        public void onDataChanged(boolean changed);
    }

    public void setDataChangedListener(DataChangedListener listener){
        this.listener = listener;

    }

    public CredentialsManager(){

    }

    @Override
    public Manager initialize(Context context) {
        if (!initialized) {
            this.context = context;
            this.sharedPreferences = this.context.getSharedPreferences(ManagersInfo.C_TYPE, MODE_PRIVATE);
            this.fb = new FirebaseBuilder();
            this.mAuth = FirebaseAuth.getInstance();
            this.initialized = true;
            this.listener = null;
            updateCredentials();
        }
        return this;
    }

    @Override
    public void logout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void updateCredentials(){
        if(mAuth.getCurrentUser() != null){
            final DatabaseReference userDatabaseReference = fb.get_user();
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("username"))

                        updateUsername(dataSnapshot.child("username").getValue().toString());

                    if (dataSnapshot.hasChild("name"))
                        updateName(dataSnapshot.child("name").getValue().toString());

                    if (dataSnapshot.hasChild("status"))
                        updateStatus(dataSnapshot.child("status").getValue().toString());

                    if (dataSnapshot.hasChild("token"))
                        updateToken(dataSnapshot.child("token").getValue().toString());

                    if (dataSnapshot.hasChild("profile_picture"))
                        updateProfilePic(dataSnapshot.child("profile_picture").getValue().toString());

                    if (dataSnapshot.hasChild("waves") && dataSnapshot.child("waves").hasChild("in"))
                        updateNumWaves(String.valueOf(dataSnapshot.child("waves").child("in").getChildrenCount()));

                    if (dataSnapshot.hasChild("session_token")){
                        /*
                        if (!TextUtils.isEmpty(getSessionToken()) && !getSessionToken().equals(dataSnapshot.child("session_token"))){
                        }else {
                            final String sessionToken = String.valueOf(System.currentTimeMillis());
                            userDatabaseReference.child("session_token").setValue(sessionToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        updateSessionToken(sessionToken);
                                }
                            });
                            updateSessionToken(dataSnapshot.child("session_token").getValue().toString());
                        }
                        */


                    }

                    if (dataSnapshot.hasChild("echos"))
                        updateNumWaves(String.valueOf(dataSnapshot.child("echos").getChildrenCount()));
                    if (listener != null)
                        listener.onDataChanged(true); // <---- fire listener here

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference userMessages = fb.getMessages();
            userMessages.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren())
                        updateNumContacts(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public User getUser(){
        return new User(this.getEmail(), this.getToken());
    }

    public String getSessionToken(){
        return sharedPreferences.getString("session_token", "");
    }

    public String getUsername(){
        return sharedPreferences.getString("username","");
    }

    public String getName(){return  sharedPreferences.getString("name", "");}

    public String getStatus(){return  sharedPreferences.getString("bio", "");}

    public String getProfilePic(){return  sharedPreferences.getString("profile_picture", "");}

    public String getEmail(){
        if (mAuth.getCurrentUser() != null){
            return  mAuth.getCurrentUser().getEmail();
        }
        return "";
    }

    public String getToken(){
        return sharedPreferences.getString("token",null);
    }

    public String getNumEchos(){
        return sharedPreferences.getString("num_echos","");
    }

    public String getNumContacts(){
        return sharedPreferences.getString("num_contacts","");
    }

    public String getNumWaves(){
        return sharedPreferences.getString("num_waves","");
    }


    public void updateSessionToken(String token){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("session_token", token);
        editor.apply();
    }


    public void updateNumWaves(String waves){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("num_waves", waves);
        editor.apply();
    }

    public void updateNumEchos(String echos){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("num_echos", echos);
        editor.apply();
    }

    public void updateNumContacts(String contacts){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("num_contacts", contacts);
        editor.apply();
    }

    public void updateUsername(String username){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }


    public void updateProfilePic(String picURL){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("profile_picture", picURL);
        editor.apply();
    }

    public void updateName(String name){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public void updateStatus(String bio){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("bio", bio);
        editor.apply();
    }


    public void updateToken(String token){
        SharedPreferences.Editor editor= this.sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }



    @Override
    public String getType() {
        return ManagersInfo.C_TYPE;
    }

    @Override
    public String getDescription() {
        return ManagersInfo.C_DESCRIPTION;
    }


}

