package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class PinnedWavesActivity extends AppCompatActivity {

    private FirebaseBuilder firebase = new FirebaseBuilder();

    private PinnedOptionAdapter pinnedOptionAdapter;
    private List<HashMap<String, String>> waves;
    private AppManager appManager;

    private TextView pinnedTitle;
    private RecyclerView pinnedWavesView;

    final int sdk = android.os.Build.VERSION.SDK_INT;


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinned_waves_edit);
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());



        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        settings.setVisibility(View.INVISIBLE);

        pinnedTitle = findViewById(R.id.pinned_title);
        pinnedWavesView = findViewById(R.id.pinned_wave_recycleView);

        pinnedOptionAdapter = new PinnedOptionAdapter(getApplicationContext());
        pinnedOptionAdapter.populate();
        pinnedWavesView.setAdapter(pinnedOptionAdapter);
        pinnedWavesView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class PinnedOptionHolder extends RecyclerView.ViewHolder{

        TextView pinnedName;
        ToggleButton pinnedSwitch;
        View mView;
        String waveID;

        public PinnedOptionHolder(View itemView){
            super(itemView);
            pinnedName = itemView.findViewById(R.id.pinned_name);
            pinnedSwitch = itemView.findViewById(R.id.pinned_switch);
            mView = itemView;

            pinnedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    firebase.get_user_authId("waves", "pinned").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(waveID) && !isChecked){
                                firebase.get_user_authId("waves", "pinned", waveID).removeValue();
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(pinnedSwitch.getContext(), R.drawable.circle_holder_gray) );
                                } else {
                                    pinnedSwitch.setBackground(ContextCompat.getDrawable(pinnedSwitch.getContext(), R.drawable.circle_holder_gray));
                                }
                            }
                            else if (!dataSnapshot.hasChild(waveID) && isChecked){
                                firebase.get_user_authId("waves", "pinned", waveID)
                                        .setValue(ServerValue.TIMESTAMP);
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(pinnedSwitch.getContext(), R.drawable.circle_holder_main_color) );
                                } else {
                                    pinnedSwitch.setBackground(ContextCompat.getDrawable(pinnedSwitch.getContext(), R.drawable.circle_holder_main_color));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    private class PinnedOptionAdapter extends RecyclerView.Adapter<PinnedOptionHolder> {
        LayoutInflater inflater;
        Context context1;
        private HashMap<String, Integer> record;
        private HashMap<String, String> allHolders = new HashMap<>();

        public PinnedOptionAdapter(final Context context){
            waves = new ArrayList<>();
            this.context1 = context;
        }

        public void populate(){
            final DatabaseReference databaseReference = firebase.get_user_authId("waves", "in");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waves.clear();
                    record = new HashMap<>();
                    for (final DataSnapshot wave : dataSnapshot.getChildren()) {
                        final String waveID = wave.getKey();
                        DatabaseReference waveDBReference = firebase.getEvents(waveID);
                        waveDBReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot masterDataSnapshot) {
                                    firebase.get_user_authId("waves", "pinned").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            HashMap<String, String> eventInfo = new HashMap<>();
                                            if(dataSnapshot.hasChild(waveID)){
                                                eventInfo.put("pinned", "true");
                                            }
                                            else{
                                                eventInfo.put("pinned", "false");
                                            }
                                            eventInfo.put("pinnedName", masterDataSnapshot.child("name_event").getValue().toString());
                                            eventInfo.put("waveID", waveID);
                                            if(!record.containsKey(waveID)) {
                                                waves.add(eventInfo);
                                                record.put(waveID, waves.size()-1);
                                                if(waveID.equals(appManager.getWaveM().getEventID())){
                                                    int toExchange = waves.size()-1;
                                                    Collections.swap(waves,0, toExchange);
                                                    record.put(waveID, 0);
                                                    record.put(waves.get(toExchange).get("waveID"), waves.size()-1);
                                                }
                                            }else {
                                                waves.set(record.get(waveID), eventInfo);
                                            }
                                            pinnedOptionAdapter.notifyDataSetChanged();
                                            inflater = LayoutInflater.from(context1);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("PIN", "OH WELL");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("PIN", "OH WELL");
                }
            });
        }

        @Override
        public PinnedOptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.pinned_wave_option, parent, false);
            PinnedOptionHolder holder = new PinnedOptionHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(final PinnedOptionHolder holder, int position) {
            final String waveName = waves.get(position).get("pinnedName");
            holder.waveID = waves.get(position).get("waveID");
            holder.pinnedName.setText(waveName);
            // Does any presetting
            if(waves.get(position).get("pinned").equals("true")){
                holder.pinnedSwitch.setChecked(true);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_main_color) );
                } else {
                    holder.pinnedSwitch.setBackground(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_main_color));
                }
            }
            else{
                holder.pinnedSwitch.setChecked(false);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_gray) );
                } else {
                    holder.pinnedSwitch.setBackground(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_gray));
                }
            }


        }

        @Override
        public int getItemCount() {
            return waves.size();
        }
    }
}
