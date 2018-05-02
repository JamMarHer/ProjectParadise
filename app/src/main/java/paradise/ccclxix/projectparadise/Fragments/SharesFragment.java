package paradise.ccclxix.projectparadise.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppModeManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.EnhancedFragment;
import paradise.ccclxix.projectparadise.HolderFragment;
import paradise.ccclxix.projectparadise.Models.EventAttendingItem;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.User;

public class SharesFragment extends HolderFragment implements EnhancedFragment {

    RecyclerView listAttendingUsers;
    AppModeManager appModeManager;

    EventManager eventManager;

    private UsersAdapter usersAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appModeManager = new AppModeManager(getContext());
        if(appModeManager.isHostingMode() || appModeManager.isAttendantMode()) {
            View inflater1 = inflater.inflate(R.layout.fragment_shares, null);
            listAttendingUsers = inflater1.findViewById(R.id.usersAttending);
            eventManager = new EventManager(getContext());
            usersAdapter = new UsersAdapter(getContext());
            listAttendingUsers.setAdapter(usersAdapter);
            listAttendingUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            return inflater1;
        }
        return inflater.inflate(R.layout.fragment_shares, null);

    }


    @Override
    public String getName() {
        return null;
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        ImageView thumpnail;
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            thumpnail = itemView.findViewById(R.id.profile_image);
            mView = itemView;
        }
    }

    public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder>{

        private LayoutInflater inflater;

        private List<String> usersList;
        HashMap<String, HashMap<String, Long>> attendingUsers;

        public UsersAdapter(final Context context){
            attendingUsers = new HashMap<>();
            usersList = new ArrayList<>();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(eventManager.getEventID()).child("attending");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    attendingUsers.clear();
                    usersList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        System.out.println(dataSnapshot1.getKey());
                        HashMap<String, Long> inOut = new HashMap<>();
                        inOut.put("in", Long.valueOf(dataSnapshot.child(dataSnapshot1.getKey()).child("in").getValue().toString()));
                        attendingUsers.put(dataSnapshot1.getKey(), inOut);
                        usersList.clear();

                        usersList.addAll(attendingUsers.keySet());
                    }
                    usersAdapter.notifyDataSetChanged();
                    inflater =LayoutInflater.from(context);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });

        }



        @Override
        public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.user_single_layout, parent, false);
            UsersViewHolder holder = new UsersViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(UsersViewHolder holder, int position) {
            final String usernameString = usersList.get(position);
            holder.username.setText(usernameString);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("user_id", usernameString);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return usersList.size();
        }
    }

}
