package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.Attending.QRScannerActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.Fragments.PersonalRelated.EditProfileActivity;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Hosting.CreateEventActivity;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.Transformations;

public class PersonalFragment extends HolderFragment implements EnhancedFragment {


    EventManager eventManager;
    FirebaseAuth mAuth;

    private CredentialsManager credentialsManager;
    private TextView personalUsername;
    private ImageView settingsImageView;
    private ImageView profilePicture;
    private TextView myNumWaves;
    private TextView myNumContacts;
    private TextView mStatus;
    private RecyclerView mPinnedWavesRecyclerV;

    Button createWave;
    Button joinWave;
    View generalView;

    AppModeManager appModeManager;

    List<HashMap<String, String >> wavePinned;
    WaveCardPinnedAdapter pinnedWavesAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflater1 = inflater.inflate(R.layout.fragment_my_waves, null);

        wavePinned = new ArrayList<>();
        eventManager = new EventManager(getContext());
        credentialsManager = new CredentialsManager(getContext());
        appModeManager = new AppModeManager(getContext());
        mAuth = FirebaseAuth.getInstance();

        settingsImageView = inflater1.findViewById(R.id.edit_profile);

        myNumWaves = inflater1.findViewById(R.id.numberWaves);
        myNumContacts = inflater1.findViewById(R.id.numberContacts);
        personalUsername = inflater1.findViewById(R.id.personal_username);
        profilePicture = inflater1.findViewById(R.id.profile_picture_personal);
        mStatus = inflater1.findViewById(R.id.personal_status);
        mPinnedWavesRecyclerV = inflater1.findViewById(R.id.pinned_waves_recyclerView);
        // TODO check for user logged in.



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

        credentialsManager = new CredentialsManager(getContext());
        generalView = inflater1;
        setupNumWavesAndContacts();


        personalUsername.setText(credentialsManager.getUsername());


        View settingsPopupView = inflater.inflate(R.layout.settings_popup, null);
        if (eventManager.getEventID() == null){
            //TODO
        }


        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });



        // Views inside settings Popup window.
        final Button logoutButton = settingsPopupView.findViewById(R.id.logoutButton);



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), InitialAcitivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        pinnedWavesAdapter = new WaveCardPinnedAdapter(getContext());
        mPinnedWavesRecyclerV.setAdapter(pinnedWavesAdapter);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        mPinnedWavesRecyclerV.setLayoutManager(layoutManager);

        return inflater1;

    }


    // TODO This can be highly optimized.
    private void setupNumWavesAndContacts(){
        if(mAuth.getCurrentUser() != null && mAuth.getUid() != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference userDBReference = firebaseDatabase.getReference()
                    .child("users")
                    .child(mAuth.getUid())
                    .child("waves")
                    .child("in");
            final DatabaseReference contacesDBReference = firebaseDatabase.getReference()
                    .child("messages")
                    .child(mAuth.getUid());
            userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myNumWaves.setText(String.format("%s", String.valueOf(dataSnapshot.getChildrenCount())));
                    contacesDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myNumContacts.setText(String.format("%s", String.valueOf(dataSnapshot.getChildrenCount())));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase1.getReference()
                    .child("users")
                    .child(mAuth.getUid())
                    .child("profile_picture");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){

                        credentialsManager.updateProfilePic(dataSnapshot.getValue().toString());
                        Picasso.with(profilePicture.getContext()).load(dataSnapshot.getValue().toString())
                                .transform(Transformations.getScaleDownWithView(profilePicture))
                                .placeholder(R.drawable.idaelogo6_full).into(profilePicture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference = firebaseDatabase1.getReference()
                    .child("users")
                    .child(mAuth.getUid())
                    .child("status");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        mStatus.setText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    public String getName() {
        return null;
    }



    private class WaveCardViewHolder extends RecyclerView.ViewHolder{

        TextView waveName;
        ImageView waveThumbnail;
        View mView;

        public WaveCardViewHolder(View itemView) {
            super(itemView);
            waveName = itemView.findViewById(R.id.wave_single_card_name);
            waveThumbnail = itemView.findViewById(R.id.wave_single_card_thumbnail);

            mView = itemView;
        }
    }


    private class WaveCardPinnedAdapter extends RecyclerView.Adapter<WaveCardViewHolder>{

        private LayoutInflater inflater;


        private HashMap<String, Integer> record;
        public WaveCardPinnedAdapter(final Context context){
            wavePinned = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid()).child("waves").child("pinned");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    wavePinned.clear();
                    record = new HashMap<>();

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

                                if (dataSnapshot.hasChild("image_url")){
                                    eventInfo.put("waveImageURL", dataSnapshot.child("image_url").getValue().toString());
                                }else {
                                    eventInfo.put("waveImageURL", null);
                                }

                                eventInfo.put("waveTrend", "trending");
                                eventInfo.put("wavePosts", String.valueOf(dataSnapshot.child("wall").child("posts").getChildrenCount()));
                                eventInfo.put("waveAttending", String.valueOf(waveAttending));

                                if(!record.containsKey(waveID)) {
                                    wavePinned.add(eventInfo);
                                    record.put(waveID, wavePinned.size()-1);
                                    if(waveID.equals(eventManager.getEventID())){
                                        int toExchange = wavePinned.size()-1;
                                        Collections.swap(wavePinned,0, toExchange);
                                        record.put(waveID, 0);
                                        record.put(wavePinned.get(toExchange).get("waveID"), wavePinned.size()-1);
                                    }
                                }else {
                                    wavePinned.set(record.get(waveID), eventInfo);
                                }
                                pinnedWavesAdapter.notifyDataSetChanged();
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
        public WaveCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wave_single_card, parent, false);
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
                Picasso.with(holder.waveThumbnail.getContext()).load(waveImageURL)
                        .transform(Transformations.getScaleDownWithView(holder.waveThumbnail))
                        .placeholder(R.drawable.idaelogo6_full).into(holder.waveThumbnail);
            }

            holder.waveThumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (waveID.equals(eventManager.getEventID())){
                        showTopSnackBar(getView(), "You are already riding this wave.", Icons.POOP);
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


        }


        @Override
        public int getItemCount() {
            return wavePinned.size();
        }
    }


    public void showTopSnackBar(View view, String message, int icon){
        TSnackbar snackbar = TSnackbar.make(view, "You are already riding this wave.", TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(icon, 24);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
