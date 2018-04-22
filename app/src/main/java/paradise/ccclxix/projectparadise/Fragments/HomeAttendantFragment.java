package paradise.ccclxix.projectparadise.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.Loaders.StringLoader;
import paradise.ccclxix.projectparadise.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeAttendantFragment  extends HolderFragment implements EnhancedFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendant_home, null);
        return view;
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
