package paradise.ccclxix.projectparadise.Hosting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Locale;

import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.APIForms.EventResponse;
import paradise.ccclxix.projectparadise.APIForms.User;
import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.ErrorCodes;
import paradise.ccclxix.projectparadise.BuildConfig;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HostingActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SparseArray<String> fragmentTitles = new SparseArray<>();
    private Button launch;
    protected Location mLastLocation;

    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = HostingActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private SparseArray<String> fragmentTitles = new SparseArray<>();
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TITLE = "fragment_title";

        public PlaceholderFragment() {


        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String title) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(TITLE, title);
            fragment.setArguments(args);
            return fragment;
        }

        EditText eventName;
        EventManager eventManager;
        ToggleButton privacy;
        Button launch;
        int fragment_n;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_hosting, container, false);
            TextView hostTitle = (TextView) rootView.findViewById(R.id.host_title);
            eventName = rootView.findViewById(R.id.name_event);
            privacy = rootView.findViewById(R.id.host_privacy);
            ImageView privacy_background = rootView.findViewById(R.id.host_privacy_shadow);
            ImageView launch_background = rootView.findViewById(R.id.host_launch_shadow);
            launch = rootView.findViewById(R.id.host_launch_button);
            eventManager = new EventManager(getContext());
            fragment_n = getArguments().getInt(ARG_SECTION_NUMBER);


            if (!(fragment_n == 2)) {
                eventName.setVisibility(View.INVISIBLE);
            }
            if (!(fragment_n == 3)) {
                privacy.setVisibility(View.INVISIBLE);
                privacy_background.setVisibility(View.INVISIBLE);
            }
            if (!(fragment_n == 4)) {
                launch_background.setVisibility(View.INVISIBLE);
                launch.setVisibility(View.INVISIBLE);

            }
            hostTitle.setText(getArguments().getString(TITLE));

            launch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CredentialsManager cm = new CredentialsManager(getContext());
                    eventManager.updateEmail(cm.getEmail());
                    if (eventManager.checkValidEvent()) {
                        launch.setText("lit");
                        ResizeAnimation resizeAnimation = new ResizeAnimation(view, 260);
                        resizeAnimation.setRepeatCount(Animation.INFINITE);
                        resizeAnimation.setRepeatMode(Animation.REVERSE);
                        resizeAnimation.setDuration(369);
                        view.startAnimation(resizeAnimation);

                        postNetworkRequest(eventManager.getEvent());
                    } else {
                        Toast.makeText(getContext(), "Please fill everything.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return rootView;
        }

        private void postNetworkRequest(final Event event) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

            Call<EventResponse> call = iDaeClient.post_event(event);

            call.enqueue(new Callback<EventResponse>() {
                @Override
                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                    System.out.println(response.raw());
                    if (response.body().getStatus() == 100) {
                        Intent intent =  new Intent(getContext(), MainActivity.class);
                        intent.putExtra("source","event_created");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    } else {
                        Toast.makeText(getContext(), "Wrong formating :(", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EventResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Nullable
        @Override
        public View getView() {
            if (fragment_n == 2) {
                updateName();
            } else if (fragment_n == 3) {
                updatePrivacy();
            }
            return super.getView();
        }


        public void updateName() {
            if (!eventName.getText().toString().equals("")) {
                System.out.println("updating name");
                eventManager.updateName(eventName.getText().toString());
            }
        }

        public void updatePrivacy() {
            System.out.println("updating privacy");
            eventManager.updatePrivacy(String.valueOf(privacy.isChecked()));
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SparseArray<PlaceholderFragment> fragmentTitles = new SparseArray<>();


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentTitles.put(1, PlaceholderFragment.newInstance(1, getString(R.string.create_event_first_message)));
            fragmentTitles.put(2, PlaceholderFragment.newInstance(2, getString(R.string.create_event_set_name)));
            fragmentTitles.put(3, PlaceholderFragment.newInstance(3, getString(R.string.create_event_privacy)));
            fragmentTitles.put(4, PlaceholderFragment.newInstance(4, getString(R.string.create_event_done)));
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position + 1 == 4) {
                fragmentTitles.get(2).updateName();
                fragmentTitles.get(3).updatePrivacy();
            }
            return fragmentTitles.get(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
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
                getLastLocation();
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
        ActivityCompat.requestPermissions(HostingActivity.this,
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

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            EventManager eventManager = new EventManager(getApplicationContext());
                            eventManager.updateLatitude(Double.toString(mLastLocation.getLatitude()));
                            eventManager.updateLongitude(Double.toString(mLastLocation.getLongitude()));
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());

                        }
                    }
                });
    }
}