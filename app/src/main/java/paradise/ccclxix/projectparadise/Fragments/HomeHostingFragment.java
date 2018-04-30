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

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Loaders.LoaderAdapter;
import paradise.ccclxix.projectparadise.Loaders.StringLoader;
import paradise.ccclxix.projectparadise.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeHostingFragment extends HolderFragment implements EnhancedFragment {

    private LoaderAdapter loaderAdapter;
    private StringLoader stringLoader;
    private LayoutInflater popupInflater;
    private EventManager eventManager;

    private TextView eventName;
    private TextView currentlyAttending;
    private TextView songName;
    private TextView albumName;
    private TextView artistName;
    private TextView nextSongName;
    private ImageView songImage;
    private Button addHostButton;
    private Button removeHostButton;
    private Button removeAttendantButton;
    private Button sendNotificationButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        loaderAdapter = new LoaderAdapter(getContext());
        getLoaderManager().initLoader(R.id.string_loader_id, null, loaderCallbacks);
        stringLoader = new StringLoader(getContext());
        eventManager = new EventManager(getContext());



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
        eventName.setText(eventManager.getEvent().getName());
        System.out.println(eventManager.getEvent().getName());
        currentlyAttending.setText(String.valueOf(eventManager.getEvent().getAttending().size()));

    }

    private Bitmap getEventQR(){
        return QRCode.from(eventManager.getEvent().getEventID()).bitmap();
    }

    private LoaderManager.LoaderCallbacks<List<String>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return stringLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            loaderAdapter.swapData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            ArrayList<String> list = new ArrayList<>();
            list.add("working");
            loaderAdapter.swapData(list);
        }
    };

    @Override
    public LoaderAdapter getLoaderAdapter() {
        return this.loaderAdapter;
    }

    @Override
    public String getName() {
        return null;
    }
}
