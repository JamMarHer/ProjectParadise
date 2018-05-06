package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeHostingFragment extends HolderFragment implements EnhancedFragment {

    private LayoutInflater popupInflater;
    private EventManager eventManager;

    private TextView eventName;
    private TextView currentlyAttending;
    private TextView songName;
    private TextView albumName;
    private TextView artistName;
    private TextView nextSongName;
    private ImageView songImage;
    private ImageView privacyImage;
    private Button addHostButton;
    private Button removeHostButton;
    private Button removeAttendantButton;
    private Button sendNotificationButton;

    HashMap<String, Object> event;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        eventManager = new EventManager(getContext());
        event = new HashMap<>();


        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_hosting_home, null);
        eventManager = new EventManager(getContext());
        popupInflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.qr_popup_window, null);
        ImageView qrImage =popupView.findViewById(R.id.qr_image_view);
        qrImage.setImageBitmap(getEventQR());
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setAnimationStyle(R.style.AnimationPopUpWindow);

        eventName = view.findViewById(R.id.eventNameHosting);
        currentlyAttending = view.findViewById(R.id.attendingEventHosting);
        songName = view.findViewById(R.id.songNameHosting);
        albumName = view.findViewById(R.id.albumNameHosting);
        artistName = view.findViewById(R.id.artistNameHosting);
        nextSongName = view.findViewById(R.id.nextSongNameHosting);
        songImage = view.findViewById(R.id.songImageHosting);
        addHostButton = view.findViewById(R.id.addHostHosting);
        removeHostButton = view.findViewById(R.id.removeHostHosting);
        removeAttendantButton = view.findViewById(R.id.removeAttendantHosting);
        sendNotificationButton = view.findViewById(R.id.sendNotificationHosting);
        privacyImage = view.findViewById(R.id.eventPrivacy);
        setupEventInfo();




        final Button displayQR = (Button) view.findViewById(R.id.floatingButtonDisplayQR);
        displayQR.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    popupWindow.dismiss();
                }
                return true;
            }

        });
        return view;
    }


    private void setupEventInfo(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference =firebaseDatabase.getReference()
                .child("events_us")
                .child(eventManager.getEventID());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event.put("name_event", dataSnapshot.child("name_event").getValue().toString());
                eventName.setText((String)event.get("name_event"));
                event.put("event_id", eventManager.getEventID());
                event.put("privacy", dataSnapshot.child("privacy").getValue().toString());
                event.put("latitude", dataSnapshot.child("latitude").getValue().toString());
                event.put("longitude", dataSnapshot.child("longitude").getValue().toString());
                event.put("age_target", dataSnapshot.child("age_target").getValue().toString());
                HashMap<String, HashMap<String, Long>> attending = new HashMap<>();
                HashMap<String, HashMap<String, Long>> attended = new HashMap<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attending").getChildren()) {
                    HashMap<String, Long> inOut = new HashMap<>();
                    inOut.put("in", Long.valueOf(dataSnapshot.child("attending").child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                    attending.put(dataSnapshot1.getKey(), inOut);
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("attended").getChildren()) {
                    HashMap<String, Long> inOut = new HashMap<>();
                    inOut.put("in", Long.valueOf(dataSnapshot.child("attended").child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                    inOut.put("out", Long.valueOf(dataSnapshot.child("attended").child(dataSnapshot1.getKey()).child("out").getValue().toString()));
                    attended.put(dataSnapshot1.getKey(), inOut);
                }


                event.put("attended", attended);
                event.put("attending", attending);
                currentlyAttending.setText(String.valueOf(attending.size()));

                if (event.get("privacy").equals("private")){
                    privacyImage.setImageResource(R.drawable.ic_lock_outline_white_24dp);
                }else {
                    privacyImage.setImageResource(R.drawable.ic_lock_open_white_24dp);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

    }

    private Bitmap getEventQR(){
        return QRCode.from((String)eventManager.getEventID()).bitmap();
    }



    @Override
    public String getName() {
        return null;
    }
}
