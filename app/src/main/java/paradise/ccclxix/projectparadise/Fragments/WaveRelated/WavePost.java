package paradise.ccclxix.projectparadise.Fragments.WaveRelated;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class WavePost extends Fragment {

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


    FirebaseAuth mAuth;

    AppManager appManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflater1 = inflater.inflate(R.layout.fragment_post, null);

        mAuth = FirebaseAuth.getInstance();


        wavePostUsername = inflater1.findViewById(R.id.wave_post_username);
        wavePostMessage = inflater1.findViewById(R.id.wave_post_message);
        wavePostNumEchos = inflater1.findViewById(R.id.wave_post_num_echos);
        wavePostTime = inflater1.findViewById(R.id.wave_post_time);
        wavePostThumbnail = inflater1.findViewById(R.id.wave_post_thumbnail);
        wavePostNumComments = inflater1.findViewById(R.id.wave_post_num_comments);
        wavePostImage = inflater1.findViewById(R.id.wave_post_image_main);
        wavePostEcho = inflater1.findViewById(R.id.wave_post_echo);
        wavePostOpenComments = inflater1.findViewById(R.id.wave_post_open_comments);
        wavePostViewComments = inflater1.findViewById(R.id.wave_post_view_comments);


        Bundle postInfo = getArguments();

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

            Picasso.with(wavePostImage.getContext()).load(message2).transform(Transformations.getScaleDownWithMaxWidthDP(getContext()))
                    .placeholder(R.drawable.idaelogo6_full).into(wavePostImage);
        }



        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbPlainReference = firebaseDatabase.getReference();

        DatabaseReference waveTableGet = dbPlainReference
                .child("events_us")
                .child(appManager.getWaveM().getEventID())
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
                .child(mAuth.getUid())
                .child("echos")
                .child(appManager.getWaveM().getEventID());
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
                            .child(mAuth.getUid())
                            .child("echos")
                            .child(appManager.getWaveM().getEventID());
                    personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot mainDataSnapshot) {
                            if (!mainDataSnapshot.hasChild(postID)) {

                                DatabaseReference dbWave = dbPlainReference
                                        .child("events_us")
                                        .child(appManager.getWaveM().getEventID())
                                        .child("wall")
                                        .child("posts")
                                        .child(postID)
                                        .child("echos")
                                        .child(mAuth.getUid()).push();
                                String chatUserRef = "events_us/" + appManager.getWaveM().getEventID() + "/wall/posts/" + postID + "/echos";
                                final String pushID = dbWave.getKey();
                                Map postMap = new HashMap();
                                postMap.put("from", mAuth.getUid());
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
                                                    .child(mAuth.getUid())
                                                    .child("echos")
                                                    .child(appManager.getWaveM().getEventID())
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
                                        .child(mAuth.getUid())
                                        .child("echos")
                                        .child(appManager.getWaveM().getEventID())
                                        .child(postID);
                                deleteFromUserEcho.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            DatabaseReference deleteFromWaveEcho = FirebaseDatabase.getInstance().getReference()
                                                    .child("events_us")
                                                    .child(appManager.getWaveM().getEventID())
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
                Intent intent = new Intent(getActivity(), WavePostCommentsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("postID", postID);
                intent.putExtras(extras);
                getActivity().startActivity(intent);
            }
        });



        wavePostViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WavePostCommentsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("postID", postID);
                intent.putExtras(extras);
                getActivity().startActivity(intent);
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

                    Picasso.with(wavePostThumbnail.getContext()).load(dataSnapshot.getValue().toString())
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


        return inflater1;

    }


}
