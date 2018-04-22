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
import android.widget.Toast;

import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.Attending.JoiningEventActivity;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Fragments.HomeAttendantFragment;
import paradise.ccclxix.projectparadise.Fragments.HomeFragment;
import paradise.ccclxix.projectparadise.Fragments.HomeHostingFragment;
import paradise.ccclxix.projectparadise.Fragments.MusicFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesFragment;
import paradise.ccclxix.projectparadise.Hosting.HostingActivity;
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
    private NetworkHandler networkHandler;

    private EventManager eventManager;

    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appModeManager = new AppModeManager(getApplicationContext());
        Intent  intent = getIntent();
        String source = intent.getStringExtra("source");
        networkHandler = new NetworkHandler(getApplicationContext());
        if (source.equals("registration")){
            appModeManager.setModeToExplore();
            Toast.makeText(MainActivity.this, "Welcome fam.", Toast.LENGTH_SHORT).show();
            loadExploreMode();
        }else if (source.equals("event_created")) {
            appModeManager.setModeToHost();
            invalidateOptionsMenu();
            loadHostMode();
        }else if (source.equals("qr_code_scanned")) {
            appModeManager.setModeToAttendant();
            loginEvent(intent.getStringExtra("event_id"));
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
            networkHandler.announceInternetConnection(this);
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
            networkHandler.announceServerAlive(this);
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        eventManager = new EventManager(getApplicationContext());

        loadAllFragments();
        fragmentToShow(homeFragment, musicFragment, sharesFragment);

    }

    private void loginEvent(String eventID){
        CredentialsManager cm = new CredentialsManager(getApplicationContext());
        final Event event =  new Event().setUpLoginEvent(cm.getToken(), eventID);

        Thread loginEvent = new Thread() {
            @Override
            public void run() {
                networkHandler.loginEvent(event);
                try {
                    super.run();
                    while (networkHandler.isRunning()) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NetworkResponse networkResponse = networkHandler.getNetworkResponse();
                            switch (networkResponse.getStatus()){

                                case MessageCodes.OK:
                                    showSnackbar("You are now logged in. There are "+String.valueOf(
                                            networkResponse.getResponse().getAttendants().size())+ " attendants.");
                                    break;
                                case MessageCodes.INCORRECT_FORMAT:
                                    showSnackbar("There has been a problem with the server response.");
                                    break;
                                case MessageCodes.FAILED_CONNECTION:
                                    showSnackbar("Server didn't respond.");
                                    break;
                                case MessageCodes.NO_INTERNET_CONNECTION:
                                    showSnackbar("No internet connection.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        loginEvent.start();
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

                Event currentEvent = eventManager.getEvent();
                invalidateEvent(currentEvent);
                return true;
            case R.id.log_out_attending:
                appModeManager.setModeToExplore();
                logoutEvent(null);
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
        Intent intent = new Intent(MainActivity.this, HostingActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void joinEvent(View view){
        Intent intent = new Intent(MainActivity.this, JoiningEventActivity.class);
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

    private void invalidateEvent(final Event event){
        Thread invalidateEvent = new Thread() {
            @Override
            public void run() {
                networkHandler.invalidateEventNetworkRequest(event);
                try {
                    super.run();
                    while (networkHandler.isRunning()) {
                        sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            NetworkResponse networkResponse = networkHandler.getNetworkResponse();
                            switch (networkResponse.getStatus()){

                                case 100:
                                    eventManager.clear();
                                    Intent intent = new Intent(MainActivity.this, InitialAcitivity.class);
                                    finish();
                                    startActivity(intent);
                                    break;
                                case MessageCodes.INCORRECT_FORMAT:
                                    showSnackbar("There has been a problem with the server response.");
                                    break;
                                case MessageCodes.FAILED_CONNECTION:
                                    showSnackbar("Server didn't respond.");
                                    break;
                                case MessageCodes.NO_INTERNET_CONNECTION:
                                    showSnackbar("No internet connection.");
                                    break;
                            }
                        }
                    });
                }
            }
        };
        invalidateEvent.start();
    }

    // TODO
    private void logoutEvent(final Event event){
        Intent intent = new Intent(MainActivity.this, InitialAcitivity.class);
        finish();
        startActivity(intent);
    }
}
