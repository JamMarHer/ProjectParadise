package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends HolderFragment implements EnhancedFragment {

    private LoaderAdapter loaderAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        loaderAdapter = new LoaderAdapter(view.getContext());
        ListView mainListView = (ListView)view.findViewById(R.id.list_comments);
        mainListView.setAdapter(loaderAdapter);
        getLoaderManager().initLoader(R.id.string_loader_id, null, loaderCallbacks);

        view.findViewById(R.id.forceload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StringLoader.ACTION);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
            }
        });
        return view;
    }

    private LoaderManager.LoaderCallbacks<List<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return new StringLoader(getContext());
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
