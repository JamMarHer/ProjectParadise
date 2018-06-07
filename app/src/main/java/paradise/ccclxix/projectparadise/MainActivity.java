package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.auth.FirebaseAuth;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Fragments.ExploreFragment;
import paradise.ccclxix.projectparadise.Fragments.PersonalFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveFragment;
import paradise.ccclxix.projectparadise.Fragments.ChatFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.Icons;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment personalFragment;
    private HolderFragment waveFragment;
    private HolderFragment exploreFragment;
    private HolderFragment chatFragment;


    AppManager appManager;

    private Toolbar mainToolbar;
    private FirebaseBuilder firebase;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        appManager = (AppManager) new AppManager().initialize(getApplicationContext());

        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        backButton.setVisibility(View.INVISIBLE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        Intent  intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")){
            appManager.getModeM().setModeToExplore();
            showSnackbar("Welcome fam :)", Icons.COOL);
            loadExploreMode();
        }else if (source.equals("event_created")) {
            appManager.getModeM().setModeToHost();
            invalidateOptionsMenu();
            loadHostMode();
        }else if (source.equals("qr_code_scanned")) {
            appManager.getModeM().setModeToAttendant();
            loadAttendantMode();
        }else if (source.equals("joined_event")) {
            appManager.getModeM().setModeToAttendant();
            loadAttendantMode();
            showSnackbar(" You are now riding: "+ appManager.getWaveM().getEventName(), Icons.COOL);
        }else if (source.equals("login")){
            appManager.getModeM().setModeToExplore();
            loadExploreMode();
        }else if (source.equals("logged_in")){
            if (appManager.getModeM().getMode().equals("host")){
                loadHostMode();
            }else if (appManager.getModeM().getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
        }else if (source.equals("logged_in_no_internet")){
            // TODO constant check to get internet going.
            if (appManager.getModeM().getMode().equals("host")){
                loadHostMode();
            }else if (appManager.getModeM().getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
            showSnackbar("Working without internet. Trying to reconnect.", Icons.POOP);
        } else if (source.equals("logged_in_server_problem")){
            // TODO constant check to get server going.
            if (appManager.getModeM().getMode().equals("host")){
                loadHostMode();
            }else if (appManager.getModeM().getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
            showSnackbar("Server didn't respond. Trying to communicate.", Icons.POOP);
        }

        if(source.equals("postAdded")){
            loadAttendantMode();
            loadAllFragments();
            fragmentToShow(waveFragment, personalFragment, exploreFragment, chatFragment);
            navigation.setSelectedItemId(R.id.navigation_wave);
        }else {
            loadAllFragments();
            fragmentToShow(personalFragment, waveFragment, exploreFragment, chatFragment);
            navigation.setSelectedItemId(R.id.navigation_personal);
        }





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

    private void showSnackbar(final String message, int icon) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(icon, 24);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void loadExploreMode(){
        personalFragment =  new PersonalFragment();
        waveFragment = new WaveFragment();
        exploreFragment = new ExploreFragment();
        chatFragment = new ChatFragment();
    }

    public void loadHostMode(){
        showSnackbar("  You are now hosting.", Icons.FIRE);
        personalFragment =  new PersonalFragment();
        waveFragment = new WaveFragment();
        exploreFragment = new ExploreFragment();
        chatFragment = new ChatFragment();
    }

    public void loadAttendantMode(){
        personalFragment =  new PersonalFragment();
        waveFragment = new WaveFragment();
        exploreFragment = new ExploreFragment();
        chatFragment = new ChatFragment();
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
                .add(R.id.fragment_container, personalFragment)
                .add(R.id.fragment_container, waveFragment)
                .add(R.id.fragment_container, exploreFragment)
                .add(R.id.fragment_container, chatFragment)
                .commit();
    }

    private boolean fragmentToShow(Fragment toShow, Fragment toHide, Fragment toHide2, Fragment toHide3){
        if (toShow != null & toHide != null & toHide2 != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(toShow)
                    .hide(toHide)
                    .hide(toHide2)
                    .hide(toHide3)
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
                return fragmentToShow(personalFragment, waveFragment, exploreFragment, chatFragment);
            case R.id.navigation_wave:
                return fragmentToShow(waveFragment, personalFragment, exploreFragment, chatFragment);
            case R.id.navigation_explore:
                return fragmentToShow(exploreFragment, personalFragment, waveFragment, chatFragment);
            case R.id.navigation_chat:
                return fragmentToShow(chatFragment, personalFragment, waveFragment, exploreFragment);
        }
        return false;
    }



}
