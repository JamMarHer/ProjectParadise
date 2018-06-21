package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.glxn.qrgen.core.scheme.YouTube;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.Defaults;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.Transformations;
import paradise.ccclxix.projectparadise.utils.YoutubeHelpers;


public class WaveAddPostActivity extends YouTubeBaseActivity{

    private TextView waveAddPostUsername;
    private TextView waveAddPostWave;
    private EditText waveAddPostMessage;
    private ImageView waveAddPostThumbnail;
    private ImageView waveAddPostInsertImage;
    private ImageView waveAddPostImage;
    private ImageView waveAddPostCreatePost;
    private ImageView waveAddYoutubeVideo;

    private String youtubeLink  = "";

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    private Uri imageUriGeneral = null;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    SnackBar snackbar = new SnackBar();

    public static final int GALLERY_PICK = 1;

    AppManager appManager;

    Picasso picasso;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_add_post);
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());

        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();

        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageView mainSettings = toolbar.getRootView().findViewById(R.id.main_settings);
        mainSettings.setVisibility(View.INVISIBLE);

        waveAddPostUsername = findViewById(R.id.wave_add_post_username);
        waveAddPostWave = findViewById(R.id.wave_add_post_wave);
        waveAddPostMessage = findViewById(R.id.wave_add_post_message);
        waveAddPostThumbnail = findViewById(R.id.wave_add_post_thumbnail);
        waveAddPostInsertImage = findViewById(R.id.wave_add_post_insert_image);
        waveAddPostImage = findViewById(R.id.wave_add_post_image);
        waveAddPostCreatePost = findViewById(R.id.wave_add_post_send);
        waveAddYoutubeVideo = findViewById(R.id.wave_add_post_insert_youtube_video);
        youTubePlayerView = findViewById(R.id.create_post_youtube_view);

        youTubePlayerView.setVisibility(View.INVISIBLE);

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayerView.setVisibility(View.VISIBLE);
                waveAddPostImage.setVisibility(View.INVISIBLE);
                youTubePlayer.loadVideo(youtubeLink);
                youTubePlayer.pause();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                youTubePlayerView.setVisibility(View.INVISIBLE);
            }
        };

        if(appManager.getWaveM().getEventID() != null){
            waveAddPostUsername.setText(appManager.getCredentialM().getUsername());
            waveAddPostWave.setText(appManager.getWaveM().getEventName());
        }

        waveAddYoutubeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WaveAddPostActivity.this, R.style.MyDialogTheme);
                builder.setTitle("YouTube link");
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                input.setHint("link");
                builder.setView(input);
                builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String videoID = YoutubeHelpers.getVideoID(input.getText().toString());
                                if (!TextUtils.isEmpty(videoID)){
                                    youtubeLink = videoID;
                                    youTubePlayerView.initialize(Defaults.YOUTUBE_TOKEN, onInitializedListener);
                                }
                                dialogInterface.dismiss();

                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

        waveAddPostInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubeLink = "";
                youTubePlayerView.setVisibility(View.INVISIBLE);
                waveAddPostImage.setVisibility(View.VISIBLE);
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });





        waveAddPostCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!TextUtils.isEmpty(waveAddPostMessage.getText()) || imageUriGeneral != null){
                    if (imageUriGeneral != null){

                        String imageName = String.format("%s_.%s.jpg",String.valueOf(System.currentTimeMillis()),appManager.getCredentialM().getUsername());


                        StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
                        StorageReference filePath = imageStorage.child(appManager.getWaveM().getEventID()).child("images").child(imageName);
                        filePath.putFile(imageUriGeneral).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    String downloadURL = task.getResult().getDownloadUrl().toString();
                                    DatabaseReference dbWave = firebase.getEvents(appManager.getWaveM().getEventID(),
                                            "wall", "posts", firebase.auth_id()).push();
                                    String chatUserRef = "events_us/" + appManager.getWaveM().getEventID() + "/wall/posts";
                                    String pushID = dbWave.getKey();
                                    Map postMap = new HashMap();
                                    postMap.put("message", waveAddPostMessage.getText().toString());
                                    postMap.put("message2", downloadURL); // TODO For now.
                                    postMap.put("seen", false);
                                    postMap.put("numEchos", 0);
                                    postMap.put("type", "image");
                                    postMap.put("time", ServerValue.TIMESTAMP);
                                    postMap.put("from", firebase.auth_id());
                                    postMap.put("fromUsername", appManager.getCredentialM().getUsername());

                                    Map postUserMap = new HashMap();
                                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                                    firebase.getDatabase().updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if(databaseError != null){
                                                Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                                snackbar.showErrorBar(view);
                                                view.clearAnimation();
                                            }else{
                                                Intent intent = new Intent(WaveAddPostActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.putExtra("source", "postAdded");
                                                WaveAddPostActivity.this.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }else {
                        String messageType = "text";
                        String message2 = "Null";
                        if (!TextUtils.isEmpty(youtubeLink)){
                            messageType = "youtube";
                            message2 = youtubeLink;
                        }


                        DatabaseReference dbWave = firebase.getEvents(appManager.getWaveM().getEventID(), "wall", "posts", firebase.auth_id()).push();
                        String chatUserRef = "events_us/" + appManager.getWaveM().getEventID() + "/wall/posts";
                        String pushID = dbWave.getKey();
                        Map postMap = new HashMap();
                        postMap.put("message", waveAddPostMessage.getText().toString());
                        postMap.put("message2", message2); // TODO For now.
                        postMap.put("seen", false);
                        postMap.put("numEchos", 0);
                        postMap.put("type", messageType);
                        postMap.put("time", ServerValue.TIMESTAMP);
                        postMap.put("from", firebase.auth_id());
                        postMap.put("fromUsername", appManager.getCredentialM().getUsername());

                        Map postUserMap = new HashMap();
                        postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                        firebase.getDatabase().updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                    snackbar.showErrorBar(view);
                                    view.clearAnimation();
                                }else{
                                    Intent intent = new Intent(WaveAddPostActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("source", "postAdded");
                                    WaveAddPostActivity.this.startActivity(intent);
                                }
                            }
                        });

                    }
                }
            }
        });




        if (firebase.auth_id() != null){
            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase1.getReference()
                    .child("users")
                    .child(firebase.auth_id());
            databaseReference.child("profile_picture").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){

                        appManager.getCredentialM().updateProfilePic(dataSnapshot.getValue().toString());
                        picasso.load(dataSnapshot.getValue().toString())
                                .fit()
                                .centerInside()
                                .placeholder(R.drawable.baseline_person_black_24).into(waveAddPostThumbnail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            try{
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                waveAddPostImage.setImageDrawable(Drawable.createFromStream(inputStream, imageUri.toString()));
                imageUriGeneral = imageUri;
            }catch (Exception e){

            }
        }
    }


}
