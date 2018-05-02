package paradise.ccclxix.projectparadise.Hosting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.BuildConfig;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.LocationManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Models.Event;
import paradise.ccclxix.projectparadise.R;

public class CreateEventActivity extends AppCompatActivity {

    EditText eventCreateName;
    Switch eventCreateSwitchPrivate;
    CheckBox eventCreateCheckboxAll;
    CheckBox eventCreateCheckbox18;
    CheckBox eventCreateCheckbox21;
    TextView eventCreateTextPublic;
    TextView eventCreateTextPrivate;
    Button eventCreateButtonLaunch;

    Event eventCreateResponse;

    private LocationManager locationManager;
    private CredentialsManager credentialsManager;
    private EventManager eventManager;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private String lastLocationFormated;

    private FirebaseAuth firebaseAuth;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        eventCreateName =               findViewById(R.id.createEventName);
        eventCreateSwitchPrivate =      findViewById(R.id.createEventPrivate);
        eventCreateCheckboxAll =         findViewById(R.id.createEventAll);
        eventCreateCheckbox18 =          findViewById(R.id.createEvent18);
        eventCreateCheckbox21 =          findViewById(R.id.createEvent21);
        eventCreateTextPublic =         findViewById(R.id.createEventTextPublic);
        eventCreateTextPrivate =        findViewById(R.id.createEventTextPrivate);
        eventCreateButtonLaunch =       findViewById(R.id.createEventButtonLaunch);

        locationManager = new LocationManager(getApplicationContext());
        credentialsManager = new CredentialsManager(getApplicationContext());
        eventManager = new EventManager(getApplicationContext());


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        eventCreateButtonLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validInput()){
                    eventCreateButtonLaunch.setText("lit");
                    ResizeAnimation resizeAnimation = new ResizeAnimation(view, 260);
                    resizeAnimation.setRepeatCount(Animation.INFINITE);
                    resizeAnimation.setRepeatMode(Animation.REVERSE);
                    resizeAnimation.setDuration(369);
                    view.startAnimation(resizeAnimation);
                    addEvent();
                }
            }
        });

        eventCreateCheckboxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                eventCreateCheckbox18.setChecked(false);
                eventCreateCheckbox21.setChecked(false);
                if (b){
                    eventCreateCheckboxAll.setChecked(true);
                }
            }
        });
        eventCreateCheckbox18.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                eventCreateCheckboxAll.setChecked(false);
                eventCreateCheckbox21.setChecked(false);
                if (b){
                    eventCreateCheckbox18.setChecked(true);
                }
            }
        });
        eventCreateCheckbox21.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                eventCreateCheckbox18.setChecked(false);
                eventCreateCheckboxAll.setChecked(false);
                if (b){
                    eventCreateCheckbox21.setChecked(true);
                }
            }
        });
    }

    private String getAgeTarget(){
        if (eventCreateCheckboxAll.isChecked()){
            return "all";
        }
        if (eventCreateCheckbox18.isChecked()){
            return "18";
        }
        if (eventCreateCheckbox21.isChecked()){
            return "21";
        }
        return "all";
    }

    private String getPrivacy(){
        if (eventCreateSwitchPrivate.isChecked()){
            return "false";
        }else {
            return "true";
        }
    }

    private void addEvent(){

        if (firebaseAuth.getCurrentUser() != null){
            final HashMap<String, String> eventMap = new HashMap<>();
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String userID = current_user.getUid();
            final long timeStamp = System.currentTimeMillis();
            final String eventID = String.format("%s_%s", userID, String.valueOf(timeStamp));
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference eventDatabaseReference = database.getReference().child("events_us").child(
                    eventID);
            DatabaseReference userDatabaseReference = database.getReference().child("users").child(userID);
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventMap.put("host", dataSnapshot.child("username").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            eventMap.put("name_event", eventCreateName.getText().toString());
            eventMap.put("event_id", eventID);
            eventMap.put("privacy", getPrivacy());
            eventMap.put("latitude", locationManager.getLastLatitude(getApplicationContext()));
            eventMap.put("longitude", locationManager.getLastLongitude(getApplicationContext()));
            eventMap.put("active", "true");
            eventMap.put("age_target", getAgeTarget());
            eventDatabaseReference.setValue(eventMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference eventDatabaseReference = database.getReference().child("events_us").child(eventID).child("attending").child(credentialsManager.getUsername());
                        DatabaseReference eventDatabaseReference1 = database.getReference().child("events_us").child(eventID).child("attended");
                        HashMap<String, HashMap<String, Long>> attended = new HashMap<>();
                        HashMap<String, Long> in = new HashMap<>();
                        in.put("in", timeStamp);
                        eventDatabaseReference.setValue(in).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()){
                                    showSnackbar("Something went wrong");
                                }
                            }
                        });
                        eventDatabaseReference1.setValue(attended).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                                    intent.putExtra("source", "event_created");

                                    eventManager.updateEventID(eventID);
                                    eventManager.updatePersonalTimein(timeStamp);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });


                    }else {
                        showSnackbar("Something went wrong creating your event.");
                        eventCreateButtonLaunch.clearAnimation();
                    }
                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            lastLocationFormated = locationManager.getLastFormatedLocation(getApplicationContext());
        }
    }
    private boolean validInput(){
        if (!checkName()){
            eventCreateName.requestFocus();
            eventCreateName.setError("Missing name for the event.");
            return false;
        }else if (!eventCreateCheckboxAll.isChecked() && !eventCreateCheckbox18.isChecked() && !eventCreateCheckbox21.isChecked()){
            showSnackbar("Please select a age target.");
            return false;
        }
        return  true;
    }

    private boolean checkName(){
        return !eventCreateName.getText().toString().equals("");
    }



    // TODO properly integrate the new snack bar.
    private void showSnackbar(final String message) {
        Snackbar.make(findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                lastLocationFormated = locationManager.getLastFormatedLocation(getApplicationContext());
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.notice_location_needed, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(CreateEventActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.notice_location_needed, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }
}
