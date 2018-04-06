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
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.Loaders.StringLoader;

public class HomeFragment extends HolderFragment implements EnhancedFragment {

    private LoaderAdapter loaderAdapter;
    private StringLoader stringLoader;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        loaderAdapter = new LoaderAdapter(getContext());
        getLoaderManager().initLoader(R.id.string_loader_id, null, loaderCallbacks);
        stringLoader = new StringLoader(getContext());
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

    private LoaderManager.LoaderCallbacks<List<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return stringLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            loaderAdapter.swapData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            ArrayList<String> list = new ArrayList<>();
            list.add("working");
            loaderAdapter.swapData(list);
        }
    };

    @Override
    public LoaderAdapter getLoaderAdapter() {
        return this.loaderAdapter;
    }
}
