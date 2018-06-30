package paradise.ccclxix.projectparadise.Fragments.ExploreRelated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.Fragments.WaveFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePostActivity;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePostCommentsActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;
import paradise.ccclxix.projectparadise.utils.ErrorMessageComposer;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;
import paradise.ccclxix.projectparadise.utils.YoutubeHelpers;

public class WaveOverviewActivity extends AppCompatActivity {


    private static final String TAG = "WAVE_OVERVIEW_ACTIVITY";

    private TextView mWaveName;
    private TextView mWaveMembers;
    private TextView mWavePosts;
    private TextView mWaveScore;
    private TextView mWaveJoin;

    private ImageView mWaveThumbnail;
    private ProgressBar mprogressBar;
    private ConstraintLayout constraintLayout;

    // TODO: Rename and change types of parameters
    private String waveID;
    private String waveName;
    private String waveMembers;
    private String wavePosts;
    private String wScore;
    private String waveThumbnail;


    private PostsAdapter adapter;
    private RecyclerView waveRecyclerView;

    private List<Map<String, String>> posts;
    Picasso picasso;


    AppManager appManager;
    FirebaseBuilder firebase;
    SnackBar snackBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wave_overview);
        mWaveName = findViewById(R.id.wave_card_name);
        mWaveMembers = findViewById(R.id.wave_card_members);
        mWavePosts = findViewById(R.id.wave_card_posts);
        mWaveJoin = findViewById(R.id.wave_overview_join);
        mWaveScore = findViewById(R.id.wave_card_wScore);
        mprogressBar = findViewById(R.id.join_progress);
        constraintLayout = findViewById(R.id.wave_overview_constraintLayout);
        waveRecyclerView = findViewById(R.id.wave_overview_posts);



        snackBar = new SnackBar();
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView back = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WaveOverviewActivity.this, SettingsActivity.class);
                WaveOverviewActivity.this.startActivity(intent1);
            }
        });


        firebase = new FirebaseBuilder();
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        Bundle bundle = getIntent().getExtras();
        picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(okHttpClient)).build();


        if (bundle !=null){
            waveID = bundle.getString("ID","");
            waveName = bundle.getString("name", "");
            waveThumbnail = bundle.getString("thumbnail", "");
            mWaveName.setText(waveName);
            adapter = new PostsAdapter(getApplicationContext(), waveID);
            waveRecyclerView.setAdapter(adapter);
            waveRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            if (!TextUtils.isEmpty(waveThumbnail)){
                picasso.load(waveThumbnail)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(mWaveThumbnail);
            }

            DatabaseReference databaseReference = firebase.getEvents(waveID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("name_event")){
                        if (dataSnapshot.hasChild("attending")){
                            waveMembers = String.valueOf(dataSnapshot.child("attending").getChildrenCount());
                        }else {
                            waveMembers = "0";
                        }
                        if (dataSnapshot.hasChild("wall") && dataSnapshot.child("wall").hasChild("posts")){
                            wavePosts = String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount());
                        }else {
                            wavePosts = "0";
                        }
                        if (dataSnapshot.hasChild("wave_score"))
                            wScore = dataSnapshot.child("wave_score").getValue().toString();
                        else
                            wScore = "?";
                        mWaveMembers.setText(waveMembers);
                        mWavePosts.setText(wavePosts);
                        mWaveScore.setText(wScore);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mWaveJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UINotificationHelpers.showProgress(true,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                appManager.getWaveM().updateEventID(waveID);
                appManager.getWaveM().updateEventName(waveName);
                DatabaseReference eventDatabaseReference = firebase.getEvents(waveID, "attending", firebase.auth_id());
                HashMap<String, Long> in = new HashMap<>();
                final long timeIn = System.currentTimeMillis();
                in.put("in", timeIn);
                eventDatabaseReference.setValue(in).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            UINotificationHelpers.showProgress(false,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                            DatabaseReference userDatabaseReference =  firebase.get_user_authId("waves", "in", waveID);
                            userDatabaseReference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    final Intent intent = new Intent(WaveOverviewActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("source", "joined_event");

                                    appManager.getWaveM().updatePersonalTimein(timeIn);

                                    WaveOverviewActivity.this.startActivity(intent);
                                    finish();
                                }
                            });
                        }else{
                            snackBar.showWhiteBar(findViewById(android.R.id.content),"Something went wrong");
                            UINotificationHelpers.showProgress(false,constraintLayout, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                        }

                    }
                });
            }
        });

    }


    private class PostViewHolder extends  RecyclerView.ViewHolder{
        TextView waveName;
        TextView postMessage;
        TextView postEchos;
        TextView postComments;
        TextView postTTL;

        ImageView postEcho;
        ImageView postComment;
        ImageView postImage;
        ImageView postLaunch;
        ImageView postWaveThumbnail;
        ImageView postFromThumbnail;
        ImageView postTimeToLiveIcon;
        ImageView source;

        ConstraintLayout briefConstraintL;

        public PostViewHolder(View itemView){
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_single_brief_name_main);
            postMessage = itemView.findViewById(R.id.wave_single_brief_message_main);
            postEchos = itemView.findViewById(R.id.wave_single_brief_echos_main);
            postComments = itemView.findViewById(R.id.wave_single_brief_comments_main);
            postTTL = itemView.findViewById(R.id.wave_single_brief_time_to_live_main);
            postImage = itemView.findViewById(R.id.wave_post_image_main);
            postLaunch = itemView.findViewById(R.id.wave_single_brief_launch_main);
            briefConstraintL = itemView.findViewById(R.id.wave_single_brief_main);
            postWaveThumbnail = itemView.findViewById(R.id.wave_single_brief_wave_thumbnail_main);
            postEcho = itemView.findViewById(R.id.main_echo_post);
            postComment = itemView.findViewById(R.id.main_comment_post);
            postTimeToLiveIcon = itemView.findViewById(R.id.wave_post_time_to_live_icon);
            source = itemView.findViewById(R.id.wave_single_brief_source);

        }

    }




    private class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{

        private LayoutInflater inflater;
        private Context context;
        private String mWaveID;

        public void reload (){

            posts = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            UINotificationHelpers.showProgress(true,waveRecyclerView, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

            DatabaseReference waveDBReference = FirebaseDatabase.getInstance().getReference().child("events_us").child(mWaveID);
            waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    posts.clear();
                    record = new HashMap<>();
                    if (dataSnapshot.hasChild("name_event")){
                        final String waveName = dataSnapshot.child("name_event").getValue().toString();

                        Query lastQuery = firebaseDatabase.getReference().child("events_us")
                                .child(mWaveID)
                                .child("wall")
                                .child("posts");
                        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                if (dataSnapshot2.hasChildren()){
                                    for (DataSnapshot postSnapshot : dataSnapshot2.getChildren()){
                                        HashMap<String, String> postInfo = new HashMap<>();
                                        String postID = postSnapshot.getKey();
                                        postInfo.put("waveName", waveName);
                                        postInfo.put("waveID", mWaveID);
                                        postInfo.put("postID", postID);
                                        if (postSnapshot.hasChild("from")){
                                            postInfo.put("postFrom", postSnapshot.child("from").getValue().toString());
                                            postInfo.put("postFromUsername", postSnapshot.child("fromUsername").getValue().toString());
                                            postInfo.put("postMessage", postSnapshot.child("message").getValue().toString());
                                            postInfo.put("postMessage2", postSnapshot.child("message2").getValue().toString());
                                            postInfo.put("postEchos", String.valueOf(postSnapshot.child("echos").getChildrenCount()));
                                            postInfo.put("postComments", String.valueOf(postSnapshot.child("comments").getChildrenCount()));
                                            postInfo.put("postTime", String.valueOf(postSnapshot.child("time").getValue()));
                                            postInfo.put("postType", postSnapshot.child("type").getValue().toString());

                                            if (postSnapshot.hasChild("permanent")){
                                                postInfo.put("permanent", postSnapshot.child("permanent").getValue().toString());
                                            }else {
                                                postInfo.put("permanent", "");
                                            }
                                        }else {
                                            postInfo.put("postFrom", "Error.. My bad.");
                                            postInfo.put("postFromUsername",  ":(");
                                            postInfo.put("postMessage",  ":(");
                                            postInfo.put("postMessage2",  ":(");
                                            postInfo.put("postEchos",  ":(");
                                            postInfo.put("postComments",  ":(");
                                            postInfo.put("postTime",  ":(");
                                            postInfo.put("postType",  "error");
                                            if (dataSnapshot2.hasChild("permanent")){
                                                postInfo.put("permanent", dataSnapshot2.child("permanent").getValue().toString());
                                            }else {
                                                postInfo.put("permanent", "");
                                            }
                                        }


                                        posts.add(postInfo);
                                    }
                                    Collections.reverse(posts);
                                    adapter.notifyDataSetChanged();

                                    inflater = LayoutInflater.from(context);

                                }
                                UINotificationHelpers.showProgress(false,waveRecyclerView, mprogressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("MY_WAVES", databaseError.getMessage());
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        private HashMap<String, Integer> record;
        public PostsAdapter(final Context context, final String mWaveID){
            this.context = context;
            this.mWaveID = mWaveID;
            reload();
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_brief_main, parent, false);
            PostViewHolder holder = new PostViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final PostViewHolder holder, final int pos) {


            final int position = getItemViewType(pos);
            final String postID = posts.get(position).get("postID");
            final String postFromUsername = posts.get(position).get("postFromUsername");
            final String postMessage = posts.get(position).get("postMessage");
            final String postMessage2 = posts.get(position).get("postMessage2");
            final String waveName = posts.get(position).get("waveName");
            final String waveID = posts.get(position).get("waveID");
            final String postNumEchos = posts.get(position).get("postEchos");
            final String postNumComments = posts.get(position).get("postComments");
            final String postFrom = posts.get(position).get("postFrom");
            final String postType = posts.get(position).get("postType");
            final String postTime = posts.get(position).get("postTime");
            final String permanent = posts.get(position).get("permanent");

            holder.waveName.setText(postFromUsername);
            holder.postMessage.setText(postMessage);
            holder.postEchos.setText(postNumEchos);
            holder.postComments.setText(postNumComments);
            holder.source.setVisibility(View.INVISIBLE);


            if (postType.equals("image")) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.postImage.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WaveOverviewActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = (int) convertPixelsToDp(displayMetrics.heightPixels, getApplicationContext());
                params.width = width;
                holder.postImage.setLayoutParams(params);
                picasso.load(postMessage2)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export)
                        .into(holder.postImage);

            }
            if(postType.equals("error")){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.postImage.getLayoutParams();
                params.height = 350;
                holder.postImage.setLayoutParams(params);
                ErrorMessageComposer.loadingPost(TAG, waveID, postID);
                holder.postLaunch.setVisibility(View.INVISIBLE);
                holder.postImage.setVisibility(View.VISIBLE);
                holder.postImage.setImageDrawable(ContextCompat.getDrawable(holder.postImage.getContext(), R.drawable.paradire_banner_error));
                return;
            }

            if (!TextUtils.isEmpty(permanent) && permanent.equals("true")){
                holder.postTimeToLiveIcon.setImageDrawable(ContextCompat.getDrawable(holder.postTimeToLiveIcon.getContext(),R.drawable.circle_holder_main_colors));
                holder.postTTL.setVisibility(View.INVISIBLE);
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
                                                        posts.remove(position);
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                            }else {
                                                if (24 - TimeUnit.MILLISECONDS.toHours(timeDifference) >=1){
                                                    holder.postTTL.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                                                }else if(60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference) >=1){
                                                    holder.postTTL.setText( String.format("%d m", 60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference)));
                                                }else {
                                                    holder.postTTL.setText("< 1m");
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
                            long timeDifference = System.currentTimeMillis() -  Long.valueOf(postTime);
                            if (TimeUnit.MILLISECONDS.toHours(timeDifference) >= 24){
                                DatabaseReference db3 = firebase.getEvents(waveID, "wall", "posts", postID);
                                db3.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        posts.remove(position);
                                        notifyDataSetChanged();
                                    }
                                });
                            }else {
                                if (24 - TimeUnit.MILLISECONDS.toHours(timeDifference) >=1){
                                    holder.postTTL.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                                }else if(60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference) >=1){
                                    holder.postTTL.setText( String.format("%d m", 60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference)));
                                }else {
                                    holder.postTTL.setText("< 1m");
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

            if (postType.equals("youtube")){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.postImage.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WaveOverviewActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = (int) convertPixelsToDp(displayMetrics.heightPixels, getApplicationContext());
                params.width = width;
                holder.postImage.setLayoutParams(params);
                picasso.load(YoutubeHelpers.getVideoThumbnail(postMessage2))
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export)
                        .into(holder.postImage);
                holder.source.setVisibility(View.VISIBLE);
            }

            if (postType.equals("link")){
                holder.postImage.setVisibility(View.VISIBLE);
                if (postMessage2.contains("@@@@@@")){
                    String[] linkValues = postMessage2.split("@@@@@@");
                    String linkTitle = linkValues[1];

                    String linkImage = linkValues[2];
                    picasso.load(linkImage)
                            .fit()
                            .centerInside()
                            .placeholder(R.drawable.baseline_person_black_24).into(holder.postImage);
                    TextView websiteTitle = new TextView(holder.briefConstraintL.getContext());
                    websiteTitle.setText(linkTitle);
                    websiteTitle.setId(0);
                    websiteTitle.setTextColor(Color.WHITE);
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        websiteTitle.setBackgroundDrawable(ContextCompat.getDrawable(websiteTitle.getContext(), R.drawable.circle_holder_gray) );
                    } else {
                        websiteTitle.setBackground(ContextCompat.getDrawable(websiteTitle.getContext(), R.drawable.circle_holder_gray));
                    }

                    holder.briefConstraintL.addView(websiteTitle);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(holder.briefConstraintL);
                    constraintSet.connect(websiteTitle.getId(),ConstraintSet.BOTTOM, holder.postImage.getId(),ConstraintSet.BOTTOM,3);
                    constraintSet.connect(websiteTitle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 3);
                    constraintSet.connect(websiteTitle.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 3);

                    constraintSet.applyTo(holder.briefConstraintL);
                    holder.source.setImageDrawable(ContextCompat.getDrawable(holder.source.getContext(), R.drawable.baseline_link_black_24));
                    holder.source.setVisibility(View.VISIBLE);

                }
            }


            DatabaseReference databaseReferenceWave = firebase.get_user(postFrom, "profile_picture");
            databaseReferenceWave.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("default")){
                        picasso.load(dataSnapshot.getValue().toString())
                                .fit()
                                .centerInside()
                                .into(holder.postWaveThumbnail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference personalTableGet = firebase.get_user_authId("echos", appManager.getWaveM().getEventID());

            personalTableGet.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot mainDataSnapshot) {
                    try{
                        if (mainDataSnapshot.hasChild(postID)) {
                            holder.postEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_black_24));
                        }else {
                            holder.postEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_panorama_fish_eye_black_24));
                        }
                    }catch (Exception e){
                        // TODO Is this bad for the long run?
                        Log.d("For now..", e.getMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference databaseReferencePost = firebase.getEvents(waveID, "wall", "posts", postID);
            databaseReferencePost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        String numEchos = String.valueOf(dataSnapshot.child("echos").getChildrenCount());
                        String numComments =  String.valueOf(dataSnapshot.child("comments").getChildrenCount());
                        posts.get(position).put("postEchos", numEchos);
                        posts.get(position).put("postComments", numComments);

                        holder.postEchos.setText(numEchos);
                        holder.postComments.setText(numComments);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


}

