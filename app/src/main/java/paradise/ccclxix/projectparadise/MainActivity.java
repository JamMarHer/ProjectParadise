package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Fragments.ExploreFragment;
import paradise.ccclxix.projectparadise.Fragments.PersonalFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveFragment;
import paradise.ccclxix.projectparadise.Fragments.ChatFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.Registration.WelcomeToParadiseActivity;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.OkHttp3Helpers;
import paradise.ccclxix.projectparadise.utils.SnackBar;


public class MainActivity extends AppCompatActivity{

    private static int SPLASH_TIME_OUT = 4000;
    private HolderFragment personalFragment;
    private HolderFragment waveFragment;
    private HolderFragment exploreFragment;


    AppManager appManager;

    private Toolbar mainToolbar;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    private SnackBar snackbar = new SnackBar();
    Picasso picasso;

    private int currentFragment;
    private  AppBarLayout toolbar;


    private ImageView switchFragments;
    private ImageView addPost_createWave;
    private ImageView shareWave_scanQR;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentFragment = 0;

        snackbar = new SnackBar();
        this.picasso =  new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(
                OkHttp3Helpers.getOkHttpClient(this.TAG, getApplicationContext()))).build();

        appManager = (AppManager) new AppManager().initialize(getApplicationContext());


        this.toolbar = findViewById(R.id.appBarLayout);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_app_bar_settings);




        this.toolbar.getRootView().findViewById(R.id.main_app_bar_user_search_text).setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source.equals("registration")) {
            appManager.getModeM().setModeToExplore();
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
        fragmentToShow(waveFragment, exploreFragment);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent1);
            }
        });

        setupUserCard();

        switchFragments = findViewById(R.id.main_fab_switch_fragments);
        shareWave_scanQR = findViewById(R.id.main_fab_share_wave_scan_qr);
        addPost_createWave = findViewById(R.id.main_fab_add_wave_post);

        switchFragments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment == 0)
                    currentFragment = 1;
                else
                    currentFragment = 0;
                changeFragment();
            }
        });

    }

    public AppBarLayout getToolbar(){
        return this.toolbar;
    }

    public AppManager getAppManager() {
        return appManager;
    }

    public void loadFragments(){
        waveFragment = new WaveFragment();
        exploreFragment = new ExploreFragment();
    }

    // TODO This can be highly optimized.
    private void setupUserCard(){
        appManager.getCredentialM().setDataChangedListener(new CredentialsManager.DataChangedListener() {
            @Override
            public void onDataChanged(boolean key) {
                if (key){
                    ((TextView)toolbar.getRootView().findViewById(R.id.main_app_bar_username)).setText(appManager.getCredentialM().getUsername());
                    if(appManager.getWaveM().inWave()){
                        ((TextView)toolbar.getRootView().findViewById(R.id.main_app_bar_waveName)).setText(appManager.getWaveM().getEventName());
                    }else {
                        ((TextView)toolbar.getRootView().findViewById(R.id.main_app_bar_waveName)).setText("Waveless");

                    }
                    //myNumContacts.setText(appManager.getCredentialM().getNumContacts());
                    //myNumWaves.setText(appManager.getCredentialM().getNumWaves());
                    //mNumVerified.setText(appManager.getCredentialM().getNumPermanents());
                    String thumbnailURL = appManager.getCredentialM().getProfilePic();
                    if (!TextUtils.isEmpty(thumbnailURL)) {
                        picasso.load(thumbnailURL)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.ic_import_export).into(((ImageView)
                                toolbar.getRootView().findViewById(R.id.main_app_bar_user_thumbanail)));
                    }
                }

            }
        });
    }

    private void addAllFragments(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, waveFragment)
                .add(R.id.fragment_container, exploreFragment)
                .commit();
    }

    private boolean fragmentToShow(Fragment toShow, Fragment toHide){
        if (toShow != null & toHide != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.alpha_0_1,R.anim.alpha_1_0)
                    .show(toShow)
                    .hide(toHide)
                    .commit();
            return true;
        }else{
            return false;
        }
    }


    public void changeFragment() {
        final EditText searchText = (EditText) this.toolbar.getRootView().findViewById(R.id.main_app_bar_user_search_text);
        final TextView wavename = (TextView) this.toolbar.getRootView().findViewById(R.id.main_app_bar_waveName);
        final TextView username = (TextView) this.toolbar.getRootView().findViewById(R.id.main_app_bar_username);
        final ImageView thumbnail = (ImageView) this.toolbar.getRootView().findViewById(R.id.main_app_bar_user_thumbanail);


        final Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_0_1);
        Animation fadeoutAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_1_0);
        switch (currentFragment){
            case 0:
                fragmentToShow(waveFragment, exploreFragment);
                searchText.startAnimation(fadeoutAnimation);
                picasso.load(appManager.getCredentialM().getProfilePic())
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_import_export).into(((ImageView)
                        toolbar.getRootView().findViewById(R.id.main_app_bar_user_thumbanail)));
                shareWave_scanQR.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_black_24dp));
                switchFragments.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_explore_black_24));
                addPost_createWave.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_black_24dp));

                wavename.startAnimation(fadeInAnimation);
                username.startAnimation(fadeInAnimation);
                thumbnail.startAnimation(fadeInAnimation);
                shareWave_scanQR.startAnimation(fadeInAnimation);
                switchFragments.startAnimation(fadeInAnimation);
                addPost_createWave.startAnimation(fadeInAnimation);


                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        searchText.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fadeoutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        wavename.setVisibility(View.VISIBLE);
                        username.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 1:
                fragmentToShow(exploreFragment, waveFragment);
                thumbnail.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_search_white_24));
                searchText.startAnimation(fadeInAnimation);
                thumbnail.startAnimation(fadeInAnimation);
                wavename.startAnimation(fadeoutAnimation);
                username.startAnimation(fadeoutAnimation);
                shareWave_scanQR.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_center_focus_weak_black_24));
                switchFragments.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_view_day_black_24));

                shareWave_scanQR.startAnimation(fadeInAnimation);
                switchFragments.startAnimation(fadeInAnimation);
                addPost_createWave.startAnimation(fadeInAnimation);

                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        searchText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fadeoutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        wavename.setVisibility(View.INVISIBLE);
                        username.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;


        }
    }

}
