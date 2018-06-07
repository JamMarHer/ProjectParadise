package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
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

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.FirebaseBuilder;
import paradise.ccclxix.projectparadise.Fragments.PersonalRelated.EditProfileActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class PersonalFragment extends HolderFragment implements EnhancedFragment {


    private FirebaseBuilder firebase;

    private TextView personalUsername;
    private ImageView settingsImageView;
    private ImageView profilePicture;
    private TextView myNumWaves;
    private TextView myNumContacts;
    private TextView mStatus;
    private RecyclerView mPinnedWavesRecyclerV;
    private RecyclerView mHightlightedPostsRecyclerV;

    Button createWave;
    Button joinWave;
    View generalView;


    List<HashMap<String, String >> wavePinned;
    List<HashMap<String, String>> highlightPosts;
    WaveCardPinnedAdapter pinnedWavesAdapter;
    HighlightedPostsAdapter highlightedPostsAdapter;


    AppManager appManager;
    Picasso picasso;
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
        final View inflater1 = inflater.inflate(R.layout.fragment_my_waves, null);

        wavePinned = new ArrayList<>();
        settingsImageView = inflater1.findViewById(R.id.edit_profile);

        myNumWaves = inflater1.findViewById(R.id.numberWaves);
        myNumContacts = inflater1.findViewById(R.id.numberContacts);
        personalUsername = inflater1.findViewById(R.id.personal_username);
        profilePicture = inflater1.findViewById(R.id.profile_picture_personal);
        mStatus = inflater1.findViewById(R.id.personal_status);
        mPinnedWavesRecyclerV = inflater1.findViewById(R.id.pinned_waves_recyclerView);
        mHightlightedPostsRecyclerV = inflater1.findViewById(R.id.highlighted_posts_recyclerView);
        // TODO check for user logged in.



        createWave = inflater1.findViewById(R.id.createWave);
        joinWave = inflater1.findViewById(R.id.joinWave);


        createWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                getActivity().startActivity(intent);
            }
        });

        joinWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QRScannerActivity.class);
                startActivity(intent);
            }
        });

        generalView = inflater1;
        setupNumWavesAndContacts();


        if (appManager.getCredentialM() !=null){
            personalUsername.setText(appManager.getCredentialM().getUsername());

        }


        View settingsPopupView = inflater.inflate(R.layout.settings_popup, null);
        if (appManager.getWaveM().getEventID() == null){
            //TODO
        }


        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });



        // Views inside settings Popup window.
        final Button logoutButton = settingsPopupView.findViewById(R.id.logoutButton);



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.auth().signOut();
                Intent intent = new Intent(getContext(), InitialAcitivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });



        pinnedWavesAdapter = new WaveCardPinnedAdapter(getContext());
        highlightedPostsAdapter = new HighlightedPostsAdapter(getContext());
        mPinnedWavesRecyclerV.setAdapter(pinnedWavesAdapter);
        mHightlightedPostsRecyclerV.setAdapter(highlightedPostsAdapter);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        mPinnedWavesRecyclerV.setLayoutManager(layoutManager);
        mHightlightedPostsRecyclerV.setLayoutManager(new LinearLayoutManager(getContext()));
        mPinnedWavesRecyclerV.setItemViewCacheSize(20);
        mPinnedWavesRecyclerV.setDrawingCacheEnabled(true);
        mPinnedWavesRecyclerV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mHightlightedPostsRecyclerV.setItemViewCacheSize(20);
        mHightlightedPostsRecyclerV.setDrawingCacheEnabled(true);
        mHightlightedPostsRecyclerV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        return inflater1;

    }


    // TODO This can be highly optimized.
    private void setupNumWavesAndContacts(){
        if(firebase.auth().getCurrentUser() != null && firebase.auth().getUid() != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference userDBReference = firebase.get("users", firebase.auth().getUid(), "waves", "in");
            final DatabaseReference contacesDBReference = firebase.get("messages", firebase.auth().getUid());
            userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myNumWaves.setText(String.format("%s", String.valueOf(dataSnapshot.getChildrenCount())));
                    contacesDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myNumContacts.setText(String.format("%s", String.valueOf(dataSnapshot.getChildrenCount())));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference databaseReference = firebase.get_user("profile_picture");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){

                        picasso.load(dataSnapshot.getValue().toString())
                                .fit()
                                .centerInside()
                                .transform(Transformations.getScaleDownWithView(profilePicture))
                                .placeholder(R.drawable.idaelogo6_full).into(profilePicture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference = firebase.get_user("status");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        mStatus.setText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    public String getName() {
        return null;
    }



    private class WaveCardViewHolder extends RecyclerView.ViewHolder{

        TextView waveName;
        ImageView waveThumbnail;
        View mView;

        public WaveCardViewHolder(View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_single_card_name);
            waveThumbnail = itemView.findViewById(R.id.main_wave_thumbnail);

            mView = itemView;
        }
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

        ConstraintLayout briefConstraintL;

        public HighlightPostViewHolder(View itemView){
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_single_brief_name);
            postMessage = itemView.findViewById(R.id.wave_single_brief_message);
            postEchos = itemView.findViewById(R.id.wave_single_brief_echos);
            postComments = itemView.findViewById(R.id.wave_single_brief_comments);
            postTTL = itemView.findViewById(R.id.wave_single_brief_time_to_live);
            postImage = itemView.findViewById(R.id.wave_post_image);
            postLaunch = itemView.findViewById(R.id.wave_single_brief_launch);
            briefConstraintL = itemView.findViewById(R.id.wave_single_brief);
            postWaveThumbnail = itemView.findViewById(R.id.wave_single_brief_wave_thumbnail);
        }

    }




    private class HighlightedPostsAdapter extends RecyclerView.Adapter<HighlightPostViewHolder>{

        private LayoutInflater inflater;


        private HashMap<String, Integer> record;
        public HighlightedPostsAdapter(final Context context){
            highlightPosts = new ArrayList<>();
            final DatabaseReference databaseReference = firebase.get_user("waves", "in");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    highlightPosts.clear();
                    record = new HashMap<>();

                    for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                        final String waveID = wave.getKey();

                        DatabaseReference waveDBReference = FirebaseDatabase.getInstance().getReference().child("events_us").child(waveID);
                        waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
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
                                                postInfo.put("postFrom", dataSnapshot2.child("from").getValue().toString());
                                                postInfo.put("postFromUsername", dataSnapshot2.child("fromUsername").getValue().toString());
                                                postInfo.put("postMessage", dataSnapshot2.child("message").getValue().toString());
                                                postInfo.put("postMessage2", dataSnapshot2.child("message2").getValue().toString());
                                                postInfo.put("postEchos", dataSnapshot2.child("numEchos").getValue().toString());
                                                postInfo.put("postComments", String.valueOf(dataSnapshot2.child("comments").getChildrenCount()));
                                                postInfo.put("postTime", String.valueOf(dataSnapshot2.child("time").getValue()));
                                                postInfo.put("postType", dataSnapshot2.child("type").getValue().toString());

                                                highlightPosts.add(postInfo);
                                                highlightedPostsAdapter.notifyDataSetChanged();
                                                inflater = LayoutInflater.from(context);
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("MY_WAVES", databaseError.getMessage());

                }
            });
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
        public void onBindViewHolder(final HighlightPostViewHolder holder, int position) {
            position = getItemViewType(position);
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

            holder.waveName.setText(waveName);
            holder.postMessage.setText(postMessage);
            holder.postEchos.setText(postNumEchos);
            holder.postComments.setText(postNumComments);

            if (postType.equals("image")) {
                picasso.load(postMessage2)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.idaelogo6_full).into(holder.postImage);

            }else {
                holder.postImage.setVisibility(View.INVISIBLE);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(holder.briefConstraintL);
                constraintSet.connect(holder.postMessage.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,ConstraintSet.END, 0);
                constraintSet.connect(holder.postLaunch.getId(), ConstraintSet.TOP, holder.postMessage.getId(), ConstraintSet.BOTTOM, 3);
                constraintSet.applyTo(holder.briefConstraintL);

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
                                .placeholder(R.drawable.idaelogo6_full).into(holder.postWaveThumbnail);
                    }
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

        private class WaveCardPinnedAdapter extends RecyclerView.Adapter<WaveCardViewHolder>{

        private LayoutInflater inflater;


        private HashMap<String, Integer> record;
        public WaveCardPinnedAdapter(final Context context){
            wavePinned = new ArrayList<>();
            final DatabaseReference databaseReference = firebase.get_user("waves", "pinned");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    wavePinned.clear();
                    record = new HashMap<>();

                    for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                        final String waveID = wave.getKey();
                        DatabaseReference waveDBReference = firebase.get("events_us", waveID);
                        waveDBReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int waveAttending = (int)dataSnapshot.child("attending").getChildrenCount();
                                HashMap<String, String> eventInfo = new HashMap<>();
                                eventInfo.put("waveID", waveID);
                                eventInfo.put("waveName", dataSnapshot.child("name_event").getValue().toString());
                                // TODO add function (algo) for trending.

                                if (dataSnapshot.hasChild("image_url")){
                                    eventInfo.put("waveImageURL", dataSnapshot.child("image_url").getValue().toString());
                                }else {
                                    eventInfo.put("waveImageURL", null);
                                }

                                eventInfo.put("waveTrend", "trending");
                                eventInfo.put("wavePosts", String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount()));
                                eventInfo.put("waveAttending", String.valueOf(waveAttending));

                                if(!record.containsKey(waveID)) {
                                    wavePinned.add(eventInfo);
                                    record.put(waveID, wavePinned.size()-1);
                                    if(waveID.equals(appManager.getWaveM().getEventID())){
                                        int toExchange = wavePinned.size()-1;
                                        Collections.swap(wavePinned,0, toExchange);
                                        record.put(waveID, 0);
                                        record.put(wavePinned.get(toExchange).get("waveID"), wavePinned.size()-1);
                                    }
                                }else {
                                    wavePinned.set(record.get(waveID), eventInfo);
                                }
                                pinnedWavesAdapter.notifyDataSetChanged();
                                inflater = LayoutInflater.from(context);
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
                    Log.d("MY_WAVES", databaseError.getMessage());

                }
            });
        }



        @Override
        public WaveCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_card, parent, false);
            WaveCardViewHolder holder = new WaveCardViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final WaveCardViewHolder holder, final int position) {
            final String waveID = wavePinned.get(position).get("waveID");
            final String waveName = wavePinned.get(position).get("waveName");
            final String waveImageURL = wavePinned.get(position).get("waveImageURL");
            final String waveAttending = wavePinned.get(position).get("waveAttending");
            final String waveTrend = wavePinned.get(position).get("waveTrend");
            final long currentWavePostsNum  = Long.valueOf(wavePinned.get(position).get("wavePosts"));
            holder.waveName.setText(waveName);
            if (waveImageURL != null){
                picasso.load(waveImageURL)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.idaelogo6_full).into(holder.waveThumbnail);
            }

            holder.waveThumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (waveID.equals(appManager.getWaveM().getEventID())){
                        showTopSnackBar(getView(), "You are already riding this wave.", Icons.POOP);
                    }else {
                        appManager.getWaveM().updateEventID(waveID);
                        appManager.getWaveM().updateEventName(waveName);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("source", "joined_event");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            });


        }


        @Override
        public int getItemCount() {
            return wavePinned.size();
        }
    }


    public void showTopSnackBar(View view, String message, int icon){
        TSnackbar snackbar = TSnackbar.make(view, "You are already riding this wave.", TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(icon, 24);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
