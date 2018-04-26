package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;

import iDaeAPI.IDaeClient;
import iDaeAPI.model.EventAttenEnterRequest;
import iDaeAPI.model.EventAttenEnterResponse;
import iDaeAPI.model.EventAttenLeaveRequest;
import iDaeAPI.model.EventAttenLeaveResponse;
import iDaeAPI.model.EventHostLeaveRequest;
import iDaeAPI.model.EventHostLeaveResponse;
import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.Attending.JoiningEventActivity;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Fragments.HomeAttendantFragment;
import paradise.ccclxix.projectparadise.Fragments.HomeFragment;
import paradise.ccclxix.projectparadise.Fragments.HomeHostingFragment;
import paradise.ccclxix.projectparadise.Fragments.MusicFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.Network.NetworkHandler;
import paradise.ccclxix.projectparadise.Network.NetworkResponse;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private LoaderAdapter loaderAdapter;
    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment currentFragment;
    private HolderFragment homeFragment;
    private HolderFragment musicFragment;
    private HolderFragment sharesFragment;
    private AppModeManager appModeManager;


    ApiClientFactory apiClientFactory;
    IDaeClient iDaeClient;

    EventHostLeaveResponse eventHostLeaveResponse;
    EventAttenEnterResponse eventAttenEnterResponse;
    EventAttenLeaveResponse eventAttenLeaveResponse;

    EventManager eventManager;
    CredentialsManager credentialsManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appModeManager = new AppModeManager(getApplicationContext());
        Intent  intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")){
            appModeManager.setModeToExplore();
            showSnackbar("Welcome fam :)");
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
            showSnackbar("Working without internet. Trying to reconnect.");
        } else if (source.equals("logged_in_server_problem")){
            // TODO constant check to get server going.
            if (appModeManager.getMode().equals("host")){
                loadHostMode();
            }else if (appModeManager.getMode().equals("attendant")){
                loadAttendantMode();
            }else{
                loadExploreMode();
            }
            showSnackbar("Server didn't respond. Trying to communicate.");
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        credentialsManager = new CredentialsManager(getApplicationContext());
        eventManager = new EventManager(getApplicationContext());

        apiClientFactory = new ApiClientFactory();
        iDaeClient = apiClientFactory.build(IDaeClient.class);

        loadAllFragments();
        fragmentToShow(homeFragment, musicFragment, sharesFragment);

    }


    private void showSnackbar(final String message) {
        Snackbar.make(findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out_hosting:
                appModeManager.setModeToExplore();

                leaveHostEvent();
                return true;
            case R.id.log_out_attending:
                appModeManager.setModeToExplore();
                leaveAttendantEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (appModeManager.isHostingMode()){
            inflater.inflate(R.menu.menu_hosting, menu);
            return true;
        }else if(appModeManager.isAttendantMode()){
            inflater.inflate(R.menu.menu_attending, menu);
            return true;
        }
        return false;
    }


    public void loadExploreMode(){
        homeFragment =  new HomeFragment();
        musicFragment = new MusicFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadHostMode(){
        homeFragment =  new HomeHostingFragment();
        musicFragment = new MusicFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadAttendantMode(){
        homeFragment =  new HomeAttendantFragment();
        musicFragment = new MusicFragment();
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
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container, musicFragment)
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
        //TODO come back here when the replacing happends, the cache is also lost.

        switch (item.getItemId()){
            case R.id.navigation_home:
                return fragmentToShow(homeFragment, musicFragment, sharesFragment);
            case R.id.navigation_music:
                return fragmentToShow(musicFragment, sharesFragment, homeFragment);
            case R.id.navigation_shares:
                return fragmentToShow(sharesFragment, musicFragment, homeFragment);
        }
        return false;
    }

    private void leaveHostEvent(){

        final EventHostLeaveRequest eventHostLeaveRequest = new EventHostLeaveRequest();
        eventHostLeaveRequest.setEventID(eventManager.getEventHost().getEventID());
        eventHostLeaveRequest.setToken(credentialsManager.getToken());
        eventHostLeaveRequest.setUsername(credentialsManager.getUsername());

        Thread invalidateEvent = new Thread() {
            @Override
            public void run() {
                eventHostLeaveResponse = iDaeClient.idaeEventHostLeaveeventPost(eventHostLeaveRequest);
                try {
                    super.run();
                    while (eventHostLeaveResponse == null) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (eventHostLeaveResponse.getStatus()){

                                case MessageCodes.OK:
                                    eventManager.updateEventHost(null);
                                    Intent intent = new Intent(MainActivity.this, InitialAcitivity.class);
                                    finish();
                                    startActivity(intent);
                                    break;
                                case MessageCodes.INCORRECT_TOKEN:
                                    showSnackbar("You have been logged out.");
                                    try {
                                        sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intentOut = new Intent(MainActivity.this, InitialAcitivity.class);
                                    intentOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    credentialsManager.clear();
                                    MainActivity.this.startActivity(intentOut);
                                    break;
                                case MessageCodes.SERVER_ERROR:
                                    showSnackbar("Server didn't respond, please try again later.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        invalidateEvent.start();
    }

    private void leaveAttendantEvent(){

        final EventAttenLeaveRequest eventAttenLeaveRequest = new EventAttenLeaveRequest();
        eventAttenLeaveRequest.setEventID(eventManager.getEventAttendant().getEventID());
        eventAttenLeaveRequest.setToken(credentialsManager.getToken());
        eventAttenLeaveRequest.setUsername(credentialsManager.getUsername());

        Thread invalidateEvent = new Thread() {
            @Override
            public void run() {
                eventAttenLeaveResponse = iDaeClient.idaeEventAttendantLeaveeventPost(eventAttenLeaveRequest);
                try {
                    super.run();
                    while (eventAttenLeaveResponse == null) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (eventAttenLeaveResponse.getStatus()){

                                case MessageCodes.OK:
                                    eventManager.updateEventHost(null);
                                    Intent intent = new Intent(MainActivity.this, InitialAcitivity.class);
                                    finish();
                                    startActivity(intent);
                                    break;
                                case MessageCodes.INCORRECT_TOKEN:
                                    showSnackbar("You have been logged out.");
                                    try {
                                        sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intentOut = new Intent(MainActivity.this, InitialAcitivity.class);
                                    intentOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    credentialsManager.clear();
                                    MainActivity.this.startActivity(intentOut);
                                    break;
                                case MessageCodes.SERVER_ERROR:
                                    showSnackbar("Server didn't respond, please try again later.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        invalidateEvent.start();
    }

}
