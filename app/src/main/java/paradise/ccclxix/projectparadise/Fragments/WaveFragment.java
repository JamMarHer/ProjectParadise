package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavesSubscribeActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WaveAddPostActivity;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePostActivity;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePostCommentsActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;
import paradise.ccclxix.projectparadise.utils.YoutubeHelpers;


public class WaveFragment extends HolderFragment implements EnhancedFragment {

    public static final String TYPE = "WAVE_FRAGMENT";

    private ModeManager modeManager;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    View generalView;
    AppManager appManager;
    boolean working = false;


    private ViewGroup container;

    private PostsAdapter adapter;
    private RecyclerView waveRecyclerView;
    private ImageView waveThumbnail;
    private TextView waveName;
    private ImageView waveAddPost;
    private Button addSourceBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;


    private List<Map<String, String>> posts;
    Picasso picasso;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wave, null);

        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getActivity().getCacheDir(), 250000000))
                .build();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }else {
            appManager = new AppManager();
            appManager.initialize(getContext());
        }
        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();

        firebase = new FirebaseBuilder();
        generalView = view;
        waveName = view.findViewById(R.id.main_wave_name);
        waveThumbnail = view.findViewById(R.id.main_wave_thumbnail);
        waveAddPost = view.findViewById(R.id.main_add_post);
        addSourceBtn = view.findViewById(R.id.integrate_source_btn);
        swipeRefreshLayout = view.findViewById(R.id.wave_swipe_layout);
        progressBar = view.findViewById(R.id.progressbarWave);


        this.container = container;
        waveRecyclerView = view.findViewById(R.id.main_wave_recyclerView);
        waveRecyclerView.setHasFixedSize(false);
        waveRecyclerView.setItemViewCacheSize(20);
        waveRecyclerView.setDrawingCacheEnabled(true);
        waveRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        final ArrayList<String> record = new ArrayList<>();

        final String waveID = appManager.getWaveM().getEventID();

        adapter = new PostsAdapter(getContext(), waveID);
        waveRecyclerView.setAdapter(adapter);
        waveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (waveID != null){

            final DatabaseReference databaseReference = firebase.getEvents(waveID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("name_event")){
                        String members = String.valueOf(dataSnapshot.child("attending").getChildrenCount());
                        String posts = String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount());
                        String thumbnail = null;
                        String points = null;
                        String name = dataSnapshot.child("name_event").getValue().toString();
                        if (dataSnapshot.hasChild("image_url")){
                            thumbnail = dataSnapshot.child("image_url").getValue().toString();
                            picasso.load(thumbnail)
                                    .fit()
                                    .centerInside()
                                    .into(waveThumbnail);
                        }
                        if (dataSnapshot.hasChild("points")){
                            points = String.valueOf(dataSnapshot.child("points").getChildrenCount());
                        }

                        waveName.setText(name);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UINotificationHelpers.showProgress(true,waveRecyclerView, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));
                waveRecyclerView.removeAllViews();
                adapter.reload();


                swipeRefreshLayout.setRefreshing(false);

            }
        });

        waveAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getActivity(), WaveAddPostActivity.class);
                getActivity().startActivity(intent);
            }
        });
        addSourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getActivity(), WavesSubscribeActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
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

                                        posts.add(postInfo);
                                    }
                                    Collections.reverse(posts);
                                    adapter.notifyDataSetChanged();

                                    inflater = LayoutInflater.from(context);

                                }
                                UINotificationHelpers.showProgress(false,waveRecyclerView, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

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
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = (int) convertPixelsToDp(displayMetrics.heightPixels, getContext());
                params.width = width;
                holder.postImage.setLayoutParams(params);
                picasso.load(postMessage2)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export)
                        .into(holder.postImage);

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
                                                if (TimeUnit.MILLISECONDS.toHours(timeDifference) < 24){
                                                    holder.postTTL.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                                                }else if(TimeUnit.MILLISECONDS.toMinutes(timeDifference) < 60){
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
                                if (24 - TimeUnit.MILLISECONDS.toHours(timeDifference) >=0){
                                    System.out.println(TimeUnit.MILLISECONDS.toHours(timeDifference));
                                    holder.postTTL.setText( String.format("%d h", 24 - TimeUnit.MILLISECONDS.toHours(timeDifference)));
                                }else if(60 - TimeUnit.MILLISECONDS.toMinutes(timeDifference) >=0){
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
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                int height = (int) convertPixelsToDp(displayMetrics.heightPixels, getContext());
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



            View.OnClickListener launchPostListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WavePostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("waveID", waveID);
                    bundle.putString("postID", postID);
                    bundle.putString("username", postFromUsername);
                    bundle.putString("message", postMessage);
                    bundle.putString("message2", postMessage2);
                    bundle.putString("numEchos", postNumEchos);
                    bundle.putString("numComments", postNumComments);
                    bundle.putString("permanent", permanent);
                    bundle.putString("time", postTime);
                    bundle.putString("type", postType);
                    bundle.putString("from", postFrom);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            };


            holder.postLaunch.setOnClickListener(launchPostListener);
            holder.postImage.setOnClickListener(launchPostListener);


            holder.postEcho.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (!working) {
                        working = true;

                        DatabaseReference personalTableGet = firebase.get_user_authId("echos", appManager.getWaveM().getEventID());
                        personalTableGet.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot mainDataSnapshot) {
                                if (!mainDataSnapshot.hasChild(postID)) {

                                    DatabaseReference dbWavePostEchos = firebase.getEvents(appManager.getWaveM().getEventID(),
                                            "wall", "posts", postID, "echos", firebase.auth_id()).push();
                                    String chatUserRef = "events_us/" + appManager.getWaveM().getEventID() + "/wall/posts/" + postID + "/echos";

                                    final DatabaseReference dbWave = firebase.getEvents(appManager.getWaveM().getEventID());
                                    final DatabaseReference dbWaveb = firebase.getEvents(appManager.getWaveM().getEventID(),
                                            "wall", "posts", postID);

                                    dbWave.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild("attending")){
                                                final long countEchos = dataSnapshot.child("attending").getChildrenCount();
                                                dbWaveb.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                                        if (dataSnapshot2.hasChild("echos")){
                                                            if (dataSnapshot2.child("echos").getChildrenCount()/3 <= countEchos+1){
                                                                if (!dataSnapshot2.hasChild("permanent")){
                                                                    dbWaveb.child("permanent").setValue("true");
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    final String pushID = dbWavePostEchos.getKey();
                                    Map postMap = new HashMap();
                                    postMap.put("from", firebase.auth_id());
                                    postMap.put("pushID", pushID);
                                    postMap.put("fromUsername", appManager.getCredentialM().getUsername()); // TODO For now.
                                    postMap.put("time", ServerValue.TIMESTAMP);

                                    Map postUserMap = new HashMap();
                                    postUserMap.put(chatUserRef + "/" + pushID, postMap);
                                    firebase.getDatabase().updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                DatabaseReference personalTable = firebase.get_user_authId("echos",
                                                        appManager.getWaveM().getEventID(), postID);
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
                                                            holder.postEcho.setImageDrawable(getResources().getDrawable(R.drawable.baseline_lens_black_24));

                                                        }
                                                    }
                                                });
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

            holder.postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WavePostCommentsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("postID", postID);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
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
    @Override
    public String getName() {
        return null;
    }
}
