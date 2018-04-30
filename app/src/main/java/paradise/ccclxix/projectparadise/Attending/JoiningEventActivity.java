package paradise.ccclxix.projectparadise.Attending;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import iDaeAPI.model.Event;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.LocationManager;
import paradise.ccclxix.projectparadise.Loaders.EventLoader;
import paradise.ccclxix.projectparadise.Loaders.EventLoaderAdapter;
import paradise.ccclxix.projectparadise.R;

public class JoiningEventActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private EventLoaderAdapter eventLoaderAdapter;
    private EventLoader eventLoader;
    private LocationManager locationManager;
    private String lastLocationFormated;
    private static final String TAG = JoiningEventActivity.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_joining_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_scan_qr = (FloatingActionButton) findViewById(R.id.fab_scan_qr);
        fab_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoiningEventActivity.this, QRScannerActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab_refresh = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventLoader.ACTION);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
            }
        });

        eventLoaderAdapter = new EventLoaderAdapter(this);
        locationManager = new LocationManager(getApplicationContext());
/*
        getSupportLoaderManager().initLoader(R.id.string_loader_id, null,  loaderCallbacks);
        eventLoader = new EventLoader(getApplicationContext(), this);

        ListView events_list = findViewById(R.id.events_listview);
        events_list.setAdapter(eventLoaderAdapter);
        */
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

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


    private LoaderManager.LoaderCallbacks<List<Event>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Event>>() {
        @Override
        public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
            return eventLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
            eventLoaderAdapter.swapData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Event>> loader) {
            ArrayList<Event> list = new ArrayList<>();
            eventLoaderAdapter.swapData(list);
        }
    };

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(JoiningEventActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
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
