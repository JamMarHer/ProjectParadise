package paradise.ccclxix.projectparadise;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
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

    public DatabaseReference get(String... args){
        DatabaseReference db = database.getReference();
        for(String arg: args){
            db = db.child(arg);
        }
        return db;
    }

    public DatabaseReference get_user(String... args){
        DatabaseReference db = database.getReference().child("users").child(auth.getUid());
        for(String arg: args){
            db = db.child(arg);
        }
        return db;
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
