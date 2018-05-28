package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Fragments.PersonalFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class WavePost extends Fragment {

    TextView wavePostUsername;
    TextView wavePostMessage;
    TextView wavePostTime;
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

    WavePostCommentsAdapter wavesPostCommentsAdapter;
    RecyclerView wavePostListComments;

    FirebaseAuth mAuth;

    EventManager eventManager;
    CredentialsManager credentialsManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflater1 = inflater.inflate(R.layout.fragment_post, null);

        mAuth = FirebaseAuth.getInstance();

        eventManager = new EventManager(getContext());
        credentialsManager = new CredentialsManager(getContext());

        wavePostUsername = inflater1.findViewById(R.id.wave_post_username);
        wavePostMessage = inflater1.findViewById(R.id.wave_post_message);
        wavePostNumEchos = inflater1.findViewById(R.id.wave_post_num_echos);
        wavePostTime = inflater1.findViewById(R.id.wave_post_time);
        wavePostNumComments = inflater1.findViewById(R.id.wave_post_num_comments);
        wavePostAddCommentMessage = inflater1.findViewById(R.id.wave_post_add_message);
        wavePostAddCommentSend = inflater1.findViewById(R.id.wave_post_add_send);
        wavePostImage = inflater1.findViewById(R.id.wave_post_image);
        wavePostEcho = inflater1.findViewById(R.id.echoPost);
        wavePostListComments = inflater1.findViewById(R.id.wave_post_comments_recyclerView);


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

        wavesPostCommentsAdapter = new WavePostCommentsAdapter(getContext());
        wavePostListComments.setAdapter(wavesPostCommentsAdapter);
        wavePostListComments.setLayoutManager(new LinearLayoutManager(getContext()));

        SimpleDateFormat formatedDate = new SimpleDateFormat("EEE, d MMM. hh:mm a");
        java.sql.Timestamp timestamp = new java.sql.Timestamp(Long.valueOf(this.time));
        try {
            wavePostTime.setText(formatedDate.format(timestamp));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (type.equals("image")){
            Picasso.with(wavePostImage.getContext()).load(message2)
                    .placeholder(R.drawable.idaelogo6_full).into(wavePostImage);
        }



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

        wavePostAddCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(wavePostAddCommentMessage.getText())){
                    DatabaseReference dbAddCommentPlain = firebaseDatabase.getReference();
                    DatabaseReference dbAddComment = dbAddCommentPlain
                            .child("events_us")
                            .child(eventManager.getEventID())
                            .child("wall")
                            .child("posts")
                            .child(postID)
                            .child("comments")
                            .child(mAuth.getUid()).push();
                    String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts/"+postID
                            +"/comments";
                    String pushID = dbAddComment.getKey();
                    Map postMap = new HashMap();
                    postMap.put("message", wavePostAddCommentMessage.getText().toString());
                    postMap.put("seen", false);
                    postMap.put("type", "text");
                    postMap.put("time", ServerValue.TIMESTAMP);
                    postMap.put("from", mAuth.getUid());
                    postMap.put("fromUsername", credentialsManager.getUsername());

                    Map postUserMap = new HashMap();
                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                    dbAddCommentPlain.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d("ADDING_COMMENT", databaseError.getMessage());
                                TSnackbar snackbar = TSnackbar.make(inflater1.getRootView().findViewById(android.R.id.content), "There was a problem adding comment.", TSnackbar.LENGTH_SHORT);
                                snackbar.setActionTextColor(Color.WHITE);
                                snackbar.setIconLeft(R.drawable.poop_icon, 24);
                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                                TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);
                                snackbar.show();
                            }else {
                                wavePostAddCommentMessage.setText("");
                            }
                        }
                    });
                }
            }
        });

        return inflater1;

    }


    private class WavePostCommentsViewHolder extends RecyclerView.ViewHolder{

        TextView wavePostCommentUsername;
        TextView wavePostCommentMessage;
        ImageView wavePostCommentThumbnail;
        View mView;

        public WavePostCommentsViewHolder(View itemView) {
            super(itemView);
            wavePostCommentUsername = itemView.findViewById(R.id.wave_post_comment_username);
            wavePostCommentMessage = itemView.findViewById(R.id.wave_post_comment_message);
            wavePostCommentThumbnail = itemView.findViewById(R.id.wave_post_comment_thumbnail);

            mView = itemView;
        }
    }

    private class WavePostCommentsAdapter extends RecyclerView.Adapter<WavePostCommentsViewHolder>{

        private LayoutInflater inflater;

        private List<HashMap<String, String>> waveList;
        private HashMap<String, Integer> record;
        public WavePostCommentsAdapter(final Context context){
            waveList = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(eventManager.getEventID())
                    .child("wall")
                    .child("posts")
                    .child(postID)
                    .child("comments");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waveList.clear();
                    record = new HashMap<>();

                    for (final  DataSnapshot comment: dataSnapshot.getChildren()){
                        final String commentID = comment.getKey();
                        HashMap<String, String> eventInfo = new HashMap<>();
                        eventInfo.put("commentID", commentID);
                        eventInfo.put("commentUsername", comment.child("fromUsername").getValue().toString());
                        // TODO add function (algo) for trending.
                        eventInfo.put("commentMessage", comment.child("message").getValue().toString());

                        if(!record.containsKey(commentID)) {
                            waveList.add(eventInfo);
                            record.put(commentID, waveList.size()-1);
                            if(commentID.equals(eventManager.getEventID())){
                                int toExchange = waveList.size()-1;
                                Collections.swap(waveList,0, toExchange);
                                record.put(commentID, 0);
                                record.put(waveList.get(toExchange).get("commentID"), waveList.size()-1);
                            }
                        }else {
                            waveList.set(record.get(commentID), eventInfo);
                        }
                        wavesPostCommentsAdapter.notifyDataSetChanged();
                        inflater = LayoutInflater.from(context);
                    }

                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("MY_WAVES", databaseError.getMessage());

                }
            });
        }



        @Override
        public WavePostCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_post_single_comment, parent, false);
            WavePostCommentsViewHolder holder = new WavePostCommentsViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(WavePostCommentsViewHolder holder, final int position) {
            final String commentID = waveList.get(position).get("commentID");
            final String commentUsername = waveList.get(position).get("commentUsername");
            final String commentMessage = waveList.get(position).get("commentMessage");
            holder.wavePostCommentUsername.setText(commentUsername);
            holder.wavePostCommentMessage.setText(commentMessage);


        }


        @Override
        public int getItemCount() {
            return waveList.size();
        }
    }

}
