package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import paradise.ccclxix.projectparadise.User;

import static android.content.Context.MODE_PRIVATE;

public class CredentialsManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String CREDENTIALS = "iDaeCredentials";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;

    public CredentialsManager(Context context){
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        updateCredentials();
    }

    public void updateCredentials(){
        if(mAuth.getCurrentUser() != null){
            DatabaseReference userDatabaseReference = database.getReference().child("users").child(mAuth.getUid());
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    updateUsername(dataSnapshot.child("username").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        System.out.println(getUsername());
    }



    public void registrationSave(String username, String email, String token){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("token", token);
        editor.apply();

    }

    public User getUser(){
        return new User(this.getEmail(), this.getToken());
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean checkLoggedIn(){
        return !(this.getUsername()  == null || this.getEmail() == null || this.getToken() == null);
    }

    public String getUsername(){
        return sharedPreferences.getString("username",null);
    }

    public String getName(){return  sharedPreferences.getString("name", null);}

    public String getStatus(){return  sharedPreferences.getString("bio", null);}

    public String getProfilePic(){return  sharedPreferences.getString("profile_picture", null);}

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

