package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Manager;
import paradise.ccclxix.projectparadise.User;
import paradise.ccclxix.projectparadise.utils.ManagersInfo;

import static android.content.Context.MODE_PRIVATE;

public class CredentialsManager  implements Manager{


    private Context context;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;

    private boolean initialized = false;



    public CredentialsManager(){

    }

    @Override
    public Manager initialize(Context context) {
        if (!initialized){
            this.context = context;
            this.sharedPreferences = this.context.getSharedPreferences(ManagersInfo.C_TYPE, MODE_PRIVATE);
            mAuth = FirebaseAuth.getInstance();
            updateCredentials();
            initialized = true;
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
            DatabaseReference userDatabaseReference = database.getReference().child("users").child(mAuth.getUid());
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("name"))
                        updateName(dataSnapshot.child("name").getValue().toString());

                    if (dataSnapshot.hasChild("status"))
                        updateStatus(dataSnapshot.child("status").getValue().toString());

                    if (dataSnapshot.hasChild("token"))
                        updateToken(dataSnapshot.child("token").getValue().toString());

                    if (dataSnapshot.hasChild("profile_picture"))
                        updateProfilePic(dataSnapshot.child("profile_picture").getValue().toString());

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



    public String getUsername(){
        return sharedPreferences.getString("username",null);
    }

    public String getName(){return  sharedPreferences.getString("name", null);}

    public String getStatus(){return  sharedPreferences.getString("bio", null);}

    public String getProfilePic(){return  sharedPreferences.getString("profile_picture", null);}

    public String getEmail(){
        if (mAuth.getCurrentUser() != null){
            return  mAuth.getCurrentUser().getEmail();
        }
        return "";
    }

    public String getToken(){
        return sharedPreferences.getString("token",null);
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

