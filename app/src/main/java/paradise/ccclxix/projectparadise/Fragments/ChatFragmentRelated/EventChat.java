package paradise.ccclxix.projectparadise.Fragments.ChatFragmentRelated;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.Chat.MessageAdapter;
import paradise.ccclxix.projectparadise.Chat.Messages;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;

import static android.app.Activity.RESULT_OK;

public class EventChat extends Fragment{

    private Toolbar toolbar;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int ITEMS_TO_LOAD = 369;
    public static final int GALLERY_PICK = 1;
    private int pageNumber = 1;
    private String token;

    private int itemPosition = 0;
    private String lastKey = "";
    private String prevKey = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    View inflater1;

    private Button chatAddButton;
    private Button chatSendButton;
    private EditText chatMessageText;
    private RecyclerView chatMessages;

    private Query messageQuery;
    private ChildEventListener eventListener;
    private SnackBar snackbar;
    private FirebaseBuilder firebase = new FirebaseBuilder();

    AppManager appManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getClass().getSimpleName().equals("MainActivity")){
            MainActivity mainActivity = (MainActivity)getActivity();
            appManager = mainActivity.getAppManager();
        }

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message =  dataSnapshot.getValue(Messages.class);
                itemPosition++;
                if (itemPosition ==1){
                    String key = dataSnapshot.getKey();
                    lastKey = key;
                    prevKey = key;
                }
                messagesList.add(message);
                messageAdapter.notifyDataSetChanged();
                chatMessages.scrollToPosition(messagesList.size()-1);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messagesList.clear();
        messageQuery.removeEventListener(eventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        messagesList.clear();
        messageQuery.removeEventListener(eventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater.inflate(R.layout.event_conversation_fragment, null);
        chatAddButton = inflater1.findViewById(R.id.event_chat_add);
        chatSendButton = inflater1.findViewById(R.id.event_chat_send);
        chatMessageText = inflater1.findViewById(R.id.event_chat_message);
        chatMessages = inflater1.findViewById(R.id.event_chat_messages);
        swipeRefreshLayout = inflater1.findViewById(R.id.event_chat_swipe_layout);


        linearLayoutManager = new LinearLayoutManager(getContext());
        messageAdapter = new MessageAdapter(messagesList, firebase.auth_id(), getContext());
        chatMessages.setHasFixedSize(false);
        chatMessages.setLayoutManager(linearLayoutManager);
        chatMessages.setAdapter(messageAdapter);



        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                snackbar.showEmojiBar(inflater1.getRootView().findViewById(android.R.id.content),"You are good, son.", Icons.FIRE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        chatAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("events_us").child(appManager.getWaveM().getEventID()).child("messages");
        messageQuery = databaseReference.limitToLast(ITEMS_TO_LOAD);
        messageQuery.addChildEventListener(eventListener);
        return inflater1;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference();
            StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
            Uri imageUri = data.getData();

            final String currentUser = "events_us/"+appManager.getWaveM().getEventID()+"/messages/" + appManager.getCredentialM().getUsername();

            String imageName = String.format("%s_.%s.jpg",String.valueOf(System.currentTimeMillis()),appManager.getCredentialM().getUsername());


            StorageReference filePath = imageStorage.child("message_images").child(imageName);
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        String downloadURL = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", downloadURL);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(currentUser, messageMap);

                        chatMessageText.setText("");

                        databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.d("UPLOADING_IMAGE", databaseError.getMessage());
                                    messageAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    private void sendMessage(){
        final String message = chatMessageText.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String currentUserRef = "events_us/"+appManager.getWaveM().getEventID()+"/messages/";


            String pushID = firebase.getEvents_authId().push().getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", firebase.auth_id());

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef +pushID, messageMap);

            chatMessageText.setText("");

            firebase.getDatabase().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError !=null){
                        Log.d("CHAT_LOG", databaseError.getMessage());
                    }

                }
            });
        }
    }
}
