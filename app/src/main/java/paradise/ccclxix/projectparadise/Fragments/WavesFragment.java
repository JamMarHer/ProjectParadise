package paradise.ccclxix.projectparadise.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.AttendantsInEvent;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.EventChat;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.OnGoingChats;
import paradise.ccclxix.projectparadise.Fragments.WavesRelated.ExploreWaves;
import paradise.ccclxix.projectparadise.Fragments.WavesRelated.MyWaves;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;

public class WavesFragment extends HolderFragment implements EnhancedFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waves, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager_waves);
        SharesFragment.FragmentAdapter adapter = new SharesFragment.FragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new MyWaves(), "My Waves");
        adapter.addFragment(new ExploreWaves(), "Explore");
        viewPager.setAdapter(adapter);
        TabLayout tabs = view.findViewById(R.id.result_tabs_waves);
        tabs.setupWithViewPager(viewPager);
        return view;

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
