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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.ModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;

public class ExploreFragment extends HolderFragment implements EnhancedFragment {

    WavesAdapter wavesAdapter;
    RecyclerView listWaves;
    private ViewGroup container;
    private FirebaseBuilder firebase = new FirebaseBuilder();
    SnackBar snackbar;
    private Button joinWave;
    private Button createWave;

    AppManager appManager;

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
        View inflater1 = inflater.inflate(R.layout.fragment_explore_waves, null);


        listWaves = inflater1.findViewById(R.id.myWaves);
        this.container = container;

        ItemTouchHelper.Callback itemTouchHelperCB = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(waveList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                wavesAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                if (toPos == 0){
                    String waveID = waveList.get(toPos).get("waveID");
                    appManager.getWaveM().updateEventID(waveID);
                    appManager.getWaveM().updateEventName(waveList.get(toPos).get("waveName"));
                    appManager.getWaveM().updateWavePosts(waveID, Long.valueOf(waveList.get(toPos).get("wavePosts")));


                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("source", "joined_event");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCB);


        itemTouchHelper.attachToRecyclerView(listWaves);

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
                startActivity(intent);
            }
        });


        return inflater1;

    }


    private class WaveViewHolder extends RecyclerView.ViewHolder{

        TextView waveName;
        TextView waveAttending;
        ImageView waveThumbnail;
        ImageView waveJoin;
        ImageView waveDrag;
        ImageView waveNotification;
        View mView;

        public WaveViewHolder(View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_name_single_layout);
            waveThumbnail = itemView.findViewById(R.id.profile_wave_single_layout);
            waveAttending = itemView.findViewById(R.id.wave_attending_single_layout);
            waveNotification = itemView.findViewById(R.id.wave_notification);
            waveDrag = itemView.findViewById(R.id.wave_drag);
            waveJoin = itemView.findViewById(R.id.wave_join);


            mView = itemView;
        }
    }

    private class WavesAdapter extends RecyclerView.Adapter<WaveViewHolder>{

        private LayoutInflater inflater;


        private HashMap<String, Integer> record;
        public WavesAdapter(final Context context){
            waveList = new ArrayList<>();
            final DatabaseReference databaseReference = firebase.get_user_authId("waves", "in");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waveList.clear();
                    record = new HashMap<>();

                    for (final  DataSnapshot wave: dataSnapshot.getChildren()){
                        final String waveID = wave.getKey();
                        DatabaseReference waveDBReference = firebase.getEvents(waveID);
                        waveDBReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                int waveAttending = (int)dataSnapshot.child("attending").getChildrenCount();
                                HashMap<String, String> eventInfo = new HashMap<>();
                                eventInfo.put("waveID", waveID);
                                eventInfo.put("waveName", dataSnapshot.child("name_event").getValue().toString());
                                // TODO add function (algo) for trending.
                                eventInfo.put("waveTrend", "trending");
                                eventInfo.put("wavePosts", String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount()));
                                eventInfo.put("waveAttending", String.valueOf(waveAttending));

                                if(!record.containsKey(waveID)) {
                                    waveList.add(eventInfo);
                                    record.put(waveID, waveList.size()-1);
                                    if(waveID.equals(appManager.getWaveM().getEventID())){
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
        public void onBindViewHolder(final WaveViewHolder holder, final int position) {
            final String waveID = waveList.get(position).get("waveID");
            final String waveName = waveList.get(position).get("waveName");
            final String waveAttending = waveList.get(position).get("waveAttending");
            final String waveTrend = waveList.get(position).get("waveTrend");
            final long currentWavePostsNum  = Long.valueOf(waveList.get(position).get("wavePosts"));
            holder.waveName.setText(waveName);
            holder.waveAttending.setText(waveAttending);

            if (appManager.getWaveM().getWavePosts(waveID) != -1){
                if (appManager.getWaveM().getWavePosts(waveID) != currentWavePostsNum){
                    holder.waveNotification.setVisibility(View.VISIBLE);
                }else {
                    holder.waveNotification.setVisibility(View.INVISIBLE);
                }
            }else {
                appManager.getWaveM().updateWavePosts(waveID, currentWavePostsNum);
            }


            if (waveID.equals(appManager.getWaveM().getEventID())){
                holder.waveJoin.setImageResource(R.drawable.baseline_radio_button_checked_white_36);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.waveJoin.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_holder_main_colors) );
                } else {
                    holder.waveJoin.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_holder_main_colors));
                }
                holder.waveDrag.setVisibility(View.INVISIBLE);
            }else {
                holder.waveJoin.setVisibility(View.INVISIBLE);
            }
            holder.waveJoin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (waveID.equals(appManager.getWaveM().getEventID())){
                        snackbar.showEmojiBar(getView(), "You are already riding this wave", Icons.POOP);
                    }else {
                        appManager.getWaveM().updateEventID(waveID);
                        appManager.getWaveM().updateEventName(waveName);

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

            holder.waveName.setOnTouchListener(new View.OnTouchListener() {
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
                    record.remove(waveID);
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
                                                                if (task.isSuccessful()) {
                                                                    appManager.getModeM().setModeToExplore();
                                                                    appManager.getWaveM().updateEventID(null);
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
                    snackbar.showEmojiBar("Something wen wrong.", Icons.FIRE);
                }
            });
        }

    }



    @Override
    public String getName() {
        return null;
    }



}
