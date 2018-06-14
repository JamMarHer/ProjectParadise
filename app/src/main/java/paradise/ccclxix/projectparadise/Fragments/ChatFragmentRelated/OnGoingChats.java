package paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class OnGoingChats extends Fragment{

    UsersAdapter usersAdapter;
    RecyclerView listAttendingUsers;
    private FirebaseBuilder firebase = new FirebaseBuilder();

    Picasso picasso;
    AppManager appManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .build();

        picasso = new Picasso.Builder(getActivity()).downloader(new OkHttp3Downloader(okHttpClient)).build();
        View inflater1 = inflater.inflate(R.layout.consersations_fragments, null);
        listAttendingUsers = inflater1.findViewById(R.id.conversations_recyclerView);
        usersAdapter = new UsersAdapter(getContext());
        listAttendingUsers.setAdapter(usersAdapter);
        listAttendingUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        return inflater1;
    }


    private class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        ImageView thumpnail;

        ImageView notification;

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_single_user_layout);
            thumpnail = itemView.findViewById(R.id.profile_image_single_user_layout);
            notification = itemView.findViewById(R.id.notification_icon_single_user_layout);
            mView = itemView;
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<OnGoingChats.UsersViewHolder>{

        private LayoutInflater inflater;

        private List<String> userIdsList;
        private List<String> usernameList;
        private List<String> userThumbnail;

        public UsersAdapter(final Context context){
            if (firebase.auth_id() == null){
                return;
            }
            userIdsList = new ArrayList<>();
            usernameList = new ArrayList<>();
            userThumbnail = new ArrayList<>();
            final String personalUN = appManager.getCredentialM().getUsername();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("messages")
                    .child(firebase.auth_id());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userIdsList.clear();
                    usernameList.clear();
                    userThumbnail.clear();
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
                        DatabaseReference userDatabaseReference = firebaseDatabase.getReference().child("users").child(dataSnapshot1.getKey());
                        userDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String otherUN = dataSnapshot.child("username").getValue().toString();
                                if(!personalUN.equals(otherUN)){
                                    usernameList.add(otherUN);
                                    if(dataSnapshot.hasChild("profile_picture")){
                                        userThumbnail.add(dataSnapshot.child("profile_picture").getValue().toString());
                                    }else {
                                        userThumbnail.add("");
                                    }
                                    userIdsList.add(dataSnapshot1.getKey());
                                }

                                usersAdapter.notifyDataSetChanged();
                                inflater =LayoutInflater.from(context);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });

        }



        @Override
        public OnGoingChats.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.user_single_layout, parent, false);
            OnGoingChats.UsersViewHolder holder = new OnGoingChats.UsersViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(final OnGoingChats.UsersViewHolder holder, int position) {
            final String userID = userIdsList.get(position);
            final String thumbnail = userThumbnail.get(position);
            final String username = usernameList.get(position);
            holder.username.setText(username);

            if (!TextUtils.isEmpty(thumbnail))
                picasso.load(thumbnail)
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.ic_import_export).into(holder.thumpnail);



            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("user_id", userID);
                    intent.putExtra("username_other", username);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return usernameList.size();
        }
    }

}
