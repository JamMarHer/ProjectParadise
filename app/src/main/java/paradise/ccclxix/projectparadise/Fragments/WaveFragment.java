package paradise.ccclxix.projectparadise.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePost;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.VerticalViewPager;


public class WaveFragment extends HolderFragment implements EnhancedFragment {


    private CredentialsManager credentialsManager;

    private TextView currentWave;
    private Button postToWall;
    private EditText messageToPostToWall;
    private LinearLayout wavePostModule;
    private ConstraintLayout wavePostModuleButtons;

    private ImageView waveShowPost;


    private AppModeManager appModeManager;


    View generalView;
    EventManager eventManager;

    RecyclerView wavePostsList;
    boolean showing = false;

    private FirebaseAuth mAuth;
    private ViewGroup container;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        eventManager = new EventManager(getContext());
        appModeManager = new AppModeManager(getContext());
        credentialsManager = new CredentialsManager(getContext());
   }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wave, null);

        generalView = view;

        postToWall = view.findViewById(R.id.post_to_wall);
        messageToPostToWall = view.findViewById(R.id.message_to_post);
        currentWave = view.findViewById(R.id.current_wave);
        waveShowPost = view.findViewById(R.id.waveAddPostShow);
        wavePostModule = view.findViewById(R.id.wavePostModule);
        wavePostModuleButtons = view.findViewById(R.id.wavePostModuleButtons);


        if (eventManager.getEventID() !=null){
            currentWave.setText(eventManager.getEventName());
        }else {
            currentWave.setText("No wave found :/");
        }

        this.container = container;


        final VerticalViewPager verticalViewPager = view.findViewById(R.id.wave_post_viewpager);
        final FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager());

        final ArrayList<String> record = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbPostsReference = firebaseDatabase.getReference()
                .child("events_us")
                .child(eventManager.getEventID())
                .child("wall")
                .child("posts");
        dbPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for (final DataSnapshot post :dataSnapshot.getChildren()){
                        if(!record.contains(post.getKey())){
                            Bundle postInfo = new Bundle();
                            postInfo.putString("postID", post.getKey());
                            postInfo.putString("username", post.child("fromUsername").getValue().toString());
                            postInfo.putString("from", post.child("from").getValue().toString());
                            postInfo.putString("message", post.child("message").getValue().toString());
                            postInfo.putString("message2", post.child("message2").getValue().toString());
                            postInfo.putString("time", post.child("time").getValue().toString());
                            postInfo.putString("type", post.child("type").getValue().toString());
                            postInfo.putString("numEchos", String.valueOf(post.child("echos").getChildrenCount()));
                            postInfo.putString("numComments", String.valueOf(post.child("comments").getChildrenCount()));
                            record.add(post.getKey());
                            WavePost wavePost = new WavePost();
                            wavePost.setArguments(postInfo);
                            fragmentAdapter.addFragment(wavePost);
                        }
                    }
                    verticalViewPager.setAdapter(fragmentAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        postToWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!TextUtils.isEmpty(messageToPostToWall.getText()) & mAuth.getUid()!= null){
                    ResizeAnimation resizeAnimation = new ResizeAnimation(view, 260);
                    resizeAnimation.setDuration(999);
                    resizeAnimation.setRepeatCount(Animation.INFINITE);
                    resizeAnimation.setRepeatMode(Animation.REVERSE);
                    postToWall.setText("lit");
                    view.startAnimation(resizeAnimation);
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference dbPlainReference = firebaseDatabase.getReference();
                    DatabaseReference dbWave = dbPlainReference
                            .child("events_us")
                            .child(eventManager.getEventID())
                            .child("wall")
                            .child("posts")
                            .child(mAuth.getUid()).push();
                    String chatUserRef = "events_us/" + eventManager.getEventID() + "/wall/posts";
                    String pushID = dbWave.getKey();
                    Map postMap = new HashMap();
                    postMap.put("message", messageToPostToWall.getText().toString());
                    postMap.put("message2", "No Image"); // TODO For now.
                    postMap.put("seen", false);
                    postMap.put("numEchos", 0);
                    postMap.put("type", "text");
                    postMap.put("time", ServerValue.TIMESTAMP);
                    postMap.put("from", mAuth.getUid());
                    postMap.put("fromUsername", credentialsManager.getUsername());

                    Map postUserMap = new HashMap();
                    postUserMap.put(chatUserRef + "/"+ pushID, postMap);
                    dbPlainReference.updateChildren(postUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("POSTING_IN_WAVE", databaseError.getMessage());
                                TSnackbar snackbar = TSnackbar.make(container, "Something went wrong.", TSnackbar.LENGTH_SHORT);
                                snackbar.setActionTextColor(Color.WHITE);
                                snackbar.setIconLeft(R.drawable.poop_icon, 24);
                                View snackbarView = snackbar.getView();
                                snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                                TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);
                                snackbar.show();
                                view.clearAnimation();
                                postToWall.setText("Post");
                            }else{
                                messageToPostToWall.setText("");
                                view.clearAnimation();
                                postToWall.setText("Post");
                            }
                        }
                    });

                }
            }
        });
        wavePostModule.setVisibility(View.INVISIBLE);
        wavePostModuleButtons.setVisibility(View.INVISIBLE);
        waveShowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(!showing){
                    showing = true;
                    wavePostModule.setVisibility(View.VISIBLE);
                    wavePostModuleButtons.setVisibility(View.VISIBLE);
                    waveShowPost.setImageDrawable(getResources().getDrawable(R.drawable.baseline_minimize_white_24));

                }else {
                    showing = false;
                    wavePostModule.setVisibility(View.INVISIBLE);
                    wavePostModuleButtons.setVisibility(View.INVISIBLE);
                    waveShowPost.setImageDrawable(getResources().getDrawable(R.drawable.baseline_add_circle_outline_white_36));
                }
        }});

        return view;
    }

    static class FragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public FragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(mFragmentList.size() -1 - position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }



