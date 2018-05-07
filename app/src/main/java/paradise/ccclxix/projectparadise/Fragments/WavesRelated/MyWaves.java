package paradise.ccclxix.projectparadise.Fragments.WavesRelated;

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

import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.R;

public class MyWaves  extends Fragment {

    EventManager eventManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_my_waves, null);
        eventManager = new EventManager(getContext());
        return inflater1;

    }


    private class UsersViewHolder extends RecyclerView.ViewHolder{

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
/*
    private class UsersAdapter extends RecyclerView.Adapter<AttendantsInEvent.UsersViewHolder>{

        private LayoutInflater inflater;

        private List<String> userIdsList;
        private List<String> usernameList;

        public UsersAdapter(final Context context){
            userIdsList = new ArrayList<>();
            usernameList = new ArrayList<>();
            CredentialsManager cm =  new CredentialsManager(context);
            final String personalUN = cm.getUsername();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("events_us")
                    .child(eventManager.getEventID()).child("attending");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userIdsList.clear();
                    usernameList.clear();
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
                        DatabaseReference userDatabaseReference = firebaseDatabase.getReference().child("users").child(dataSnapshot1.getKey());
                        userDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        public AttendantsInEvent.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.user_single_layout, parent, false);
            AttendantsInEvent.UsersViewHolder holder = new AttendantsInEvent.UsersViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(AttendantsInEvent.UsersViewHolder holder, int position) {
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
*/
}
