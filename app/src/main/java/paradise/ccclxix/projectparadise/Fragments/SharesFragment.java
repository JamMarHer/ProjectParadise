package paradise.ccclxix.projectparadise.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.R;

public class SharesFragment extends HolderFragment implements EnhancedFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shares, null);
    }

    @Override
    public LoaderAdapter getLoaderAdapter() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
