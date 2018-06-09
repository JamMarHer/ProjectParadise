package paradise.ccclxix.projectparadise.Fragments.PersonalRelated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class EditProfileActivity extends AppCompatActivity {

    EditText name;
    EditText username;
    EditText status;
    ImageView profilePicture;
    ImageView done;



    boolean[] updated = {true, true, true, true};
    private Uri imageUriGeneral = null;

    public static final int GALLERY_PICK = 1;

    AppManager appManager;
    FirebaseBuilder firebase = new FirebaseBuilder();
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        setContentView(R.layout.activity_edit_profile);
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());

        name = findViewById(R.id.name_editable);
        username = findViewById(R.id.username_editable);
        status = findViewById(R.id.status_editable);
        profilePicture = findViewById(R.id.editable_profile_picture);
        done = findViewById(R.id.done_editable);


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



        username.setText(appManager.getCredentialM().getUsername());

        if (appManager.getCredentialM().getName() != null)
            name.setText(appManager.getCredentialM().getName());


        if (appManager.getCredentialM().getStatus() != null)
            status.setText(appManager.getCredentialM().getStatus());


        if (appManager.getCredentialM().getProfilePic() != null){
            picasso.load(appManager.getCredentialM().getProfilePic())
                    .transform(Transformations.getScaleDownWithView(profilePicture))
                    .placeholder(R.drawable.idaelogo6_full).into(profilePicture);
        }



        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebase.auth_id() != null) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                    final DatabaseReference dbPersonal = firebaseDatabase.getReference()
                            .child("users")
                            .child(firebase.auth_id());

                    if (!TextUtils.isEmpty(username.getText()) && !appManager.getCredentialM().getUsername().equals(username.getText().toString())) {
                        updated[0] = false;
                        DatabaseReference databaseReference = firebaseDatabase.getReference()
                                .child("used_usernames");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(username.getText().toString()))
                                    username.setError("Username not available");
                                else {
                                    dbPersonal.child("username").setValue(username.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updated[0] = true;
                                            appManager.getCredentialM().updateUsername(username.getText().toString());
                                                if (allSet()){
                                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                     Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                     Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.putExtra("source", "logged_in");
                                                    finish();
                                                    getApplication().startActivity(intent);
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
                    if (appManager.getCredentialM().getStatus() == null || !appManager.getCredentialM().getStatus().equals(status.getText().toString())) {
                        updated[1] = false;
                        dbPersonal.child("status").setValue(status.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updated[1] = true;
                                appManager.getCredentialM().updateStatus(status.getText().toString());
                                if (allSet()){
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("source", "logged_in");
                                    finish();
                                    getApplication().startActivity(intent);
                                }
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(name.getText()) && (appManager.getCredentialM().getName() == null || !appManager.getCredentialM().getName().equals(name.getText().toString()))) {
                        updated[2] = false;
                        dbPersonal.child("name").setValue(name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updated[2] = true;
                                appManager.getCredentialM().updateName(name.getText().toString());
                                if (allSet()){
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("source", "logged_in");
                                    finish();
                                    getApplication().startActivity(intent);
                                }


                            }
                        });
                    }

                    if (imageUriGeneral != null) {
                        System.out.println("HERERE");
                        updated[3] = false;
                        String imageName = String.format("%s_.%s.jpg", String.valueOf(System.currentTimeMillis()), appManager.getCredentialM().getUsername());


                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
                        StorageReference filePath = imageStorage.child("profile_pictures").child(firebase.auth_id()).child(imageName);
                        filePath.putFile(imageUriGeneral).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final String downloadURL = task.getResult().getDownloadUrl().toString();
                                    databaseReference
                                            .child("users")
                                            .child(firebase.auth_id())
                                            .child("profile_picture")
                                            .setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updated[3] = true;
                                            if (task.isSuccessful()){
                                                appManager.getCredentialM().updateProfilePic(downloadURL);
                                            }
                                            if (allSet()){
                                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("source", "logged_in");
                                                finish();
                                                getApplication().startActivity(intent);
                                            }
                                        }
                                    });
                                }else {
                                    Log.d("UPLOADING_IMAGE", task.getException().getMessage());
                                }

                            }


                        });
                    }
                }
            }
        });

    }


    private boolean allSet(){
        for (int i = 0; i < updated.length; i++){
            if (!updated[i])
                return false;

        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            try{
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                profilePicture.setImageDrawable(Drawable.createFromStream(inputStream, imageUri.toString()));
                imageUriGeneral = imageUri;
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }



}
