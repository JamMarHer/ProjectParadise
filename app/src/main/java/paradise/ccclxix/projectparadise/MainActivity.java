package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Fragments.HomeFragment;
import paradise.ccclxix.projectparadise.Fragments.WavesFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment currentFragment;
    private HolderFragment homeFragment;
    private HolderFragment musicFragment;
    private HolderFragment sharesFragment;
    private AppModeManager appModeManager;


    EventManager eventManager;
    CredentialsManager credentialsManager;

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        appModeManager = new AppModeManager(getApplicationContext());
        Intent  intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")){
            appModeManager.setModeToExplore();
            showSnackbar("Welcome fam :)", false, false);
            loadExploreMode();
        }else if (source.equals("event_created")) {
            appModeManager.setModeToHost();
            invalidateOptionsMenu();
            loadHostMode();
        }else if (source.equals("qr_code_scanned")) {
            appModeManager.setModeToAttendant();
            loadAttendantMode();
        }else if (source.equals("joined_event")) {
            appModeManager.setModeToAttendant();
            loadAttendantMode();
        }else if (source.equals("login")){
            appModeManager.setModeToExplore();
            loadExploreMode();
        }else if (source.equals("logged_in")){
            if (appModeManager.getMode().equals("host")){
                loadHostMode();
            }else if (appModeManager.getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
        }else if (source.equals("logged_in_no_internet")){
            // TODO constant check to get internet going.
            if (appModeManager.getMode().equals("host")){
                loadHostMode();
            }else if (appModeManager.getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
            showSnackbar("Working without internet. Trying to reconnect.", false, false);
        } else if (source.equals("logged_in_server_problem")){
            // TODO constant check to get server going.
            if (appModeManager.getMode().equals("host")){
                loadHostMode();
            }else if (appModeManager.getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
            showSnackbar("Server didn't respond. Trying to communicate.", false, false);
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        credentialsManager = new CredentialsManager(getApplicationContext());
        eventManager = new EventManager(getApplicationContext());

        loadAllFragments();
        fragmentToShow(homeFragment, musicFragment, sharesFragment);
        navigation.setSelectedItemId(R.id.navigation_home);



    }


    private void showSnackbar(final String message, boolean fireEmoji, boolean coolEmoji) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        if (fireEmoji) {
            snackbar.setIconLeft(R.drawable.fire_emoji, 24);
        }
        else if (coolEmoji) {
            snackbar.setIconLeft(R.drawable.cool, 24);
        }
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void loadExploreMode(){
        homeFragment =  new HomeFragment();
        musicFragment = new WavesFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadHostMode(){
        showSnackbar("  You are now hosting.", true, false);
        homeFragment =  new HomeFragment();
        musicFragment = new WavesFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadAttendantMode(){
        homeFragment =  new HomeFragment();
        musicFragment = new WavesFragment();
        sharesFragment = new SharesFragment();
    }

    public void createEvent(View view){
        Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void joinEvent(View view){
        Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
        MainActivity.this.startActivity(intent);
    }


    private void loadAllFragments(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, musicFragment)
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container, sharesFragment)
                .commit();
    }

    private boolean fragmentToShow(Fragment toShow, Fragment toHide, Fragment toHide2){
        if (toShow != null & toHide != null & toHide2 != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(toShow)
                    .hide(toHide)
                    .hide(toHide2)
                    .commit();
            return true;
        }else{
            return false;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.navigation_home:
                return fragmentToShow(homeFragment, sharesFragment, musicFragment);
            case R.id.navigation_music:
                return fragmentToShow(musicFragment, homeFragment, sharesFragment);
            case R.id.navigation_shares:
                return fragmentToShow(sharesFragment, musicFragment, homeFragment);
        }
        return false;
    }

    private void leaveHostEvent(){
        if (mAuth.getCurrentUser() != null){
            credentialsManager.updateCredentials();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(eventManager.getEventID())
                    .child("attending")
                    .child(mAuth.getUid());
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference =firebaseDatabase.getReference()
                                .child("events_us")
                                .child(eventManager.getEventID())
                                .child("attendend")
                                .child(mAuth.getUid());
                        HashMap<String, Long>  inOut= new HashMap<>();
                        inOut.put("in", eventManager.getPersonalTimeIn());
                        inOut.put("out", System.currentTimeMillis());
                        databaseReference.setValue(inOut).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    appModeManager.setModeToExplore();
                                    Intent intent = new Intent(MainActivity.this, InitialAcitivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }else {
                        showSnackbar("Something went wrong", false, false);
                    }
                }
            });
        }
    }


}
