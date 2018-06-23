package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.AttendantsInEvent;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.EventChat;
import paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated.OnGoingChats;
import paradise.ccclxix.projectparadise.Fragments.ExploreRelated.DiscoverFragment;
import paradise.ccclxix.projectparadise.Fragments.ExploreRelated.MyWavesFragment;
import paradise.ccclxix.projectparadise.Fragments.ExploreRelated.WaveOverviewActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;

public class ExploreFragment extends HolderFragment implements EnhancedFragment {

    RecyclerView listWaves;
    private ViewGroup container;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    SnackBar snackbar = new SnackBar();

    AppManager appManager;
    EditText searchText;
    RecyclerView results;


    ArrayList<String> id;
    ArrayList<String> name;
    ArrayList<String> thumbnail;
    ArrayList<String> type;

    int MAX_SEARCH = 15;
    Picasso picasso;
    SearchAdapter searchAdapter;
    ProgressBar progressBar;


    private List<HashMap<String, String>> waveList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }else {
            appManager = new AppManager();
            appManager.initialize(getContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_discover, null);



        Button createWave = inflater1.findViewById(R.id.createWave);
        Button joinWave = inflater1.findViewById(R.id.joinWave);


        createWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                getActivity().startActivity(intent);
            }
        });

        joinWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QRScannerActivity.class);
                startActivity(intent);
            }
        });

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
        public void onBindViewHolder(SearchViewHolder holder, final int position) {
            holder.name.setText(name.get(position));
            if (!thumbnail.get(position).isEmpty()){
                picasso.load(thumbnail.get(position))
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(holder.thumbnail);
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =  new Intent(getActivity(), WaveOverviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", id.get(position));
                    bundle.putString("thumbnail", thumbnail.get(position));
                    bundle.putString("name", name.get(position));
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return name.size();
        }
    }


    public class SearchViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView name;
        ConstraintLayout view;

        public SearchViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.card_name);
            thumbnail = itemView.findViewById(R.id.card_thumbnail);
            view = itemView.findViewById(R.id.card_single_view);
        }
    }



    @Override
    public String getName() {
        return null;
    }



}
