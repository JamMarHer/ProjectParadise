package paradise.ccclxix.projectparadise.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.Fragments.WaveFragment;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.OkHttp3Helpers;
import paradise.ccclxix.projectparadise.utils.SnackBar;

public class WaveCardPinnedAdapter extends RecyclerView.Adapter<WaveCardViewHolder>{

    private LayoutInflater inflater;
    private HashMap<String, Integer> record = new HashMap<>();
    private RecyclerView mPinnedWavesRecyclerV;

    private SnackBar snackBar = new SnackBar();
    List<HashMap<String, String >> wavePinned;
    private FirebaseBuilder firebase;
    private AppManager appManager;
    private Picasso picasso;
    private static final String TAG = "WaveCardPinnedAdapter";

    public WaveCardPinnedAdapter(Context context, RecyclerView view, AppManager appManager){
        this.firebase = new FirebaseBuilder();
        this.wavePinned = new ArrayList<>();
        this.mPinnedWavesRecyclerV = view;
        this.appManager = appManager;
        this.picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(
                OkHttp3Helpers.getOkHttpClient(this.TAG, context))).build();

        mPinnedWavesRecyclerV.setAdapter(this);
        mPinnedWavesRecyclerV.setItemViewCacheSize(20);
        mPinnedWavesRecyclerV.setDrawingCacheEnabled(true);
        mPinnedWavesRecyclerV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mPinnedWavesRecyclerV.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));

        updatePinnedWaves(context);


    }

    private void updatePinnedWaves(final Context context){
        wavePinned = new ArrayList<>();
        final DatabaseReference databaseReference = firebase.get_user_authId("waves", "pinned");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wavePinned.clear();
                record.clear();
                mPinnedWavesRecyclerV.removeAllViews();
                for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                    final String waveID = wave.getKey();
                    DatabaseReference waveDBReference = firebase.get("events_us", waveID);
                    waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mPinnedWavesRecyclerV.removeAllViews();
                            updateAdapter(dataSnapshot, waveID, context);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MY_WAVES", databaseError.getMessage());

            }
        });
    }

    private List<HashMap<String, String >> updateAdapter(DataSnapshot dataSnapshot, String waveID, Context context){
        int waveAttending = (int)dataSnapshot.child("attending").getChildrenCount();
        HashMap<String, String> eventInfo = new HashMap<>();

        eventInfo.put("waveID", waveID);
        eventInfo.put("waveName", dataSnapshot.child("name_event").getValue().toString());
        // TODO add function (algo) for trending.

        // Checks if wave has a logo
        if (dataSnapshot.hasChild("image_url")){
            eventInfo.put("waveImageURL", dataSnapshot.child("image_url").getValue().toString());
        }else {
            eventInfo.put("waveImageURL", null);
        }

        // Hydrating event
        eventInfo.put("waveTrend", "trending");
        eventInfo.put("wavePosts", String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount()));
        eventInfo.put("waveAttending", String.valueOf(waveAttending));

        // Event is not in the record
        if(!record.containsKey(waveID)) {
            wavePinned.add(eventInfo);
            record.put(waveID, wavePinned.size()-1);
            if(waveID.equals(appManager.getWaveM().getEventID())){
                int toExchange = wavePinned.size()-1;
                Collections.swap(wavePinned,0, toExchange);
                record.put(waveID, 0);
                record.put(wavePinned.get(toExchange).get("waveID"), wavePinned.size()-1);
            }
        }
        notifyDataSetChanged();
        try{
            inflater = LayoutInflater.from(context);
        }catch (Exception e){

        }
        return wavePinned;
    }

    @Override
    public WaveCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.wave_single_card_pinned, parent, false);
        WaveCardViewHolder holder = new WaveCardViewHolder(view);
        return holder;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final WaveCardViewHolder holder, final int position) {
        final String waveID = wavePinned.get(position).get("waveID");
        final String waveName = wavePinned.get(position).get("waveName");
        final String waveImageURL = wavePinned.get(position).get("waveImageURL");
        final String waveAttending = wavePinned.get(position).get("waveAttending");
        final String waveTrend = wavePinned.get(position).get("waveTrend");
        final long currentWavePostsNum  = Long.valueOf(wavePinned.get(position).get("wavePosts"));
        holder.waveName.setText(waveName);
        if (waveImageURL != null){
            picasso.load(waveImageURL)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_import_export).into(holder.waveThumbnail);
        }

        if (position != 0) {
            holder.waveActiveIndicator.setVisibility(View.INVISIBLE);
        }


        holder.generalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (waveID.equals(appManager.getWaveM().getEventID())){
                    snackBar.showEmojiBar(view, "You are already riding this wave", Icons.POOP);
                }else {
                    appManager.getWaveM().updateEventID(waveID);
                    appManager.getWaveM().updateEventName(waveName);

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("source", "joined_event");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                    ((Activity)view.getContext()).finish();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return wavePinned.size();
    }

}

