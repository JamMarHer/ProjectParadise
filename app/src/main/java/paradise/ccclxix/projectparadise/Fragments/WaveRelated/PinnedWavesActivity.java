package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.QRGenerator;
import paradise.ccclxix.projectparadise.utils.SnackBar;

public class PinnedWavesActivity extends AppCompatActivity {

    private FirebaseBuilder firebase = new FirebaseBuilder();

    public LinearLayout generalLinearLayout;

    private PinnedOptionAdapter pinnedOptionAdapter;

    private List<HashMap<String, String>> waves;
    private HashMap<String, Integer> record;
    private AppManager appManager;

    private TextView pinnedTitle;
    private RecyclerView pinnedWavesView;
    private SnackBar snackBar = new SnackBar();

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
        generalLinearLayout = findViewById(R.id.pinned_waves_linear_layout);

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
        ImageView infoButton;
        ToggleButton pinnedSwitch;
        View mView;
        String waveID;

        public PinnedOptionHolder(View itemView){
            super(itemView);
            pinnedName = itemView.findViewById(R.id.pinned_name);
            pinnedSwitch = itemView.findViewById(R.id.pinned_switch);
            infoButton = itemView.findViewById(R.id.wave_info_pinned);
            mView = itemView;



        }
    }

    private class PinnedOptionAdapter extends RecyclerView.Adapter<PinnedOptionHolder> {
        LayoutInflater inflater;
        Context context1;

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
                        waveDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
        public void onBindViewHolder(final PinnedOptionHolder holder, final int position) {
            final String waveName = waves.get(position).get("pinnedName");
            final String waveID = waves.get(position).get("waveID");
            holder.waveID = waveID;
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

            View waveSettignsPopupView = inflater.inflate(R.layout.share_wave_popup, null);
            ImageView qrCode = waveSettignsPopupView.findViewById(R.id.qrCode);
            TextView eventname = waveSettignsPopupView.findViewById(R.id.waveName);
            qrCode.setImageBitmap(QRGenerator.getEventQR(waveID));
            eventname.setText(waveName);


            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            final PopupWindow waveSettingsPopupWindow = new PopupWindow(waveSettignsPopupView, width,height);
            waveSettingsPopupWindow.setAnimationStyle(R.style.AnimationPopUpWindow);



            final TextView closeWaveSettings = waveSettignsPopupView.findViewById(R.id.close_share);
            final TextView leaveWave = waveSettignsPopupView.findViewById(R.id.leave_wave);
            final TextView enterWave = waveSettignsPopupView.findViewById(R.id.enter_wave);


            enterWave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (waveID.equals(appManager.getWaveM().getEventID())){
                        snackBar.showEmojiBar(generalLinearLayout, "You are already riding this wave", Icons.POOP);
                    }else {
                        // TODO add listener to update the wave fragment to display the correct information.
                        appManager.getWaveM().updateEventID(waveID);
                        appManager.getWaveM().updateEventName(waveName);

                        Intent intent = new Intent(PinnedWavesActivity.this, MainActivity.class);
                        intent.putExtra("source", "joined_event");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        PinnedWavesActivity.this.finish();
                    }
                }
            });

            closeWaveSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waveSettingsPopupWindow.dismiss();
                }
            });

            leaveWave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waves.remove(position);
                    pinnedOptionAdapter.notifyItemRemoved(position);
                    pinnedOptionAdapter.notifyItemRangeChanged(position, waves.size());

                    leaveWave(waveID, position);
                    waveSettingsPopupWindow.dismiss();
                }
            });

            holder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waveSettingsPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                }
            });
            holder.pinnedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    firebase.get_user_authId("waves", "pinned").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(waveID) && !isChecked){
                                firebase.get_user_authId("waves", "pinned", waveID).removeValue();
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    holder.pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_gray) );
                                } else {
                                    holder.pinnedSwitch.setBackground(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_gray));
                                }
                            }
                            else if (!dataSnapshot.hasChild(waveID) && isChecked){
                                firebase.get_user_authId("waves", "pinned", waveID)
                                        .setValue(ServerValue.TIMESTAMP);
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    holder.pinnedSwitch.setBackgroundDrawable(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_main_color) );
                                } else {
                                    holder.pinnedSwitch.setBackground(ContextCompat.getDrawable(holder.pinnedSwitch.getContext(), R.drawable.circle_holder_main_color));
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

        @Override
        public int getItemCount() {
            return waves.size();
        }
    }

    private void leaveWave(final String waveID, final int position) {
        if (firebase.getCurrentUser() != null) {
            final DatabaseReference databaseReference = firebase.getEvents(waveID, "attending", firebase.auth_id());
            // Gets the time the user logged into the wave.
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final long inTime = Long.valueOf(dataSnapshot.child("in").getValue().toString());
                    final DatabaseReference userDatabaseReference = firebase.get_user_authId("waves", "in", waveID);
                    // Removes the wave from personal waves
                    userDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference dbFUsers = firebase.get_user_authId("waves", "out", waveID);
                                final HashMap<String, Long> inoutInfo = new HashMap<>();
                                inoutInfo.put("in", inTime);
                                inoutInfo.put("out", System.currentTimeMillis());
                                // Updates the attended record in user table.
                                dbFUsers.setValue(inoutInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference dbWaves = firebase.getEvents(waveID, "attended", firebase.auth_id());
                                            // Updates the wave table of attended.
                                            dbWaves.setValue(inoutInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Removes the user from the wave table attending.
                                                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    if (task.getException() != null)
                                                                        Log.d("LEAVING_WAVE_PINNED", task.getException().getMessage());
                                                                    else
                                                                        Log.d("LEAVING_WAVE_PINNED", "No exception caught.");

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
                            } else {
                                Log.d("LEAVING_WAVE", task.getException().getMessage());
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    snackBar.showEmojiBar(generalLinearLayout, "Something wen wrong.", Icons.FIRE);
                }
            });
        }
    }
}
