package paradise.ccclxix.projectparadise.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;

public class HomeFragment extends HolderFragment implements EnhancedFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        /*ListView mainListView = (ListView)view.findViewById(R.id.list_comments);

        mainListView.setAdapter(loaderAdapter);

        Log.d("Home", "fragment");

        view.findViewById(R.id.forceload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StringLoader.ACTION);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
            }
        });
        */
        return view;
    }


    @Override
    public String getName() {
        return null;
    }
}
