package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseAuth;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class HomeFragment extends HolderFragment implements EnhancedFragment {


    private CredentialsManager credentialsManager;
    private TextView personalUsername;
    private ImageView settingsImageView;
    private ImageView infoImageView;
    private ImageView shareWaveImageView;

    View generalView;
    EventManager eventManager;


    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        eventManager = new EventManager(getContext());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, null);
        credentialsManager = new CredentialsManager(getContext());
        generalView = view;
        personalUsername = view.findViewById(R.id.personal_username);
        settingsImageView = view.findViewById(R.id.settings_Imageview);
        infoImageView = view.findViewById(R.id.info_Imageview);
        shareWaveImageView = view.findViewById(R.id.share_wave);

        personalUsername.setText(credentialsManager.getUsername());


        View settingsPopupView = inflater.inflate(R.layout.settings_popup, null);

        View shareWavePopupView = inflater.inflate(R.layout.share_wave_popup, null);
        ImageView qrCode = shareWavePopupView.findViewById(R.id.qrCode);
        TextView eventname = shareWavePopupView.findViewById(R.id.waveName);
        if (eventManager.getEventID() != null){
            qrCode.setImageBitmap(getEventQR());
            eventname.setText(eventManager.getEventName());
        }

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow settingsPopupWindow = new PopupWindow(settingsPopupView, width, height);
        final PopupWindow shareWavePopupWindow = new PopupWindow(shareWavePopupView, width,height);
        settingsPopupWindow.setAnimationStyle(R.style.AnimationPopUpWindow);
        shareWavePopupWindow.setAnimationStyle(R.style.AnimationPopUpWindow);

        settingsImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    settingsPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                return true;
            }

        });

        shareWaveImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    shareWavePopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
                return true;
            }
        });

        Button logoutButton = settingsPopupView.findViewById(R.id.logoutButton);
        Button updateProfilePicture = settingsPopupView.findViewById(R.id.updateProfilePicture);
        Button closeSettings = settingsPopupView.findViewById(R.id.close_settings);
        final Button closeShareWave = shareWavePopupView.findViewById(R.id.close_share);

        closeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsPopupWindow.dismiss();
            }
        });

        closeShareWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWavePopupWindow.dismiss();
            }
        });


        updateProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TSnackbar snackbar = TSnackbar.make(container, "Not yet, son.", TSnackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.setIconLeft(R.drawable.fire_emoji, 24);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
                TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });

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

        return view;
    }


    private Bitmap getEventQR(){
        return QRCode.from((String)eventManager.getEventID()).bitmap();
    }


    @Override
    public String getName() {
        return null;
    }
}
