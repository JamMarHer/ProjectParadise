package paradise.ccclxix.projectparadise.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.AttendantsInEvent;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.EventChat;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.OnGoingChats;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Settings.SettingsActivity;

public class ChatFragment extends AppCompatActivity {

    RecyclerView listAttendingUsers;

    AppManager appManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shares);

        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


                appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        ViewPager viewPager =  findViewById(R.id.viewpager);
        if(appManager.getModeM().isHostingMode() || appManager.getModeM().isAttendantMode()) {
            setUpAttending(viewPager);
            TabLayout tabs = findViewById(R.id.result_tabs);
            tabs.setupWithViewPager(viewPager);
        }else{
            setUpExploring(viewPager);
            TabLayout tabs = findViewById(R.id.result_tabs);
            tabs.setupWithViewPager(viewPager);
        }
    }

    public AppManager getAppManager() {
        return appManager;
    }

    private void setUpAttending(ViewPager viewPager) {

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new OnGoingChats(), "Contacts");
        adapter.addFragment(new AttendantsInEvent(), "Waving");
        adapter.addFragment(new EventChat(), "Wave Chat");
        viewPager.setAdapter(adapter);

    }

    private void setUpExploring(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new OnGoingChats(), "Chats");
        viewPager.setAdapter(adapter);

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
