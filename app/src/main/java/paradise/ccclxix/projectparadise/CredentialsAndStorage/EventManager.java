package paradise.ccclxix.projectparadise.CredentialsAndStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.HashMap;


import static android.content.Context.MODE_PRIVATE;

public class EventManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String TEMP_EVENT = "iDaeTempEvent";
    private HashMap<String, Object> event;
    private boolean working;


    public EventManager(Context context){
        event = new HashMap<>();
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(TEMP_EVENT, MODE_PRIVATE);
    }




    public void updatePersonalTimein(Long time){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("personalTimeIn", time);
        editor.apply();
    }

    public void updateEventID(String eventID){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eventID", eventID);
        editor.apply();
    }

    public String getEventID(){
        return  sharedPreferences.getString("eventID", null);
    }

    public long getPersonalTimeIn(){
        return this.sharedPreferences.getLong("personalTimeIn", 0);
    }

    public void updateEvent(String eventID){
        this.updateEventID(eventID);
        event.clear();
        this.getEvent();
    }


    public boolean addUser(String username, long timeIn){
        final boolean[] toReturn = {false};
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventDatabaseReference = database.getReference().child("events_us").child(this.getEventID()).child("attending");
        HashMap<String, HashMap<String, Long>> attending = new HashMap<>();
        HashMap<String, Long> in = new HashMap<>();
        in.put("in", timeIn);
        attending.put(username, in);
        eventDatabaseReference.setValue(attending).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    getEventID();
                    toReturn[0] = true;

                }else{
                    toReturn[0] =false;
                }

            }
        });
        return toReturn[0];

    }

    public boolean isWorking(){
        return working;
    }

    public HashMap<String, Object> getEvent() {
        return event;
    }

    public void loadEvent(){
        System.out.println(getEventID());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference =firebaseDatabase.getReference()
                .child("events_us")
                .child(getEventID());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event.put("name_event", dataSnapshot.child("name_event").getValue().toString());
                System.out.println(event.get("name_event"));
                event.put("event_id", getEventID());
                event.put("privacy", dataSnapshot.child("privacy").getValue().toString());
                event.put("latitude", dataSnapshot.child("latitude").getValue().toString());
                event.put("longitude", dataSnapshot.child("longitude").getValue().toString());
                event.put("age_target", dataSnapshot.child("age_target").getValue().toString());
                HashMap<String, HashMap<String, Long>> attending = new HashMap<>();
                HashMap<String, HashMap<String, Long>> attended = new HashMap<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attending").getChildren()) {
                    HashMap<String, Long> inOut = new HashMap<>();
                    inOut.put("in", Long.valueOf(dataSnapshot1.child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                    attending.put(dataSnapshot1.getKey(), inOut);
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attended").getChildren()) {
                    HashMap<String, Long> inOut = new HashMap<>();
                    inOut.put("in", Long.valueOf(dataSnapshot1.child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                    inOut.put("out", Long.valueOf(dataSnapshot1.child(dataSnapshot1.getKey()).child("out").getValue().toString()));
                    attended.put(dataSnapshot1.getKey(), inOut);
                }


                event.put("attended", attended);
                event.put("attending", attending);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

    }


}
