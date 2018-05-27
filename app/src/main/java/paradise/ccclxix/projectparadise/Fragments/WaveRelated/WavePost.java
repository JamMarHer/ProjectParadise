package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.R;

public class WavePost extends Fragment {

    TextView wavePostUsername;
    TextView wavePostMessage;
    TextView wavePostNumEchos;
    TextView wavePostNumComments;

    EditText wavePostAddCommentMessage;

    ImageView wavePostAddCommentSend;
    ImageView wavePostImage;
    ImageView wavePostEcho;


    String postID;
    String username;
    String message;
    String message2;
    String numEchos;
    String numComments;
    String time;
    String type;


    FirebaseAuth mAuth;

    EventManager eventManager;
    CredentialsManager credentialsManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_post, null);

        mAuth = FirebaseAuth.getInstance();

        eventManager = new EventManager(getContext());
        credentialsManager = new CredentialsManager(getContext());

        wavePostUsername = inflater1.findViewById(R.id.wave_post_username);
        wavePostMessage = inflater1.findViewById(R.id.wave_post_message);
        wavePostNumEchos = inflater1.findViewById(R.id.wave_post_num_echos);
        wavePostNumComments = inflater1.findViewById(R.id.wave_post_num_comments);
        wavePostAddCommentMessage = inflater1.findViewById(R.id.wave_post_add_message);
        wavePostAddCommentSend = inflater1.findViewById(R.id.wave_post_add_send);
        wavePostImage = inflater1.findViewById(R.id.wave_post_image);
        wavePostEcho = inflater1.findViewById(R.id.echoPost);

        Bundle postInfo = getArguments();

        this.postID = postInfo.getString("postID");
        this.username = postInfo.getString("username");
        this.message = postInfo.getString("message");
        this.message2 = postInfo.getString("message2");
        this.numEchos = postInfo.getString("numEchos");
        this.numComments = postInfo.getString("numComments");
        this.time = postInfo.getString("time");
        this.type = postInfo.getString("type");

        wavePostUsername.setText(username);
        wavePostMessage.setText(message);



        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbPlainReference = firebaseDatabase.getReference();

        DatabaseReference waveTableGet = dbPlainReference
                .child("events_us")
                .child(eventManager.getEventID())
                .child("wall")
                .child("posts")
                .child(this.postID);
        waveTableGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("echos").getChildrenCount() == 1)
                    wavePostNumEchos.setText(String.format("%s echo", 1));
                else
                    wavePostNumEchos.setText(String.format("%s echos", dataSnapshot.child("echos").getChildrenCount()));

                if (dataSnapshot.child("comments").getChildrenCount() == 1)
                    wavePostNumComments.setText(String.format("%s comment", 1));
                else
                    wavePostNumComments.setText(String.format("%s comments", dataSnapshot.child("comments").getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference personalTableGet = dbPlainReference
                .child("users")
                .child(mAuth.getUid())
                .child("echos")
                .child(eventManager.getEventID());
        personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot mainDataSnapshot) {
                if (mainDataSnapshot.hasChild(postID)) {
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_like_white));
                }else {
                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_not_like_white));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        wavePostEcho.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference dbPlainReference = firebaseDatabase.getReference();

                DatabaseReference personalTableGet = dbPlainReference
                        .child("users")
                        .child(mAuth.getUid())
                        .child("echos")
                        .child(eventManager.getEventID());
                personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot mainDataSnapshot) {
                        if (!mainDataSnapshot.hasChild(postID)) {

                            DatabaseReference dbWave = dbPlainReference
                                    .child("events_us")
                                    .child(eventManager.getEventID())
                                    .child("wall")
                                    .child("posts")
                                    .child(postID)
                                    .child("echos")
                                    .child(mAuth.getUid()).push();
                            String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts/" + postID + "/echos";
                            final String pushID = dbWave.getKey();
                            Map postMap = new HashMap();
                            postMap.put("from", mAuth.getUid());
                            postMap.put("pushID", pushID);
                            postMap.put("fromUsername", credentialsManager.getUsername()); // TODO For now.
                            postMap.put("time", ServerValue.TIMESTAMP);

                            Map postUserMap = new HashMap();
                            postUserMap.put(chatUserRef + "/" + pushID, postMap);
                            dbPlainReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        DatabaseReference personalTable = dbPlainReference
                                                .child("users")
                                                .child(mAuth.getUid())
                                                .child("echos")
                                                .child(eventManager.getEventID())
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
                                                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_like_white));

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
                                    .child(mAuth.getUid())
                                    .child("echos")
                                    .child(eventManager.getEventID())
                                    .child(postID);
                            deleteFromUserEcho.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        DatabaseReference deleteFromWaveEcho = FirebaseDatabase.getInstance().getReference()
                                                .child("events_us")
                                                .child(eventManager.getEventID())
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
                                                    wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_not_like_white));
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
        });

        return inflater1;

    }
}
