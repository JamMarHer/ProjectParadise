package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Transformations;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaveOverview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WaveOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaveOverview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "waveID";
    private static final String ARG_NAME = "waveName";
    private static final String ARG_MEM = "waveMembers";
    private static final String ARG_POSTS = "wavePosts";
    private static final String ARG_POINTS = "wavePoints";
    private static final String ARG_THUMB = "waveThumbnail";



    private TextView mWaveName;
    private TextView mWaveMembers;
    private TextView mWavePosts;
    private TextView mWavePoints;

    private ImageView mWaveThumbnail;

    // TODO: Rename and change types of parameters
    private String waveID;
    private String waveName;
    private String waveMembers;
    private String wavePosts;
    private String wavePoints;
    private String waveThumbnail;

    private OnFragmentInteractionListener mListener;

    Picasso picasso;
    public WaveOverview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaveOverview.
     */
    // TODO: Rename and change types and number of parameters
    public static WaveOverview newInstance(String param0, String param1, String param2, String param3, String param4,
                                           String param5) {
        WaveOverview fragment = new WaveOverview();
        Bundle args = new Bundle();
        args.putString(ARG_ID, param0);
        args.putString(ARG_NAME, param1);
        args.putString(ARG_MEM, param2);
        args.putString(ARG_POSTS, param3);
        args.putString(ARG_POINTS, param4);
        args.putString(ARG_THUMB, param5);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .cache(new Cache(getActivity().getCacheDir(), 250000000))
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        if (getArguments() != null) {
            waveID = getArguments().getString(ARG_ID);
            waveName = getArguments().getString(ARG_NAME);
            waveMembers = getArguments().getString(ARG_MEM);
            wavePosts = getArguments().getString(ARG_POSTS);
            wavePoints = getArguments().getString(ARG_POINTS);
            waveThumbnail = getArguments().getString(ARG_THUMB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflater1 = inflater.inflate(R.layout.fragment_wave_overview, container, false);

        mWaveName = inflater1.findViewById(R.id.wave_overview_name);
        mWaveMembers = inflater1.findViewById(R.id.wave_overview_number_members);
        mWavePosts = inflater1.findViewById(R.id.wave_overview_number_posts);
        mWavePoints = inflater1.findViewById(R.id.wave_overview_number_points);
        mWaveThumbnail = inflater1.findViewById(R.id.main_wave_thumbnail);


        mWaveName.setText(waveName);
        mWaveMembers.setText(waveMembers);
        mWavePosts.setText(wavePosts);
        mWavePoints.setText(wavePoints);

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase1.getReference()
                .child("events_us")
                .child(waveID)
                .child("image_url");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    picasso.load(dataSnapshot.getValue().toString())
                            .transform(Transformations.getScaleDownWithView(mWaveThumbnail))
                            .placeholder(R.drawable.idaelogo6_full).into(mWaveThumbnail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return inflater1;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
