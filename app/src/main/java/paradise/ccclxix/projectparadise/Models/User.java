package paradise.ccclxix.projectparadise.Models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class User {
    /*
    User Schema
    -------------------------
    name            :String
    email           :String
    phone           :String
    username        :String
    password        :String
    status          :String
    thumb_image     :String
    join            :Date
    waves           [List of :Wave]
    history         [List of :Action]
    */

    private String name;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String status;
    private String thumb_image;
    private Date join;
    private HashSet<Wave> waves;
    private ArrayList<Action> history;

    public User(String u){
        username = u;
    }

    public interface Callback {
        void onCallback(boolean value, String name);
    }

    private void setUsername(String new_username){
        if(new_username.length() < 4) return;

        usernameExist(new_username, new Callback(){
            @Override
            public void onCallback(boolean value, String new_username) {
                if(value){
                    return;
                }
                else{
                    // Username does not exist
                    username = new_username;
                }
            }
        });
    }

    private void setPassword(String new_password){
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}";
        Log.d("REGEX", Boolean.toString(new_password.matches(pattern)));
        boolean yes = new_password.matches(pattern);
        if(password.length() > 4 && yes) password = new_password;
    }

    private void usernameExist(final String username, final Callback callback){
        FirebaseBuilder firebase = new FirebaseBuilder();
        firebase.get("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    callback.onCallback(true, username);
                } else {
                    callback.onCallback(false, username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(false, username);
            }
        });
    }

    private String getUsername(){
        return username != null ? username : "";
    }

    public HashMap<String, String> render(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        //default
        map.put("status", "We lit");
        map.put("thumb_image", "default");
        return map;
    }

}
