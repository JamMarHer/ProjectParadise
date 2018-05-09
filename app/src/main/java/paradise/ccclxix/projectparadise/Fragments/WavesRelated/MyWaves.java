package paradise.ccclxix.projectparadise.Fragments.WavesRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class MyWaves  extends Fragment {

    WavesAdapter wavesAdapter;
    RecyclerView listWaves;
    EventManager eventManager;
    FirebaseAuth mAuth;

    Button createWave;
    Button joinWave;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_my_waves, null);
        eventManager = new EventManager(getContext());
        mAuth = FirebaseAuth.getInstance();
        // TODO check for user logged in.
        listWaves = inflater1.findViewById(R.id.myWaves);
        wavesAdapter = new WavesAdapter(getContext());
        listWaves.setAdapter(wavesAdapter);
        listWaves.setLayoutManager(new LinearLayoutManager(getContext()));

        createWave = inflater1.findViewById(R.id.createWave);
        joinWave = inflater1.findViewById(R.id.joinWave);

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
                getActivity().startActivity(intent);
            }
        });
        return inflater1;

    }


    private class WaveViewHolder extends RecyclerView.ViewHolder{

        TextView waveName;
        TextView waveAttending;
        ImageView waveThumbnail;
        ImageView waveTrending;
        View mView;

        public WaveViewHolder(View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_name_single_layout);
            waveThumbnail = itemView.findViewById(R.id.profile_wave_single_layout);
            waveAttending = itemView.findViewById(R.id.wave_attending_single_layout);
            waveTrending = itemView.findViewById(R.id.wave_trending_single_layout);

            mView = itemView;
        }
    }

    private class WavesAdapter extends RecyclerView.Adapter<WaveViewHolder>{

        private LayoutInflater inflater;

        private List<HashMap<String, String>> waveList;

        public WavesAdapter(final Context context){
            waveList = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("waves").child("in");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waveList.clear();
                    for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                        final String waveID = wave.getKey();
                        DatabaseReference waveDBReference = firebaseDatabase.getReference().child("events_us").child(waveID);
                        waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int waveAttending = (int)dataSnapshot.child("attending").getChildrenCount();
                                HashMap<String, String> eventInfo = new HashMap<>();
                                eventInfo.put("waveID", waveID);
                                eventInfo.put("waveName", dataSnapshot.child("name_event").getValue().toString());
                                // TODO add function (algo) for trending.
                                eventInfo.put("waveTrend", "trending");
                                eventInfo.put("waveAttending", String.valueOf(waveAttending));
                                waveList.add(eventInfo);

                                wavesAdapter.notifyDataSetChanged();
                                inflater = LayoutInflater.from(context);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("MY_WAVES", databaseError.getMessage());
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



        @Override
        public WaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_layout, parent, false);
            WaveViewHolder holder = new WaveViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(WaveViewHolder holder, int position) {
            final String waveID = waveList.get(position).get("waveID");
            final String waveName = waveList.get(position).get("waveName");
            final String waveAttending = waveList.get(position).get("waveAttending");
            final String waveTrend = waveList.get(position).get("waveTrend");
            holder.waveName.setText(waveName);
            holder.waveAttending.setText(waveAttending);
            if (waveTrend.equals("trending")){
                holder.waveTrending.setImageResource(R.drawable.ic_trending_up_white_24dp);
            }else {
                holder.waveTrending.setImageResource(R.drawable.ic_trending_flat_white_24dp);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    eventManager.updateEventID(waveID);
                    eventManager.updateEventName(waveName);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("source", "joined_event");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }


        @Override
        public int getItemCount() {
            return waveList.size();
        }
    }

}
