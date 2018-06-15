package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class WavePostActivity extends AppCompatActivity {


    TextView wavePostUsername;
    TextView wavePostMessage;
    TextView wavePostTime;
    TextView wavePostNumEchos;
    TextView wavePostNumComments;


    ImageView wavePostThumbnail;
    ImageView wavePostOpenComments;
    ImageView wavePostImage;
    ImageView wavePostEcho;
    ImageView wavePostViewComments;


    String waveID;
    String postID;      // ID of the post.
    String username;    // Username of the user that posted.
    String from;        // UserID ...^
    String message;
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
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView mainSettings = toolbar.getRootView().findViewById(R.id.main_settings);
        mainSettings.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
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


        Bundle postInfo = getIntent().getExtras();

        waveID = postInfo.getString("waveID");
        postID = postInfo.getString("postID");
        this.username = postInfo.getString("username");
        this.message = postInfo.getString("message");
        this.message2 = postInfo.getString("message2");
        this.numEchos = postInfo.getString("numEchos");
        this.numComments = postInfo.getString("numComments");
        this.time = postInfo.getString("time");
        this.type = postInfo.getString("type");
        this.from = postInfo.getString("from");



        wavePostMessage.setText(message);


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
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_black_24));
                }else {
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_panorama_fish_eye_black_24));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

                                DatabaseReference dbWave = dbPlainReference
                                        .child("events_us")
                                        .child(waveID)
                                        .child("wall")
                                        .child("posts")
                                        .child(postID)
                                        .child("echos")
                                        .child(firebase.auth_id()).push();
                                String chatUserRef = "events_us/" + waveID + "/wall/posts/" + postID + "/echos";
                                final String pushID = dbWave.getKey();
                                Map postMap = new HashMap();
                                postMap.put("from", firebase.auth_id());
                                postMap.put("pushID", pushID);
                                postMap.put("fromUsername", appManager.getCredentialM().getUsername()); // TODO For now.
                                postMap.put("time", ServerValue.TIMESTAMP);

                                Map postUserMap = new HashMap();
                                postUserMap.put(chatUserRef + "/" + pushID, postMap);
                                dbPlainReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            DatabaseReference personalTable = dbPlainReference
                                                    .child("users")
                                                    .child(firebase.auth_id())
                                                    .child("echos")
                                                    .child(waveID)
                                                    .child(postID);
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
                                                        wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_black_24));

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {

                                final String postPushId = mainDataSnapshot
                                        .child(postID).child("pushID").getValue().toString();
                                final DatabaseReference deleteFromUserEcho = dbPlainReference
                                        .child("users")
                                        .child(firebase.auth_id())
                                        .child("echos")
                                        .child(waveID)
                                        .child(postID);
                                deleteFromUserEcho.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            DatabaseReference deleteFromWaveEcho = FirebaseDatabase.getInstance().getReference()
                                                    .child("events_us")
                                                    .child(waveID)
                                                    .child("wall")
                                                    .child("posts")
                                                    .child(postID)
                                                    .child("echos")
                                                    .child(postPushId);


                                            deleteFromWaveEcho.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.d("REMOVING_ECHO_WAVE", task.getException().getMessage());
                                                    } else {
                                                        working = false;
                                                        wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_panorama_fish_eye_black_24));
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("REMOVING_ECHO_USER", task.getException().getMessage());
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
