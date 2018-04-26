package paradise.ccclxix.projectparadise.Hosting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import iDaeAPI.IDaeClient;
import iDaeAPI.model.EventCreateRequest;
import iDaeAPI.model.EventCreateResponse;
import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.BuildConfig;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.LocationManager;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
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

    ApiClientFactory apiClientFactory;
    IDaeClient iDaeClient;
    EventCreateResponse eventCreateResponse;

    private LocationManager locationManager;
    private CredentialsManager credentialsManager;
    private EventManager eventManager;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private String lastLocationFormated;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        apiClientFactory = new ApiClientFactory();
        iDaeClient = apiClientFactory.build(IDaeClient.class);

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
        Thread loginUser = new Thread() {
            @Override
            public void run() {
                final EventCreateRequest eventCreateRequest = new EventCreateRequest();

                eventCreateRequest.setUsername(credentialsManager.getUsername());
                eventCreateRequest.setToken(credentialsManager.getToken());

                eventCreateRequest.setEventName(eventCreateName.getText().toString());
                eventCreateRequest.setPrivacy(getPrivacy());
                eventCreateRequest.setLatitude(locationManager.getLastLatitude(getApplicationContext()));
                eventCreateRequest.setLongitude(locationManager.getLastLongitude(getApplicationContext()));
                eventCreateRequest.setAgeTarget(getAgeTarget());

                eventCreateResponse = iDaeClient.idaeEventHostAddeventPost(eventCreateRequest);

                try {
                    super.run();
                    while (eventCreateResponse == null) {
                        sleep(39);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (eventCreateResponse.getStatus()){
                                case MessageCodes.OK:
                                    Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
                                    eventManager = new EventManager(getApplicationContext(), eventCreateResponse);
                                    intent.putExtra("source","event_created");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    CreateEventActivity.this.startActivity(intent);
                                    break;
                                case MessageCodes.INCORRECT_TOKEN:
                                    showSnackbar("You have been logged out.");
                                    try {
                                        sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intentOut = new Intent(CreateEventActivity.this, InitialAcitivity.class);
                                    intentOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    credentialsManager.clear();
                                    CreateEventActivity.this.startActivity(intentOut);
                                    break;
                                case MessageCodes.SERVER_ERROR:
                                    showSnackbar("Problem with connection please try again later.");
                                    break;
                            }
                            eventCreateButtonLaunch.clearAnimation();
                        }
                    });
                }
            }
        };
        loginUser.start();
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
