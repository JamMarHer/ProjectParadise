package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class WaveAddPostActivity extends AppCompatActivity {

    private TextView waveAddPostUsername;
    private TextView waveAddPostWave;
    private EditText waveAddPostMessage;
    private ImageView waveAddPostThumbnail;
    private ImageView waveAddPostInsertImage;
    private ImageView waveAddPostImage;
    private ImageView waveAddPostCreatePost;

    private Uri imageUriGeneral = null;

    FirebaseAuth mAuth;


    public static final int GALLERY_PICK = 1;

    EventManager eventManager;
    CredentialsManager credentialsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_add_post);

        mAuth = FirebaseAuth.getInstance();
        eventManager = new EventManager(getApplicationContext());
        credentialsManager = new CredentialsManager(getApplicationContext());

        waveAddPostUsername = findViewById(R.id.wave_add_post_username);
        waveAddPostWave = findViewById(R.id.wave_add_post_wave);
        waveAddPostMessage = findViewById(R.id.wave_add_post_message);
        waveAddPostThumbnail = findViewById(R.id.wave_add_post_thumbnail);
        waveAddPostInsertImage = findViewById(R.id.wave_add_post_insert_image);
        waveAddPostImage = findViewById(R.id.wave_add_post_image);
        waveAddPostCreatePost = findViewById(R.id.wave_add_post_send);

        if(eventManager.getEventID() != null){
            waveAddPostUsername.setText(credentialsManager.getUsername());
            waveAddPostWave.setText(eventManager.getEventName());
        }


        waveAddPostInsertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });


        waveAddPostCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!TextUtils.isEmpty(waveAddPostMessage.getText()) && mAuth.getUid()!= null && imageUriGeneral != null){



                    final String currentUser = "events_us/"+eventManager.getEventID()+"/wall/posts" + credentialsManager.getUsername();

                    String imageName = String.format("%s_.%s.jpg",String.valueOf(System.currentTimeMillis()),credentialsManager.getUsername());


                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final DatabaseReference databaseReference = firebaseDatabase.getReference();
                    StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
                    StorageReference filePath = imageStorage.child("message_images").child(imageName);
                    filePath.putFile(imageUriGeneral).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                String downloadURL = task.getResult().getDownloadUrl().toString();

                            }
                        }
                    });




                }
            }
        });



        waveAddPostCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!TextUtils.isEmpty(waveAddPostMessage.getText()) || imageUriGeneral != null){
                    if (imageUriGeneral != null){

                        String imageName = String.format("%s_.%s.jpg",String.valueOf(System.currentTimeMillis()),credentialsManager.getUsername());


                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference databaseReference = firebaseDatabase.getReference();
                        StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
                        StorageReference filePath = imageStorage.child(eventManager.getEventID()).child("images").child(imageName);
                        filePath.putFile(imageUriGeneral).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    String downloadURL = task.getResult().getDownloadUrl().toString();
                                    DatabaseReference dbWave = databaseReference
                                            .child("events_us")
                                            .child(eventManager.getEventID())
                                            .child("wall")
                                            .child("posts")
                                            .child(mAuth.getUid()).push();
                                    String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts";
                                    String pushID = dbWave.getKey();
                                    Map postMap = new HashMap();
                                    postMap.put("message", waveAddPostMessage.getText().toString());
                                    postMap.put("message2", downloadURL); // TODO For now.
                                    postMap.put("seen", false);
                                    postMap.put("numEchos", 0);
                                    postMap.put("type", "image");
                                    postMap.put("time", ServerValue.TIMESTAMP);
                                    postMap.put("from", mAuth.getUid());
                                    postMap.put("fromUsername", credentialsManager.getUsername());

                                    Map postUserMap = new HashMap();
                                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                                    databaseReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if(databaseError != null){
                                                Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                                TSnackbar snackbar = TSnackbar.make(view, "Something went wrong.", TSnackbar.LENGTH_SHORT);
                                                snackbar.setActionTextColor(Color.WHITE);
                                                snackbar.setIconLeft(R.drawable.poop_icon, 24);
                                                View snackbarView = snackbar.getView();
                                                snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                                                TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                snackbar.show();
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
                        DatabaseReference baseReference = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference dbWave = baseReference
                                .child("events_us")
                                .child(eventManager.getEventID())
                                .child("wall")
                                .child("posts")
                                .child(mAuth.getUid()).push();
                        String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts";
                        String pushID = dbWave.getKey();
                        Map postMap = new HashMap();
                        postMap.put("message", waveAddPostMessage.getText().toString());
                        postMap.put("message2", "No Image"); // TODO For now.
                        postMap.put("seen", false);
                        postMap.put("numEchos", 0);
                        postMap.put("type", "text");
                        postMap.put("time", ServerValue.TIMESTAMP);
                        postMap.put("from", mAuth.getUid());
                        postMap.put("fromUsername", credentialsManager.getUsername());

                        Map postUserMap = new HashMap();
                        postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                        baseReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                    TSnackbar snackbar = TSnackbar.make(view, "Something went wrong.", TSnackbar.LENGTH_SHORT);
                                    snackbar.setActionTextColor(Color.WHITE);
                                    snackbar.setIconLeft(R.drawable.poop_icon, 24);
                                    View snackbarView = snackbar.getView();
                                    snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                                    TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    snackbar.show();
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