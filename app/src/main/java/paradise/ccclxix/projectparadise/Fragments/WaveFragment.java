package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.FirebaseBuilder;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;


public class WaveFragment extends HolderFragment implements EnhancedFragment {

    public static final String TYPE = "WAVE_FRAGMENT";

    private ModeManager modeManager;

    FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();

    View generalView;
    AppManager appManager;


    private FirebaseBuilder firebase;
    private ViewGroup container;

    private PostsAdapter adapter;
    private RecyclerView waveRecyclerView;
    private List<Map<String, String>> posts;
    Picasso picasso;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getActivity().getCacheDir(), 250000000))
                .build();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();



        firebase = new FirebaseBuilder();
   }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wave, null);

        generalView = view;



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

            final DatabaseReference databaseReference = firebase.get("events_us", waveID);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        String members = String.valueOf(dataSnapshot.child("attending").getChildrenCount());
                        String posts = String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount());
                        String thumbnail = null;
                        String points = null;
                        String waveName = dataSnapshot.child("name_event").getValue().toString();
                        if (dataSnapshot.hasChild("image_url")){
                            thumbnail = dataSnapshot.child("image_url").getValue().toString();
                        }
                        if (dataSnapshot.hasChild("points")){
                            points = String.valueOf(dataSnapshot.child("points").getChildrenCount());
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return view;
    }




    private class PostViewHolder extends  RecyclerView.ViewHolder{
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
        }

    }




    private class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{

        private LayoutInflater inflater;


        private HashMap<String, Integer> record;
        public PostsAdapter(final Context context, final String mWaveID){
            posts = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            DatabaseReference waveDBReference = FirebaseDatabase.getInstance().getReference().child("events_us").child(mWaveID);
            waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    posts.clear();
                    record = new HashMap<>();
                    if (dataSnapshot.hasChildren()){
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
                                        postInfo.put("postEchos", postSnapshot.child("numEchos").getValue().toString());
                                        postInfo.put("postComments", String.valueOf(postSnapshot.child("comments").getChildrenCount()));
                                        postInfo.put("postTime", String.valueOf(postSnapshot.child("time").getValue()));
                                        postInfo.put("postType", postSnapshot.child("type").getValue().toString());

                                        posts.add(postInfo);
                                    }
                                    Collections.reverse(posts);
                                    adapter.notifyDataSetChanged();

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

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_brief_main, parent, false);
            PostViewHolder holder = new PostViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final PostViewHolder holder, int pos) {


            int position = getItemViewType(pos);
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

            holder.waveName.setText(postFromUsername);
            holder.postMessage.setText(postMessage);
            holder.postEchos.setText(postNumEchos);
            holder.postComments.setText(postNumComments);

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


            DatabaseReference databaseReferenceWave = firebaseDatabase1.getReference()
                    .child("users")
                    .child(postFrom)
                    .child("profile_picture");
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




        }
        @Override
        public int getItemCount () {
            return posts.size();
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }


    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


    @Override
    public String getName() {
        return null;
    }
}
