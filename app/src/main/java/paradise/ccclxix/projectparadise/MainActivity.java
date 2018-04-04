package paradise.ccclxix.projectparadise;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private LoaderAdapter loaderAdapter;
    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment currentFragment;
    private HolderFragment homeFragment;
    private HolderFragment musicFragment;
    private HolderFragment sharesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        homeFragment =  new HomeFragment();
        musicFragment = new MusicFragment();
        sharesFragment = new SharesFragment();
        loadFragment(homeFragment);

    }


    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //TODO come back here when the replacing happends, the cache is also lost.

        switch (item.getItemId()){
            case R.id.navigation_home:
                currentFragment = homeFragment;
                break;
            case R.id.navigation_music:
                currentFragment = musicFragment;
                break;
            case R.id.navigation_shares:
                currentFragment = sharesFragment;
                break;
        }
        return loadFragment(currentFragment);
    }
}
