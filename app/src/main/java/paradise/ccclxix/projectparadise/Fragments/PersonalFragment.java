package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.PinnedWavesActivity;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePostActivity;
import paradise.ccclxix.projectparadise.utils.ErrorMessageComposer;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.Fragments.PersonalRelated.EditProfileActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.YoutubeHelpers;

public class PersonalFragment extends HolderFragment implements EnhancedFragment {

    private static String TAG = "PERSONAL_FRAGMENT";

    private FirebaseBuilder firebase = new FirebaseBuilder();

    private TextView personalUsername;
    private ImageView profilePicture;
    private ImageView nothingToShow;
    private TextView myNumWaves;
    private TextView myNumContacts;
    private TextView mStatus;
    private TextView mNumVerified;
    private RecyclerView mHightlightedPostsRecyclerV;

    View generalView;


    List<HashMap<String, String>> highlightPosts;
    HighlightedPostsAdapter highlightedPostsAdapter;


    AppManager appManager;
    Picasso picasso;
    SnackBar snackbar = new SnackBar();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getActivity().getCacheDir(), 25000000))
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }else {
            appManager = new AppManager();
            appManager.initialize(getContext());
        }
        firebase = new FirebaseBuilder();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflater1 = inflater.inflate(R.layout.fragment_personal, null);


        myNumWaves = inflater1.findViewById(R.id.numberWaves);
        // TODO we ignore num contacts since in this stage is irrelevant.
        //myNumContacts = inflater1.findViewById(R.id.numberContacts);
        personalUsername = inflater1.findViewById(R.id.personal_username);
        profilePicture = inflater1.findViewById(R.id.profile_picture_personal);
        mStatus = inflater1.findViewById(R.id.personal_status);
        mHightlightedPostsRecyclerV = inflater1.findViewById(R.id.highlighted_posts_recyclerView);
        mNumVerified = inflater1.findViewById(R.id.numberVerified);
        nothingToShow = inflater1.findViewById(R.id.nothing_to_show_image);

        generalView = inflater1;
        setupUserCard();


        if (appManager.getCredentialM() !=null){
            personalUsername.setText(appManager.getCredentialM().getUsername());

        }



        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });


        myNumWaves.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), PinnedWavesActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });



        // Views inside settings Popup window.







        highlightedPostsAdapter = new HighlightedPostsAdapter(getContext());
        mHightlightedPostsRecyclerV.setAdapter(highlightedPostsAdapter);
        mHightlightedPostsRecyclerV.setLayoutManager(new LinearLayoutManager(getContext()));
        mHightlightedPostsRecyclerV.setItemViewCacheSize(20);
        mHightlightedPostsRecyclerV.setDrawingCacheEnabled(true);
        mHightlightedPostsRecyclerV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        return inflater1;

    }


    // TODO This can be highly optimized.
    private void setupUserCard(){
        appManager.getCredentialM().setDataChangedListener(new CredentialsManager.DataChangedListener() {
            @Override
            public void onDataChanged(boolean key) {
                if (key){
                    personalUsername.setText(appManager.getCredentialM().getUsername());
                    mStatus.setText(appManager.getCredentialM().getStatus());
                    //myNumContacts.setText(appManager.getCredentialM().getNumContacts());
                    myNumWaves.setText(appManager.getCredentialM().getNumWaves());
                    mNumVerified.setText(appManager.getCredentialM().getNumPermanents());
                    String thumbnailURL = appManager.getCredentialM().getProfilePic();
                    if (!TextUtils.isEmpty(thumbnailURL)) {
                        picasso.load(thumbnailURL)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.ic_import_export).into(profilePicture);
                    }
                }

            }
        });
    }


    @Override
    public String getName() {
        return null;
    }




    private class HighlightPostViewHolder extends  RecyclerView.ViewHolder{
        TextView waveName;
        TextView postMessage;
        TextView postEchos;
        TextView postComments;
        TextView postTTL;

        ImageView postImage;
        ImageView postLaunch;
        ImageView postWaveThumbnail;
        ImageView postFromThumbnail;
        ImageView postTimeToLiveIcon;
        ImageView source;

        ConstraintLayout briefConstraintL;

        public HighlightPostViewHolder(View itemView){
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_single_brief_name);
            postMessage = itemView.findViewById(R.id.wave_single_brief_message);
            //postEchos = itemView.findViewById(R.id.wave_single_brief_echos);
            //postComments = itemView.findViewById(R.id.wave_single_brief_comments);
            postTTL = itemView.findViewById(R.id.wave_single_brief_time_to_live);
            postImage = itemView.findViewById(R.id.wave_post_image);
            postLaunch = itemView.findViewById(R.id.wave_single_brief_launch);
            briefConstraintL = itemView.findViewById(R.id.wave_single_brief);
            postWaveThumbnail = itemView.findViewById(R.id.wave_single_brief_wave_thumbnail);
            postTimeToLiveIcon = itemView.findViewById(R.id.wave_post_time_to_live_icon);

            source = itemView.findViewById(R.id.wave_single_brief_source);
        }

    }


    private class HighlightedPostsAdapter extends RecyclerView.Adapter<HighlightPostViewHolder>{

        private LayoutInflater inflater;
        private HashMap<String, Integer> record;

        public HighlightedPostsAdapter(final Context context){
            highlightPosts = new ArrayList<>();
            final DatabaseReference databaseReference = firebase.get_user_authId("waves", "pinned");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mHightlightedPostsRecyclerV.removeAllViews();
                    updateOnChange(dataSnapshot, getContext());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("MY_WAVES", databaseError.getMessage());

                }
            });
        }

        private void updateOnChange(DataSnapshot dataSnapshot, final Context context){
            highlightPosts.clear();
            record = new HashMap<>();
            if (dataSnapshot.hasChildren()){
                nothingToShow.setVisibility(View.INVISIBLE);
                for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                    final String waveID = wave.getKey();
                    DatabaseReference waveDBReference = FirebaseDatabase.getInstance().getReference().child("events_us").child(waveID);
                    waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()){

                                final String waveName = dataSnapshot.child("name_event").getValue().toString();

                                Query lastQuery = firebase.get("events_us", waveID, "wall", "posts").orderByKey().limitToLast(1);
                                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        HashMap<String, String> postInfo = new HashMap<>();
                                        if (dataSnapshot2.hasChildren()){

                                            String postID = dataSnapshot2.getChildren().iterator().next().getKey();
                                            dataSnapshot2 = dataSnapshot2.child(postID);
                                            postInfo.put("waveName", waveName);
                                            postInfo.put("waveID", waveID);
                                            postInfo.put("postID", postID);
                                            if (dataSnapshot2.hasChild("from")){
                                                postInfo.put("postFrom", dataSnapshot2.child("from").getValue().toString());
                                                postInfo.put("postFromUsername", dataSnapshot2.child("fromUsername").getValue().toString());
                                                postInfo.put("postMessage", dataSnapshot2.child("message").getValue().toString());
                                                postInfo.put("postMessage2", dataSnapshot2.child("message2").getValue().toString());
                                                postInfo.put("postEchos", String.valueOf(dataSnapshot2.child("echos").getChildrenCount()));
                                                postInfo.put("postComments", String.valueOf(dataSnapshot2.child("comments").getChildrenCount()));
                                                postInfo.put("postTime", String.valueOf(dataSnapshot2.child("time").getValue()));
                                                postInfo.put("postType", dataSnapshot2.child("type").getValue().toString());
                                                if (dataSnapshot2.hasChild("permanent")){
                                                    postInfo.put("permanent", dataSnapshot2.child("permanent").getValue().toString());
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

                                            highlightPosts.add(postInfo);
                                            highlightedPostsAdapter.notifyDataSetChanged();
                                            // TODO Yikes.......
                                            try {
                                                inflater = LayoutInflater.from(context);

                                            }catch (Exception e){

                                            }
                                        }
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
            }else {
                mHightlightedPostsRecyclerV.removeAllViews();
                nothingToShow.setVisibility(View.VISIBLE);
            }


        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public HighlightPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_brief, parent, false);
            HighlightPostViewHolder holder = new HighlightPostViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final HighlightPostViewHolder holder, int pos) {
            final int position = getItemViewType(pos);
            final String postID = highlightPosts.get(position).get("postID");
            final String postFromUsername = highlightPosts.get(position).get("postFromUsername");
            final String postMessage = highlightPosts.get(position).get("postMessage");
            final String postMessage2 = highlightPosts.get(position).get("postMessage2");
            final String waveName = highlightPosts.get(position).get("waveName");
            final String waveID = highlightPosts.get(position).get("waveID");
            final String postNumEchos = highlightPosts.get(position).get("postEchos");
            final String postNumComments = highlightPosts.get(position).get("postComments");
            final String postFrom = highlightPosts.get(position).get("postFrom");
            final String postType = highlightPosts.get(position).get("postType");
            final String postTime = highlightPosts.get(position).get("postTime");
            final String permanent = highlightPosts.get(position).get("permanent");


            holder.waveName.setText(waveName);
            holder.postMessage.setText(postMessage);
            //holder.postEchos.setText(postNumEchos);
            //holder.postComments.setText(postNumComments);
            holder.postImage.setVisibility(View.INVISIBLE);
            holder.source.setVisibility(View.INVISIBLE);

            if (postType.equals("image")) {
                holder.postImage.setVisibility(View.VISIBLE);
                picasso.load(postMessage2)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(holder.postImage);

            }else if(postType.equals("youtube")){
                holder.postImage.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.postImage.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                params.width = width;
                holder.postImage.setLayoutParams(params);
                picasso.load(YoutubeHelpers.getVideoThumbnail(postMessage2))
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export)
                        .into(holder.postImage);
                holder.source.setVisibility(View.VISIBLE);
            } else if (postType.equals("link")){
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
            }else  if(postType.equals("error")){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.postImage.getLayoutParams();
                params.height = 350;
                holder.postImage.setLayoutParams(params);
                ErrorMessageComposer.loadingPost(TAG, waveID, postID);
                holder.postLaunch.setVisibility(View.INVISIBLE);
                holder.postImage.setVisibility(View.VISIBLE);
                holder.postImage.setImageDrawable(ContextCompat.getDrawable(holder.postImage.getContext(), R.drawable.paradire_banner_error));
                return;
            } else {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(holder.briefConstraintL);
                constraintSet.connect(holder.postMessage.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,ConstraintSet.END, 0);
                constraintSet.connect(holder.postLaunch.getId(), ConstraintSet.TOP, holder.postMessage.getId(), ConstraintSet.BOTTOM, 3);
                constraintSet.applyTo(holder.briefConstraintL);

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
                            Query lastQuery = db.child("echos").orderByKey().limitToLast(1);
                            lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()){
                                        if (child.hasChild("time")){
                                            long timeDifference = System.currentTimeMillis() -  Long.valueOf(child.child("time").getValue().toString());
                                            if (TimeUnit.MILLISECONDS.toHours(timeDifference) >= 24){
                                                DatabaseReference db3 = firebase.getEvents(waveID, "wall", "posts");
                                                db3.child(postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        highlightPosts.remove(position);
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
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            long timeDifference = System.currentTimeMillis() -  Long.valueOf(postTime);
                            if (TimeUnit.MILLISECONDS.toHours(timeDifference) >= 24){
                                DatabaseReference db3 = firebase.getEvents(waveID, "wall", "posts");
                                db3.child(postID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        highlightPosts.remove(position);

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

                    }
                });


            }

            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();

            DatabaseReference databaseReferenceWave = firebaseDatabase1.getReference()
                    .child("events_us")
                    .child(waveID)
                    .child("image_url");
            databaseReferenceWave.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        picasso.load(dataSnapshot.getValue().toString())
                                .fit()
                                .centerInside()
                                .placeholder(R.drawable.ic_import_export).into(holder.postWaveThumbnail);
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
                    bundle.putString("permanent", permanent);
                    bundle.putString("numComments", postNumComments);
                    bundle.putString("time", postTime);
                    bundle.putString("type", postType);
                    bundle.putString("from", postFrom);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            };

            if (!postType.equals("error")){
                holder.postLaunch.setOnClickListener(launchPostListener);
                holder.postImage.setOnClickListener(launchPostListener);
            }


            DatabaseReference db =  firebase.getEvents(waveID,"wall", "posts", postID);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //holder.postEchos.setText(String.valueOf(dataSnapshot.child("echos").getChildrenCount()));
                    //holder.postComments.setText(String.valueOf(dataSnapshot.child("comments").getChildrenCount()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        @Override
        public int getItemCount () {
            return highlightPosts.size();
        }
    }


}
