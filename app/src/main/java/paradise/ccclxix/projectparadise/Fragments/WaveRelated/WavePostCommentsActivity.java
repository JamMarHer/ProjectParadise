package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class WavePostCommentsActivity extends AppCompatActivity {

    EditText wavePostAddCommentMessage;

    ImageView wavePostAddCommentSend;


    RecyclerView wavePostListComments;
    WavePostCommentsAdapter wavesPostCommentsAdapter;

    FirebaseAuth mAuth;

    AppManager appManager;

    String postID;


    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_post_comments);

        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();


        postID = getIntent().getExtras().getString("postID");
        mAuth = FirebaseAuth.getInstance();

        wavePostAddCommentMessage = findViewById(R.id.wave_post_add_message);
        wavePostAddCommentSend =findViewById(R.id.wave_post_add_send);

        wavePostListComments = findViewById(R.id.wave_post_comments_recyclerView);

        wavesPostCommentsAdapter = new WavePostCommentsAdapter(getApplicationContext());
        wavePostListComments.setAdapter(wavesPostCommentsAdapter);
        wavePostListComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView mainInfo = toolbar.getRootView().findViewById(R.id.main_info);
        ImageView mainSettings = toolbar.getRootView().findViewById(R.id.main_settings);
        mainSettings.setVisibility(View.INVISIBLE);
        mainInfo.setVisibility(View.INVISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


        wavePostAddCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(wavePostAddCommentMessage.getText())){
                    DatabaseReference dbAddCommentPlain = firebaseDatabase.getReference();
                    DatabaseReference dbAddComment = dbAddCommentPlain
                            .child("events_us")
                            .child(appManager.getWaveM().getEventID())
                            .child("wall")
                            .child("posts")
                            .child(postID)
                            .child("comments")
                            .child(mAuth.getUid()).push();
                    String chatUserRef = "events_us/" + appManager.getWaveM().getEventID() + "/wall/posts/"+postID
                            +"/comments";
                    String pushID = dbAddComment.getKey();
                    Map postMap = new HashMap();
                    postMap.put("message", wavePostAddCommentMessage.getText().toString());
                    postMap.put("seen", false);
                    postMap.put("type", "text");
                    postMap.put("time", ServerValue.TIMESTAMP);
                    postMap.put("from", mAuth.getUid());
                    postMap.put("fromUsername", appManager.getCredentialM().getUsername());

                    Map postUserMap = new HashMap();
                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                    dbAddCommentPlain.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d("ADDING_COMMENT", databaseError.getMessage());
                                TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "There was a problem adding comment.", TSnackbar.LENGTH_SHORT);
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
                    .child(appManager.getWaveM().getEventID())
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
                        eventInfo.put("commentFrom", comment.child("from").getValue().toString());
                        // TODO add function (algo) for trending.
                        eventInfo.put("commentMessage", comment.child("message").getValue().toString());

                        if(!record.containsKey(commentID)) {
                            waveList.add(eventInfo);
                            record.put(commentID, waveList.size()-1);
                            if(commentID.equals(appManager.getWaveM().getEventID())){
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
        public void onBindViewHolder(final WavePostCommentsViewHolder holder, final int position) {
            final String commentID = waveList.get(position).get("commentID");
            final String commentUsername = waveList.get(position).get("commentUsername");
            final String commentMessage = waveList.get(position).get("commentMessage");
            final String commentFrom = waveList.get(position).get("commentFrom");
            holder.wavePostCommentUsername.setText(commentUsername);
            holder.wavePostCommentMessage.setText(commentMessage);
            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase1.getReference()
                    .child("users")
                    .child(commentFrom);
            databaseReference.child("profile_picture").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){

                        appManager.getCredentialM().updateProfilePic(dataSnapshot.getValue().toString());
                        picasso.load(dataSnapshot.getValue().toString())
                                .fit()
                                .centerInside()
                                .placeholder(R.drawable.baseline_person_black_24).into(holder.wavePostCommentThumbnail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        @Override
        public int getItemCount() {
            return waveList.size();
        }
    }

}
