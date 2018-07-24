package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.Fragments.ExploreFragment;
import paradise.ccclxix.projectparadise.Fragments.PersonalFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveFragment;
import paradise.ccclxix.projectparadise.Fragments.ChatFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.Registration.WelcomeToParadiseActivity;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment personalFragment;
    private HolderFragment waveFragment;
    private HolderFragment exploreFragment;


    AppManager appManager;

    private Toolbar mainToolbar;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    private SnackBar snackbar = new SnackBar();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snackbar = new SnackBar();

        appManager = (AppManager) new AppManager().initialize(getApplicationContext());


        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        backButton.setVisibility(View.INVISIBLE);

        Intent testing = new Intent(MainActivity.this, WelcomeToParadiseActivity.class);
        startActivity(testing);
        finish();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")) {
            appManager.getModeM().setModeToExplore();
            snackbar.showEmojiBar(findViewById(android.R.id.content), "Welcome fam :)", Icons.COOL);
            loadFragments();
        } else if (source.equals("event_created")) {
            appManager.getModeM().setModeToHost();
            invalidateOptionsMenu();
            loadFragments();
        } else if (source.equals("qr_code_scanned")) {
            appManager.getModeM().setModeToAttendant();
            loadFragments();
        } else if (source.equals("joined_event")) {
            appManager.getModeM().setModeToAttendant();
            loadFragments();
            snackbar.showEmojiBar(findViewById(android.R.id.content), " You are now riding: " + appManager.getWaveM().getEventName(), Icons.COOL);
        } else if (source.equals("login")) {
            appManager.getModeM().setModeToExplore();
            loadFragments();
        } else if (source.equals("logged_in")) {
            if (appManager.getModeM().getMode().equals("host")) {
                loadFragments();
            } else if (appManager.getModeM().getMode().equals("attendant")) {
                loadFragments();
            } else {
                loadFragments();
            }
        } else if (source.equals("logged_in_no_internet")) {
            // TODO constant check to get internet going.
            if (appManager.getModeM().getMode().equals("host")) {
                loadFragments();
            } else if (appManager.getModeM().getMode().equals("attendant")) {
                loadFragments();
            } else {
                loadFragments();
            }
            snackbar.showEmojiBar(findViewById(android.R.id.content), "Working without internet. Trying to reconnect.", Icons.POOP);
        } else if (source.equals("logged_in_server_problem")) {
            // TODO constant check to get server going.
            if (appManager.getModeM().getMode().equals("host")) {
                loadFragments();
            } else if (appManager.getModeM().getMode().equals("attendant")) {
                loadFragments();
            } else {
                loadFragments();
            }
            snackbar.showEmojiBar(findViewById(android.R.id.content), "Server didn't respond. Trying to communicate.", Icons.POOP);
        }

        loadFragments();
        addAllFragments();
        fragmentToShow(waveFragment, personalFragment, exploreFragment);
        navigation.setSelectedItemId(R.id.navigation_wave);



        toolbar.findViewById(R.id.main_settings).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                    MainActivity.this.startActivity(intent1);
                }
                return true;
            }

        });

    }


    public AppManager getAppManager() {
        return appManager;
    }

    public void loadFragments(){
        personalFragment =  new PersonalFragment();
        waveFragment = new WaveFragment();
        exploreFragment = new ExploreFragment();
    }


    private void addAllFragments(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, personalFragment)
                .add(R.id.fragment_container, waveFragment)
                .add(R.id.fragment_container, exploreFragment)
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
            case R.id.navigation_personal:
                return fragmentToShow(personalFragment, waveFragment, exploreFragment);
            case R.id.navigation_wave:
                return fragmentToShow(waveFragment, personalFragment, exploreFragment);
            case R.id.navigation_explore:
                return fragmentToShow(exploreFragment, personalFragment, waveFragment);
        }
        return false;
    }



}
