package paradise.ccclxix.projectparadise.Fragments;

import android.content.Intent;
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
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WaveAddPostActivity;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WavePost;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.VerticalViewPager;


public class WaveFragment extends HolderFragment implements EnhancedFragment {


    private CredentialsManager credentialsManager;

    private TextView currentWave;

    private ImageView waveShowPost;


    private AppModeManager appModeManager;


    View generalView;
    EventManager eventManager;


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

        currentWave = view.findViewById(R.id.current_wave);
        waveShowPost = view.findViewById(R.id.waveAddPostShow);


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

        if (eventManager.getEventID() != null){

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

        }
        waveShowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent =  new Intent(getActivity(), WaveAddPostActivity.class);
                getActivity().startActivity(intent);
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


    @Override
    public String getName() {
        return null;
    }
}