/*
    private class WavePostViewHolder extends RecyclerView.ViewHolder{

        TextView wavePostUsername;
        TextView wavePostMessage;
        TextView wavePostEchos;
        TextView wavePostTime;
        ImageView wavePostUserThumbnail;
        ImageView wavePostImage;
        ImageView wavePostEcho;
        View mView;

        public WavePostViewHolder(View itemView) {
            super(itemView);
            wavePostUsername = itemView.findViewById(R.id.wave_post_username);
            wavePostMessage = itemView.findViewById(R.id.wave_post_message);
            wavePostEchos = itemView.findViewById(R.id.wave_post_num_echos);
            wavePostTime = itemView.findViewById(R.id.wave_post_time);
            wavePostUserThumbnail = itemView.findViewById(R.id.wave_post_thumbnail);
            wavePostImage = itemView.findViewById(R.id.wave_post_image);
            wavePostEcho = itemView.findViewById(R.id.echoPost);
            mView = itemView;
        }
    }

    private class WavePostAdapter extends RecyclerView.Adapter<WavePostViewHolder> {

        private LayoutInflater inflater;
        private List<HashMap<String, String>> wavePostList;

        public WavePostAdapter(final Context context) {
            wavePostList = new ArrayList<>();
            if (eventManager.getEventID() != null) {

                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = firebaseDatabase.getReference()
                        .child("events_us")
                        .child(eventManager.getEventID())
                        .child("wall")
                        .child("posts");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        wavePostList.clear();
                        final HashMap<String, Integer> record = new HashMap<>();

                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final String postIDKey = postSnapshot.getKey();
                            DatabaseReference waveDBReference = firebaseDatabase.getReference()
                                    .child("events_us")
                                    .child(eventManager.getEventID())
                                    .child("wall")
                                    .child("posts")
                                    .child(postIDKey);
                            waveDBReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("from")) {

                                        HashMap<String, String> postInfo = new HashMap<>();
                                        postInfo.put("postID", postIDKey);
                                        postInfo.put("from", dataSnapshot.child("from").getValue().toString());
                                        postInfo.put("fromUsername", dataSnapshot.child("fromUsername").getValue().toString());

                                        postInfo.put("message", dataSnapshot.child("message").getValue().toString());
                                        postInfo.put("message2", dataSnapshot.child("message2").getValue().toString());
                                        postInfo.put("numEchos", String.valueOf(dataSnapshot.child("echos").getChildrenCount()));

                                        postInfo.put("type", dataSnapshot.child("type").getValue().toString());
                                        postInfo.put("time", dataSnapshot.child("time").getValue().toString());

                                        postInfo.put("seen", dataSnapshot.child("seen").getValue().toString());

                                        if (!record.containsKey(postIDKey)) {
                                            wavePostList.add(postInfo);
                                            record.put(postIDKey, wavePostList.size() - 1);
                                            if (postIDKey.equals(eventManager.getEventID())) {
                                                int toExchange = wavePostList.size() - 1;
                                                Collections.swap(wavePostList, 0, toExchange);
                                                record.put(postIDKey, 0);
                                                record.put(wavePostList.get(toExchange).get("waveID"), wavePostList.size() - 1);
                                            }
                                        } else {
                                            wavePostList.set(record.get(postIDKey), postInfo);
                                        }
                                        wavePostAdapter.notifyDataSetChanged();
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
                        Log.d("MY_WAVES", databaseError.getMessage());
                    }
                });
            }
        }


        @Override
        public WavePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.fragment_post, parent, false);
            WavePostViewHolder holder = new WavePostViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final WavePostViewHolder holder, int position) {
            if (wavePostList.isEmpty()) {
                holder.wavePostMessage.setText("This Wave doesn't have any posts. \nWanna be be first one ? ;D");
            } else {
                int listSize = wavePostList.size() - 1;
                final String postID = wavePostList.get(listSize - position).get("postID");
                final String postUsername = wavePostList.get(listSize - position).get("fromUsername");
                final String postType = wavePostList.get(listSize - position).get("type");
                final String postNumEchos = wavePostList.get(listSize - position).get("numEchos");
                final String postTime = wavePostList.get(listSize - position).get("time");
                final String postMessage = wavePostList.get(listSize - position).get("message");
                final String postMessage2 = wavePostList.get(listSize - position).get("message2");
                holder.wavePostUsername.setText(postUsername);
                holder.wavePostMessage.setText(postMessage);
                holder.wavePostEchos.setText(String.format("%s echos", postNumEchos));
                SimpleDateFormat formatedDate = new SimpleDateFormat("EEE, d MMM. hh:mm a");
                java.sql.Timestamp timestamp = new java.sql.Timestamp(Long.valueOf(postTime));
                try {
                    holder.wavePostTime.setText(formatedDate.format(timestamp));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                if (postType.equals("text")) {
                    holder.wavePostImage.setVisibility(View.INVISIBLE);
                }
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
                        if (mainDataSnapshot.hasChild(postID)) {
                            holder.wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_like_white));
                        }else {
                            holder.wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_not_like_white));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.wavePostEcho.setOnClickListener(new View.OnClickListener() {

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
                                                            holder.wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_like_white));

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
                                                            holder.wavePostEcho.setImageDrawable(getResources().getDrawable(R.drawable.heart_not_like_white));
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
            }
        }

        @Override
        public int getItemCount() {
            return wavePostList.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
    }
    */


    @Override
    public String getName() {
        return null;
    }
}
