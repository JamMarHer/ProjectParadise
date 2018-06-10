package paradise.ccclxix.projectparadise.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FirebaseBuilder {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public FirebaseBuilder(){
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return this.auth().getCurrentUser();
    }

    public DatabaseReference get(String... args){
        DatabaseReference db = database.getReference();
        return this.getDataHelper(db, args);
    }

    public DatabaseReference get_user_authId(String... args){
        DatabaseReference db = this.get("users", this.auth_id());
        return this.getDataHelper(db, args);
    }

    public DatabaseReference get_user(String... args){
        DatabaseReference db = this.get("users");
        return this.getDataHelper(db, args);
    }

    public String auth_id(){
        return this.auth().getUid();
    }

    public DatabaseReference getMessages(String... args){
        DatabaseReference db = this.get("messages", this.auth_id());
        return this.getDataHelper(db, args);
    }

    public DatabaseReference getEvents_authId(String... args){
        DatabaseReference db = this.get("events_us", this.auth_id());
        return this.getDataHelper(db, args);
    }

    public DatabaseReference getEvents(String... args){
        DatabaseReference db = this.get("events_us");
        return this.getDataHelper(db, args);
    }

    private DatabaseReference getDataHelper(DatabaseReference db, String... args){
        for(String arg: args){
            db = db.child(arg);
        }
        return db;
    }

    public DatabaseReference getDatabase(){
        return database.getReference();
    }

    public FirebaseAuth auth(){
        return auth;
    }

    public void setValue(DatabaseReference df, HashMap<String,String> value, OnCompleteListener<Void> listener){
        df.setValue(value).addOnCompleteListener(listener);
    }

    public void setValue(DatabaseReference df, String value, OnCompleteListener<Void> listener){
        df.setValue(value).addOnCompleteListener(listener);
    }
}
