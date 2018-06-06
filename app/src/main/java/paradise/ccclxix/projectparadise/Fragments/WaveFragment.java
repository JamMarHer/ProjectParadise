package paradise.ccclxix.projectparadise.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.GeneralWaveFragment;
import paradise.ccclxix.projectparadise.Fragments.WaveRelated.WaveOverview;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.VerticalViewPager;


public class WaveFragment extends HolderFragment implements EnhancedFragment {

    public static final String TYPE = "WAVE_FRAGMENT";

    private ModeManager modeManager;


    View generalView;
    AppManager appManager;


    private FirebaseAuth mAuth;
    private ViewGroup container;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
   }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wave, null);

        generalView = view;



        this.container = container;

        final VerticalViewPager verticalViewPager = view.findViewById(R.id.wave_post_viewpager);
        final FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager());

        final ArrayList<String> record = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        final String waveID = appManager.getWaveM().getEventID();

        if (waveID != null){

            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(waveID);
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
                        fragmentAdapter.addFragment(GeneralWaveFragment.newInstance(waveID, waveName, thumbnail));

                        fragmentAdapter.addFragment(WaveOverview.newInstance(waveID,
                                waveName, members, posts, points, thumbnail));
                        verticalViewPager.setAdapter(fragmentAdapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
/*
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
        */


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
