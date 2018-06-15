package paradise.ccclxix.projectparadise.Fragments.ExploreRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;

public class WaveOverviewActivity extends AppCompatActivity {


    private TextView mWaveName;
    private TextView mWaveMembers;
    private TextView mWavePosts;
    private TextView mWavePoints;
    private TextView mWaveJoin;

    private ImageView mWaveThumbnail;
    private ProgressBar mprogressBar;
    private ConstraintLayout constraintLayout;

    // TODO: Rename and change types of parameters
    private String waveID;
    private String waveName;
    private String waveMembers;
    private String wavePosts;
    private String wavePoints;
    private String waveThumbnail;


    AppManager appManager;
    FirebaseBuilder firebase;
    Picasso picasso;
    SnackBar snackBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wave_overview);
        mWaveName = findViewById(R.id.wave_overview_name);
        mWaveMembers = findViewById(R.id.wave_overview_number_members);
        mWavePosts = findViewById(R.id.wave_overview_number_posts);
        mWaveJoin = findViewById(R.id.wave_overview_join);
        mprogressBar = findViewById(R.id.join_progress);
        constraintLayout = findViewById(R.id.wave_overview_constraintLayout);

        snackBar = new SnackBar();
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView back = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WaveOverviewActivity.this, SettingsActivity.class);
                WaveOverviewActivity.this.startActivity(intent1);
            }
        });


        firebase = new FirebaseBuilder();
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        Bundle bundle = getIntent().getExtras();
        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();


        if (bundle !=null){
            waveID = bundle.getString("ID","");
            waveName = bundle.getString("name", "");
            waveThumbnail = bundle.getString("thumbnail", "");
            mWaveName.setText(waveName);
            if (!TextUtils.isEmpty(waveThumbnail)){
                picasso.load(waveThumbnail)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(mWaveThumbnail);
            }

            DatabaseReference databaseReference = firebase.getEvents(waveID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("name_event")){
                        if (dataSnapshot.hasChild("attending")){
                            waveMembers = String.valueOf(dataSnapshot.child("attending").getChildrenCount());
                        }else {
                            waveMembers = "0";
                        }
                        if (dataSnapshot.hasChild("wall") && dataSnapshot.child("wall").hasChild("posts")){
                            wavePosts = String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount());
                        }else {
                            wavePosts = "0";
                        }
                        mWaveMembers.setText(waveMembers);
                        mWavePosts.setText(wavePosts);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mWaveJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UINotificationHelpers.showProgress(true,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                appManager.getWaveM().updateEventID(waveID);
                appManager.getWaveM().updateEventName(waveName);
                DatabaseReference eventDatabaseReference = firebase.getEvents(waveID, "attending", firebase.auth_id());
                HashMap<String, Long> in = new HashMap<>();
                final long timeIn = System.currentTimeMillis();
                in.put("in", timeIn);
                eventDatabaseReference.setValue(in).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            UINotificationHelpers.showProgress(false,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                            DatabaseReference userDatabaseReference =  firebase.get_user_authId("waves", "in", waveID);
                            userDatabaseReference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    final Intent intent = new Intent(WaveOverviewActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("source", "joined_event");

                                    appManager.getWaveM().updatePersonalTimein(timeIn);

                                    WaveOverviewActivity.this.startActivity(intent);
                                    finish();
                                }
                            });
                        }else{
                            snackBar.showWhiteBar(findViewById(android.R.id.content),"Something went wrong");
                            UINotificationHelpers.showProgress(false,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                        }

                    }
                });
            }
        });

    }
}

