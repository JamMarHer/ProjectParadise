package paradise.ccclxix.projectparadise.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.ExploreRelated.WaveOverviewActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;

public class ExploreFragment extends HolderFragment implements EnhancedFragment {

    RecyclerView listWaves;
    private ViewGroup container;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    SnackBar snackbar = new SnackBar();

    Random random = new Random();
    AppManager appManager;
    EditText searchText;
    RecyclerView results;


    ArrayList<String> id;
    ArrayList<String> name;
    ArrayList<String> thumbnail;
    ArrayList<String> type;
    ArrayList<String> numPosts;
    ArrayList<String> numMembers;
    ArrayList<String> wScore;

    int MAX_SEARCH = 15;
    Picasso picasso;
    SearchAdapter searchAdapter;
    SuggestionsAdapter suggestionsAdapter;
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


        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();

        // TODO maps?
        id = new ArrayList<>();
        name = new ArrayList<>();
        numMembers = new ArrayList<>();
        numPosts = new ArrayList<>();
        wScore = new ArrayList<>();
        thumbnail = new ArrayList<>();
        type = new ArrayList<>();
        setSuggestionsAdapter();
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
                    setSearchAdapter(editable.toString());
                }else {
                    name.clear();
                    thumbnail.clear();
                    id.clear();
                    type.clear();
                    results.removeAllViews();
                    setSuggestionsAdapter();

                }
            }
        });

        return inflater1;
    }


    private void setSuggestionsAdapter(){
        UINotificationHelpers.showProgress(true,results, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));
        firebase.getEvents().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.clear();
                thumbnail.clear();
                id.clear();
                type.clear();
                numMembers.clear();
                numPosts.clear();
                wScore.clear();
                results.removeAllViews();
                if (dataSnapshot.hasChildren()){
                    int currentCount = 0;
                    long numChildren = dataSnapshot.getChildrenCount();
                    for (DataSnapshot waves : dataSnapshot.getChildren()){

                        String currentId = waves.getKey();
                        if (waves.child("privacy").getValue().toString().equals("false")){
                            String currentUsername = waves.child("name_event").getValue().toString();
                            String currentThumbnail = "";
                            if (waves.hasChild("thumbnail")){
                                currentThumbnail = waves.child("thumbnail").getValue().toString();
                            }
                            if (getRandomTrue(currentCount, numChildren)){
                                id.add(currentId);
                                name.add(currentUsername);
                                thumbnail.add(currentThumbnail);
                                if (waves.child("wall").hasChild("posts"))
                                    numPosts.add(String.valueOf(waves.child("wall").child("posts").getChildrenCount()));
                                else
                                    numPosts.add("0");
                                if (waves.hasChild("attending"))
                                    numMembers.add(String.valueOf(waves.child("attending").getChildrenCount()));
                                else
                                    numMembers.add("0");
                                if (waves.hasChild("wave_score"))
                                    wScore.add(waves.child("wave_score").getValue().toString());
                                else
                                    wScore.add("?");

                                type.add("WAVE");
                                currentCount++;
                            }
                            if (currentCount == MAX_SEARCH)
                                break;
                        }
                    }
                    UINotificationHelpers.showProgress(false,results, progressBar, getResources().getInteger(android.R.integer.config_shortAnimTime));

                }

                suggestionsAdapter = new SuggestionsAdapter(getContext());
                results.setAdapter(suggestionsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // This is a bit interesting if you think of it. The method does provide randomness but it also
    // treats with some degree of relevancy the newer waves.
    private boolean getRandomTrue(int currentCount, long dataSnapshotCCount){
        if (MAX_SEARCH - currentCount <= (int) dataSnapshotCCount){
            return random.nextBoolean();
        }else {
            return true;
        }
    }

    private void setSearchAdapter(final String s){


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
                                if (waves.child("wall").hasChild("posts"))
                                    numPosts.add(String.valueOf(waves.child("wall").child("posts").getChildrenCount()));
                                else
                                    numPosts.add("0");
                                if (waves.hasChild("attending"))
                                    numMembers.add(String.valueOf(waves.child("attending").getChildrenCount()));
                                else
                                    numMembers.add("0");
                                if (waves.hasChild("wave_score"))
                                    wScore.add(waves.child("wave_score").getValue().toString());
                                else
                                    wScore.add("?");
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

    public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionViewHolder>{

        Context context;
        public SuggestionsAdapter(Context context){
            this.context = context;
        }



        @NonNull
        @Override
        public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.wave_card_single, parent, false);
            return new SuggestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestionViewHolder holder, final int position) {
            holder.waveName.setText(name.get(position));

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
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
            holder.wScore.setText(wScore.get(position));
            holder.numMembers.setText(numMembers.get(position));
            holder.numPosts.setText(numPosts.get(position));
        }

        @Override
        public int getItemCount() {
            return name.size();
        }
    }

    public class SearchAdapter extends RecyclerView.Adapter<SuggestionViewHolder>{
        Context context;

        public SearchAdapter(Context context){
            this.context = context;
        }

        @Override
        public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.wave_card_single, parent, false);
            return new SuggestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SuggestionViewHolder holder, final int position) {
            holder.waveName.setText(name.get(position));
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.mainLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.gradient_2) );
            } else {
                holder.mainLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_2));
            }

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
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
            holder.wScore.setText(wScore.get(position));
            holder.numMembers.setText(numMembers.get(position));
            holder.numPosts.setText(numPosts.get(position));
        }

        @Override
        public int getItemCount() {
            return name.size();
        }
    }



    public class SuggestionViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout mainLayout;
        TextView waveName;
        TextView numPosts;
        TextView numMembers;
        TextView wScore;

        public SuggestionViewHolder(View itemView){
            super(itemView);
            mainLayout = itemView.findViewById(R.id.wave_card_layout);
            waveName = itemView.findViewById(R.id.wave_card_name);
            numPosts = itemView.findViewById(R.id.wave_card_posts);
            numMembers = itemView.findViewById(R.id.wave_card_members);
            wScore = itemView.findViewById(R.id.wave_card_wScore);
        }
    }



    @Override
    public String getName() {
        return null;
    }



}
