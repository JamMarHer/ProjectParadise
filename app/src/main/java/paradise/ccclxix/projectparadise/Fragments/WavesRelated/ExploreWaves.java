package paradise.ccclxix.projectparadise.Fragments.WavesRelated;

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
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.Fragments.SharesRelated.AttendantsInEvent;
import paradise.ccclxix.projectparadise.R;

public class ExploreWaves  extends Fragment {

    EventManager eventManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflater1 = inflater.inflate(R.layout.fragment_explore_waves, null);
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
}
