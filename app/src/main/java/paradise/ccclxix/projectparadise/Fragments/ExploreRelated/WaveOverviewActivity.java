package paradise.ccclxix.projectparadise.Fragments.ExploreRelated;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class WaveOverviewActivity extends AppCompatActivity {


    private TextView mWaveName;
    private TextView mWaveMembers;
    private TextView mWavePosts;
    private TextView mWavePoints;

    private ImageView mWaveThumbnail;

    // TODO: Rename and change types of parameters
    private String waveID;
    private String waveName;
    private String waveMembers;
    private String wavePosts;
    private String wavePoints;
    private String waveThumbnail;

    FirebaseBuilder firebase;
    Picasso picasso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wave_overview);
        mWaveName = findViewById(R.id.wave_overview_name);
        mWaveMembers = findViewById(R.id.wave_overview_number_members);
        mWavePosts = findViewById(R.id.wave_overview_number_posts);

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
                        }
                        if (dataSnapshot.hasChild("wall") && dataSnapshot.child("wall").hasChild("posts")){
                            wavePosts = String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount());
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

    }
}

