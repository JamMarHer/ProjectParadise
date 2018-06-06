package paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.List;

import paradise.ccclxix.projectparadise.Chat.ChatActivity;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;

public class AttendantsInEvent extends Fragment{

    UsersAdapter usersAdapter;
    RecyclerView listAttendingUsers;

    AppManager appManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.attendants_event_fragment, null);
        listAttendingUsers = inflater1.findViewById(R.id.usersAttending);

        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }
        usersAdapter = new UsersAdapter(getContext());
        listAttendingUsers.setAdapter(usersAdapter);
        listAttendingUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        return inflater1;

    }


    private class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        ImageView thumpnail;
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_single_user_layout);
            thumpnail = itemView.findViewById(R.id.profile_image_single_user_layout);
            mView = itemView;
        }
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder>{

        private LayoutInflater inflater;

        private List<String> userIdsList;
        private List<String> usernameList;

        public UsersAdapter(final Context context){
            userIdsList = new ArrayList<>();
            usernameList = new ArrayList<>();
            final String personalUN = appManager.getCredentialM().getUsername();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(appManager.getWaveM().getEventID()).child("attending");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userIdsList.clear();
                    usernameList.clear();
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
                        DatabaseReference userDatabaseReference = firebaseDatabase.getReference().child("users").child(dataSnapshot1.getKey());
                        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String otherUN = dataSnapshot.child("username").getValue().toString();
                                if(!personalUN.equals(otherUN)){
                                    usernameList.add(otherUN);
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
        public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.user_single_layout, parent, false);
            UsersViewHolder holder = new UsersViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(UsersViewHolder holder, int position) {
            final String userID = userIdsList.get(position);
            final String username = usernameList.get(position);
            holder.username.setText(username);


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
