package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import net.glxn.qrgen.android.QRCode;

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.R;

public class WaveFragment extends HolderFragment implements EnhancedFragment {


    private CredentialsManager credentialsManager;
    private TextView personalUsername;
    private ImageView settingsImageView;
    private ImageView infoImageView;
    private TextView myNumWaves;
    private TextView myNumContacts;
    private TextView currentWave;
    private Button postToWall;
    private EditText messageToPostToWall;


    private AppModeManager appModeManager;


    View generalView;
    EventManager eventManager;


    private FirebaseAuth mAuth;
    private ViewGroup container;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        eventManager = new EventManager(getContext());
        appModeManager = new AppModeManager(getContext());
        credentialsManager = new CredentialsManager(getContext());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wave, null);

        generalView = view;

        postToWall = view.findViewById(R.id.post_to_wall);
        messageToPostToWall = view.findViewById(R.id.message_to_post);
        currentWave = view.findViewById(R.id.current_wave);

        if (eventManager.getEventID() !=null){
            currentWave.setText(eventManager.getEventName());
        }else {
            currentWave.setText("No wave found :/");
        }

        this.container = container;

        postToWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!TextUtils.isEmpty(messageToPostToWall.getText()) & mAuth.getUid()!= null){
                    ResizeAnimation resizeAnimation = new ResizeAnimation(view, 260);
                    resizeAnimation.setDuration(999);
                    resizeAnimation.setRepeatCount(Animation.INFINITE);
                    resizeAnimation.setRepeatMode(Animation.REVERSE);
                    postToWall.setText("lit");
                    view.startAnimation(resizeAnimation);
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference dbPlainReference = firebaseDatabase.getReference();
                    DatabaseReference dbWave = dbPlainReference
                            .child("events_us")
                            .child(eventManager.getEventID())
                            .child("wall")
                            .child("posts")
                            .child(mAuth.getUid()).push();
                    String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts";
                    String pushID = dbWave.getKey();
                    Map postMap = new HashMap();
                    postMap.put("message", messageToPostToWall.getText().toString());
                    postMap.put("seen", false);
                    postMap.put("type", "text");
                    postMap.put("time", ServerValue.TIMESTAMP);
                    postMap.put("from", mAuth.getUid());
                    postMap.put("fromUsername", credentialsManager.getUsername());

                    Map postUserMap = new HashMap();
                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                    dbPlainReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                TSnackbar snackbar = TSnackbar.make(container, "Something went wrong.", TSnackbar.LENGTH_SHORT);
                                snackbar.setActionTextColor(Color.WHITE);
                                snackbar.setIconLeft(R.drawable.poop_icon, 24);
                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                                TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);
                                snackbar.show();
                                view.clearAnimation();
                                postToWall.setText("Post");
                            }else{
                                messageToPostToWall.setText("");
                                view.clearAnimation();
                                postToWall.setText("Post");
                            }
                        }
                    });

                }
            }
        });


        return view;
    }



    private Bitmap getEventQR(){
        return QRCode.from(eventManager.getEventID()).bitmap();
    }


    @Override
    public String getName() {
        return null;
    }
}
