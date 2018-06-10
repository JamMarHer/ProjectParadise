package paradise.ccclxix.projectparadise.Fragments.ExploreRelated;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;

public class DiscoverFragment extends Fragment {

    EditText searchText;
    RecyclerView results;

    FirebaseBuilder firebase;

    ArrayList<String> id;
    ArrayList<String> name;
    ArrayList<String> thumbnail;
    ArrayList<String> type;

    int MAX_SEARCH = 15;
    Picasso picasso;
    SearchAdapter searchAdapter;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_discover, null);
        firebase = new FirebaseBuilder();
        searchText = inflater1.findViewById(R.id.search_edit);
        results = inflater1.findViewById(R.id.results_recycler_view);
        progressBar = inflater1.findViewById(R.id.search_progress);
        results.setHasFixedSize(true);
        results.setLayoutManager(new LinearLayoutManager(getContext()));
        results.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();

        id = new ArrayList<>();
        name = new ArrayList<>();
        thumbnail = new ArrayList<>();
        type = new ArrayList<>();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()){
                    setAdapter(editable.toString());
                }else {
                    name.clear();
                    thumbnail.clear();
                    id.clear();
                    type.clear();
                    results.removeAllViews();

                }
            }
        });

        return inflater1;
    }


    private void setAdapter(final String s){


        UINotificationHelpers.showProgress(true,results, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));
        firebase.getEvents().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.clear();
                thumbnail.clear();
                id.clear();
                type.clear();
                results.removeAllViews();
                if (dataSnapshot.hasChildren()){
                    int currentCount = 0;
                    for (DataSnapshot waves : dataSnapshot.getChildren()){

                        String currentId = waves.getKey();
                        if (waves.child("privacy").getValue().toString().equals("false")){
                            String currentUsername = waves.child("name_event").getValue().toString();
                            String currentThumbnail = "";
                            if (waves.hasChild("thumbnail")){
                                currentThumbnail = waves.child("thumbnail").getValue().toString();
                            }
                            if (currentUsername.toLowerCase().contains(s.toLowerCase())){
                                id.add(currentId);
                                name.add(currentUsername);
                                thumbnail.add(currentThumbnail);
                                type.add("WAVE");
                                currentCount++;
                            }
                            if (currentCount == MAX_SEARCH)
                            break;
                        }
                    }
                    UINotificationHelpers.showProgress(false,results, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                }

                searchAdapter = new SearchAdapter(getContext());
                results.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>{
        Context context;

        public SearchAdapter(Context context){
            this.context = context;
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_wave_user_view, parent, false);
            return new SearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchViewHolder holder, int position) {
            holder.name.setText(name.get(position));
            if (!thumbnail.get(position).isEmpty()){
                picasso.load(thumbnail.get(position))
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(holder.thumbnail);
            }
        }

        @Override
        public int getItemCount() {
            return name.size();
        }
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView name;


        public SearchViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.card_name);
            thumbnail = itemView.findViewById(R.id.card_thumbnail);

        }
    }
}
