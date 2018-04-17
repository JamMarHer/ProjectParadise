package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import paradise.ccclxix.projectparadise.Attending.JoiningEventActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.Fragments.HomeFragment;
import paradise.ccclxix.projectparadise.Fragments.HostingHomeFragment;
import paradise.ccclxix.projectparadise.Fragments.MusicFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesFragment;
import paradise.ccclxix.projectparadise.Hosting.HostingActivity;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private LoaderAdapter loaderAdapter;
    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment currentFragment;
    private HolderFragment homeFragment;
    private HolderFragment musicFragment;
    private HolderFragment sharesFragment;
    private AppModeManager appModeManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appModeManager = new AppModeManager(getApplicationContext());
        Intent  intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")){
            appModeManager.setModeToExplore();
            Toast.makeText(MainActivity.this, "Welcome fam.", Toast.LENGTH_SHORT).show();
            loadExploreMode();
        }else if (source.equals("event_created")){
            appModeManager.setModeToHost();
            loadHostMode();
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
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        loadAllFragments();
        fragmentToShow(homeFragment, musicFragment, sharesFragment);


        FrameLayout fragment_container = findViewById(R.id.fragment_container);
        AnimationDrawable animationDrawable = (AnimationDrawable)fragment_container.getBackground();
        animationDrawable.setEnterFadeDuration(4000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    public void loadExploreMode(){
        homeFragment =  new HomeFragment();
        musicFragment = new MusicFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadHostMode(){
        homeFragment =  new HostingHomeFragment();
        musicFragment = new MusicFragment();
        sharesFragment = new SharesFragment();
    }

    public void loadAttendantMode(){
        homeFragment =  new HomeFragment();
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
}
