package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Defaults;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class WavePostActivity extends YouTubeBaseActivity {


    TextView wavePostUsername;
    TextView wavePostMessage;
    TextView wavePostTime;
    TextView wavePostTimeToLive;
    TextView wavePostNumEchos;
    TextView wavePostNumComments;


    ImageView wavePostThumbnail;
    ImageView wavePostOpenComments;
    ImageView wavePostImage;
    ImageView wavePostEcho;
    ImageView wavePostViewComments;
    ImageView wavePostTimeToLiveIcon;

    WebView webView;

    YouTubePlayerView youTubePlayerView;


    String waveID;
    String postID;      // ID of the post.
    String username;    // Username of the user that posted.
    String from;        // UserID ...^
    String message;
    String permanent;
    String message2;
    String numEchos;
    String numComments;
    String time;
    String type;

    boolean working = false;
    FirebaseBuilder firebase = new FirebaseBuilder();
    AppManager appManager;
    Picasso picasso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_post);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getApplicationContext().getCacheDir(), 25000000))
                .build();
        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView back = toolbar.getRootView().findViewById(R.id.toolbar_back_button);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        wavePostUsername = findViewById(R.id.wave_post_username);
        wavePostMessage = findViewById(R.id.wave_post_message);
        wavePostNumEchos = findViewById(R.id.wave_post_num_echos);
        wavePostTime = findViewById(R.id.wave_post_time);
        wavePostThumbnail = findViewById(R.id.wave_post_thumbnail);
        wavePostNumComments = findViewById(R.id.wave_post_num_comments);
        wavePostImage = findViewById(R.id.wave_post_image_main);
        wavePostEcho = findViewById(R.id.wave_post_echo);
        wavePostOpenComments = findViewById(R.id.wave_post_open_comments);
        wavePostViewComments = findViewById(R.id.wave_post_view_comments);
        youTubePlayerView = findViewById(R.id.wave_post_youtube);
        wavePostTimeToLiveIcon = findViewById(R.id.wave_post_time_to_live_icon);
        wavePostTimeToLive = findViewById(R.id.wave_post_time_to_live);
        webView = findViewById(R.id.wave_post_website);

        Bundle postInfo = getIntent().getExtras();

        waveID = postInfo.getString("waveID");
        postID = postInfo.getString("postID");
        this.username = postInfo.getString("username");
        this.message = postInfo.getString("message");
        this.message2 = postInfo.getString("message2");
        this.numEchos = postInfo.getString("numEchos");
        this.numComments = postInfo.getString("numComments");
        this.permanent = postInfo.getString("permanent");
        this.time = postInfo.getString("time");
        this.type = postInfo.getString("type");
        this.from = postInfo.getString("from");



        wavePostMessage.setText(message);
        wavePostMessage.setMovementMethod(new ScrollingMovementMethod());
        wavePostMessage.setTextIsSelectable(true);
        youTubePlayerView.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);

        SimpleDateFormat formatedDate = new SimpleDateFormat("EEE, d MMM. hh:mm a");
        java.sql.Timestamp timestamp = new java.sql.Timestamp(Long.valueOf(this.time));
        try {
            wavePostTime.setText(formatedDate.format(timestamp));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (type.equals("image")){

            picasso.load(message2)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_import_export).into(wavePostImage);
        }

        if (type.equals("youtube")){

            YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayerView.setVisibility(View.VISIBLE);
                    youTubePlayer.loadVideo(message2);
                    youTubePlayer.pause();
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };

            youTubePlayerView.initialize(Defaults.YOUTUBE_TOKEN, onInitializedListener);

        }

        if (type.equals("link")){
            webView.setVisibility(View.VISIBLE);
            String link = message2.split("@@@@@@")[0];
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(link);
        }


        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbPlainReference = firebaseDatabase.getReference();

        DatabaseReference waveTableGet = dbPlainReference
                .child("events_us")
                .child(waveID)
                .child("wall")
                .child("posts")
                .child(this.postID);
        waveTableGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("echos").getChildrenCount() == 1)
                    wavePostNumEchos.setText("1");
                else
                    wavePostNumEchos.setText(String.valueOf(dataSnapshot.child("echos").getChildrenCount()));

                if (dataSnapshot.child("comments").getChildrenCount() == 1)
                    wavePostNumComments.setText("1");
                else
                    wavePostNumComments.setText(String.valueOf(dataSnapshot.child("comments").getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference personalTableGet = dbPlainReference
                .child("users")
                .child(firebase.auth_id())
                .child("echos")
                .child(waveID);
        personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mainDataSnapshot) {
                if (mainDataSnapshot.hasChild(postID)) {
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_white_24));
                }else {
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_panorama_fish_eye_white_24));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (!TextUtils.isEmpty(permanent) && permanent.equals("true")){
            wavePostTimeToLiveIcon.setImageDrawable(ContextCompat.getDrawable(wavePostTimeToLiveIcon.getContext(),R.drawable.circle_holder_main_colors));
            wavePostTimeToLive.setVisibility(View.INVISIBLE);
        }else{
            final DatabaseReference db = firebase.getEvents(waveID, "wall", "posts", postID);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("echos")){

                        DatabaseReference db2 = firebase.getEvents(waveID, "wall", "posts", postID);
                        Query lastQuery = db2.child("echos").orderByKey().limitToLast(1);
                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                for (DataSnapshot child : dataSnapshot2.getChildren()){
                                    if (child.hasChild("time")){

                                        long timeDifference = System.currentTimeMillis() -  Long.valueOf(child.child("time").getValue().toString());
                                        if (TimeUnit.MILLISECONDS.toHours(timeDifference) >= 24){

                                            DatabaseReference db3 = firebase.getEvents(waveID, "wall", "posts");
                                            db3.child(postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        }else {
                                            if (24 - TimeUnit.MILLISECONDS.toHours(timeDifference) >=1){
                                                wavePostTimeToLive.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                                            }else if(60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference) >=1){
                                                wavePostTimeToLive.setText( String.format("%d m", 60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference)));
                                            }else {
                                                wavePostTimeToLive.setText("< 1m");
                                            }
                                        }
                                    }else {
                                        Log.d("ECHO_TIME", "echo without time");
                                    }
                                    break;

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("READING_ECHOS", databaseError.getMessage());
                            }
                        });
                    }else {
                        long timeDifference = System.currentTimeMillis() -  Long.valueOf(time);
                        if (TimeUnit.MILLISECONDS.toHours(timeDifference) >= 24){
                            DatabaseReference db3 = firebase.getEvents(waveID, "wall", "posts", postID);
                            db3.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        }else {
                            if (24 - TimeUnit.MILLISECONDS.toHours(timeDifference) >=1){
                                wavePostTimeToLive.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                            }else if(60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference) >=1){
                                wavePostTimeToLive.setText( String.format("%d m", 60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference)));
                            }else {
                                wavePostTimeToLive.setText("< 1m");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("PROBLEM " + databaseError.getMessage());
                }
            });


        }

        wavePostEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!working) {
                    working = true;

                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final DatabaseReference dbPlainReference = firebaseDatabase.getReference();

                    DatabaseReference personalTableGet = dbPlainReference
                            .child("users")
                            .child(firebase.auth_id())
                            .child("echos")
                            .child(waveID);
                    personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot mainDataSnapshot) {
                            if (!mainDataSnapshot.hasChild(postID)) {

                                DatabaseReference dbWavePostEchos = firebase.getEvents(waveID,
                                        "wall", "posts", postID, "echos", firebase.auth_id()).push();
                                String chatUserRef = "events_us/" + waveID + "/wall/posts/" + postID + "/echos";

                                final DatabaseReference dbWave = firebase.getEvents(waveID);
                                final DatabaseReference dbWaveb = firebase.getEvents(waveID,
                                        "wall", "posts", postID);

                                dbWave.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final long attending = dataSnapshot.child("attending").getChildrenCount();
                                        dbWaveb.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                if (dataSnapshot2.hasChild("echos")){
                                                    if (dataSnapshot2.child("echos").getChildrenCount() >=attending/3){
                                                        if (!dataSnapshot2.hasChild("permanent")){
                                                            dbWaveb.child("permanent").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        final DatabaseReference dbto = firebase.get_user(from);
                                                                        dbto.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                if (!dataSnapshot.child("permanent").child(waveID).hasChild(postID)){
                                                                                    dbto.child("permanent").child(waveID).child(postID).setValue(System.currentTimeMillis());
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                final String pushID = dbWavePostEchos.getKey();
                                Map postMap = new HashMap();
                                postMap.put("from", firebase.auth_id());
                                postMap.put("pushID", pushID);
                                postMap.put("fromUsername", appManager.getCredentialM().getUsername()); // TODO For now.
                                postMap.put("time", ServerValue.TIMESTAMP);

                                Map postUserMap = new HashMap();
                                postUserMap.put(chatUserRef + "/" + pushID, postMap);
                                firebase.getDatabase().updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            DatabaseReference personalTable = firebase.get_user_authId("echos",
                                                    waveID, postID);
                                            Map input = new HashMap<>();
                                            input.put("pushID", pushID);
                                            input.put("time", ServerValue.TIMESTAMP);
                                            personalTable.setValue(input).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.d("ADING_ECHO", task.getException().getMessage());
                                                    } else {
                                                        working = false;
                                                        wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_white_24));

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        wavePostOpenComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WavePostActivity.this, WavePostCommentsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("postID", postID);
                intent.putExtras(extras);
                WavePostActivity.this.startActivity(intent);
            }
        });



        wavePostViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WavePostActivity.this, WavePostCommentsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("postID", postID);
                intent.putExtras(extras);
                WavePostActivity.this.startActivity(intent);
            }
        });

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase1.getReference()
                .child("users")
                .child(this.from);
        databaseReference.child("profile_picture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){

                    picasso.load(dataSnapshot.getValue().toString())
                            .transform(Transformations.getScaleDownWithView(wavePostThumbnail))
                            .placeholder(R.drawable.baseline_person_black_24).into(wavePostThumbnail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    wavePostUsername.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
