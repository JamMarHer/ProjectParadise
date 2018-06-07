package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Transformations;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GeneralWaveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralWaveFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "param1";
    private static final String ARG_NAME = "param2";
    private static final String ARG_IMAGE = "param3";


    // TODO: Rename and change types of parameters
    private String mWaveID;
    private String mWaveName;
    private String mWaveImage;

    private FirebaseAuth mAuth;



    private TextView waveName;
    private ImageView waveThumbnail;
    private ImageView waveAddPost;

    PostsAdapter postsAdapter;
    RecyclerView postsRecyclerV;
    List<HashMap<String, String>> posts;
    AppManager appManager;


    Picasso picasso;


    public GeneralWaveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralWaveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralWaveFragment newInstance(String param1, String param2, String param3) {
        GeneralWaveFragment fragment = new GeneralWaveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, param1);
        args.putString(ARG_NAME, param2);
        args.putString(ARG_IMAGE, param3);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getActivity().getCacheDir(), 250000000))
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        if (getArguments() != null) {
            mWaveID = getArguments().getString(ARG_ID);
            mWaveName = getArguments().getString(ARG_NAME);
            mWaveImage = getArguments().getString(ARG_IMAGE);

        }
        mAuth = FirebaseAuth.getInstance();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflater1 = inflater.inflate(R.layout.fragment_general_wave, container, false);
        waveName = inflater1.findViewById(R.id.wave_general_name);
        waveThumbnail = inflater1.findViewById(R.id.wave_overview_thumbnail);
        waveAddPost = inflater1.findViewById(R.id.waveAddPostShow);
        postsRecyclerV = inflater1.findViewById(R.id.posts_recyclerView);
        postsAdapter = new PostsAdapter(getContext());
        postsRecyclerV.setAdapter(postsAdapter);

        postsRecyclerV.setLayoutManager(new LinearLayoutManager(getContext()));

        waveName.setText(mWaveName);

        if (mWaveImage !=null){
            picasso.load(mWaveImage)
                    .transform(Transformations.getScaleDownWithView(waveThumbnail))
                    .placeholder(R.drawable.idaelogo6_full).into(waveThumbnail);
        }


        waveAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent =  new Intent(getActivity(), WaveAddPostActivity.class);
                getActivity().startActivity(intent);
            }});



        return  inflater1;
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
        public PostsAdapter(final Context context){
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
                                    postsAdapter.notifyDataSetChanged();

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
                picasso.load(postMessage2)
                        .into(holder.postImage);

            }else {
                holder.postImage.setVisibility(View.INVISIBLE);
            }



            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();


            DatabaseReference databaseReferenceWave = firebaseDatabase1.getReference()
                    .child("users")
                    .child(postFrom)
                    .child("profile_picture");
            databaseReferenceWave.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals("default")){
                        picasso.load(dataSnapshot.getValue().toString())
                                .transform(Transformations.getScaleDownWithView(holder.postWaveThumbnail)).into(holder.postWaveThumbnail);
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




}
