package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.AttendantsInEvent;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.EventChat;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.OnGoingChats;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Models.EventAttendingItem;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.User;

public class SharesFragment extends HolderFragment implements EnhancedFragment {

    RecyclerView listAttendingUsers;
    AppModeManager appModeManager;

    EventManager eventManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shares,container, false);
        ViewPager viewPager =  view.findViewById(R.id.viewpager);
        appModeManager = new AppModeManager(getContext());
        if(appModeManager.isHostingMode() || appModeManager.isAttendantMode()) {
            setUpAttending(viewPager);
            TabLayout tabs = view.findViewById(R.id.result_tabs);
            tabs.setupWithViewPager(viewPager);
            return view;
        }else{
            setUpExploring(viewPager);
            TabLayout tabs = view.findViewById(R.id.result_tabs);
            tabs.setupWithViewPager(viewPager);
            return view;
        }
    }

    private void setUpAttending(ViewPager viewPager) {

        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new OnGoingChats(), "Chats");
        adapter.addFragment(new AttendantsInEvent(), "Waving");
        adapter.addFragment(new EventChat(), "Wave Chat");
        viewPager.setAdapter(adapter);

    }

    private void setUpExploring(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new OnGoingChats(), "Chats");
        viewPager.setAdapter(adapter);

    }

    @Override
    public String getName() {
        return null;
    }



    static class FragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
