package paradise.ccclxix.projectparadise.Fragments.WavesRelated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class MyWaves  extends Fragment {

    WavesAdapter wavesAdapter;
    RecyclerView listWaves;
    EventManager eventManager;
    FirebaseAuth mAuth;

    Button createWave;
    Button joinWave;

    private ViewGroup container;
    CredentialsManager credentialsManager;
    AppModeManager appModeManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_my_waves, null);
        eventManager = new EventManager(getContext());
        credentialsManager = new CredentialsManager(getContext());
        appModeManager = new AppModeManager(getContext());
        mAuth = FirebaseAuth.getInstance();
        this.container = container;
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
        ImageView waveSettings;
        ImageView waveJoin;
        View mView;

        public WaveViewHolder(View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_name_single_layout);
            waveThumbnail = itemView.findViewById(R.id.profile_wave_single_layout);
            waveAttending = itemView.findViewById(R.id.wave_attending_single_layout);
            waveTrending = itemView.findViewById(R.id.wave_trending_single_layout);
            waveSettings = itemView.findViewById(R.id.wave_settings);
            waveJoin = itemView.findViewById(R.id.wave_join);


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
                    final HashMap<String, Integer> record = new HashMap<>();

                    for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                        final String waveID = wave.getKey();
                        DatabaseReference waveDBReference = firebaseDatabase.getReference().child("events_us").child(waveID);
                        waveDBReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int waveAttending = (int)dataSnapshot.child("attending").getChildrenCount();
                                HashMap<String, String> eventInfo = new HashMap<>();
                                eventInfo.put("waveID", waveID);
                                eventInfo.put("waveName", dataSnapshot.child("name_event").getValue().toString());
                                // TODO add function (algo) for trending.
                                eventInfo.put("waveTrend", "trending");
                                eventInfo.put("waveAttending", String.valueOf(waveAttending));

                                if(!record.containsKey(waveID)) {
                                    waveList.add(eventInfo);
                                    record.put(waveID, waveList.size()-1);
                                    if(waveID.equals(eventManager.getEventID())){
                                        int toExchange = waveList.size()-1;
                                        Collections.swap(waveList,0, toExchange);
                                        record.put(waveID, 0);
                                        record.put(waveList.get(toExchange).get("waveID"), waveList.size()-1);
                                    }
                                }else {
                                    waveList.set(record.get(waveID), eventInfo);
                                }
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(WaveViewHolder holder, int position) {
            final String waveID = waveList.get(position).get("waveID");
            final String waveName = waveList.get(position).get("waveName");
            final String waveAttending = "Waving: "+waveList.get(position).get("waveAttending");
            final String waveTrend = waveList.get(position).get("waveTrend");
            holder.waveName.setText(waveName);
            holder.waveAttending.setText(waveAttending);
            if (waveTrend.equals("trending")){
                holder.waveTrending.setImageResource(R.drawable.ic_trending_up_white_24dp);
            }else {
                holder.waveTrending.setImageResource(R.drawable.ic_trending_flat_white_24dp);
            }

            if (waveID.equals(eventManager.getEventID())){
                holder.waveJoin.setImageResource(R.drawable.baseline_radio_button_checked_white_36);
            }
            holder.waveJoin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (waveID.equals(eventManager.getEventID())){
                        TSnackbar snackbar = TSnackbar.make(container, "You are already riding this wave.", TSnackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.setIconLeft(R.drawable.poop_icon, 24);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.show();
                    }else {
                        eventManager.updateEventID(waveID);
                        eventManager.updateEventName(waveName);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("source", "joined_event");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    return true;
                }
            });

            View waveSettignsPopupView = inflater.inflate(R.layout.share_wave_popup, null);
            ImageView qrCode = waveSettignsPopupView.findViewById(R.id.qrCode);
            TextView eventname = waveSettignsPopupView.findViewById(R.id.waveName);
            qrCode.setImageBitmap(getEventQR(waveID));
            eventname.setText(waveName);


            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow waveSettingsPopupWindow = new PopupWindow(waveSettignsPopupView, width,height);
            waveSettingsPopupWindow.setAnimationStyle(R.style.AnimationPopUpWindow);

            holder.waveSettings.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        waveSettingsPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
                    return true;
                }
            });

            final Button closeWaveSettings = waveSettignsPopupView.findViewById(R.id.close_share);
            final Button leaveWave = waveSettignsPopupView.findViewById(R.id.leave_wave);



            closeWaveSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waveSettingsPopupWindow.dismiss();
                }
            });

            leaveWave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    leaveWave(waveID);
                }
            });



        }


        @Override
        public int getItemCount() {
            return waveList.size();
        }
    }


    private Bitmap getEventQR(String eventID){
        return QRCode.from(eventID).bitmap();
    }

    private void leaveWave(final String waveID){
        if (mAuth.getCurrentUser() != null) {
            credentialsManager.updateCredentials();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(waveID)
                    .child("attending")
                    .child(mAuth.getUid());
            // Gets the time the user logged into the wave.
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long inTime = Long.valueOf(dataSnapshot.child("in").getValue().toString());
                    final DatabaseReference userDatabaseReference = firebaseDatabase.getReference()
                            .child("users")
                            .child(mAuth.getUid())
                            .child("waves")
                            .child("in")
                            .child(waveID);
                    // Removes the wave from personal waves
                    userDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference dbFUsers = firebaseDatabase.getReference()
                                        .child("users")
                                        .child(mAuth.getUid())
                                        .child("waves")
                                        .child("out")
                                        .child(waveID);
                                final HashMap<String, Long> inoutInfo = new HashMap<>();
                                inoutInfo.put("in", inTime);
                                inoutInfo.put("out", System.currentTimeMillis());
                                // Updates the attended record in user table.
                                dbFUsers.setValue(inoutInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference dbWaves = firebaseDatabase.getReference()
                                                    .child("events_us")
                                                    .child(waveID)
                                                    .child("attended")
                                                    .child(mAuth.getUid());
                                            // Updates the wave table of attended.
                                            dbWaves.setValue(inoutInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Removes the user from the wave table attending.
                                                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    appModeManager.setModeToExplore();
                                                                    eventManager.updateEventID(null);
                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                    intent.putExtra("source", "logged_in");
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    getActivity().finish();
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        Log.d("LEAVING_WAVE", task.getException().getMessage());
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("LEAVING_WAVE", task.getException().getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.d("LEAVING_WAVE", task.getException().getMessage());
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    TSnackbar snackbar = TSnackbar.make(container, "Something went wrong.", TSnackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.setIconLeft(R.drawable.fire_emoji, 24);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                    TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        }

    }
}
