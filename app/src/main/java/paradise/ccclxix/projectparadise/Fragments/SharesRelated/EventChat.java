package paradise.ccclxix.projectparadise.Fragments.SharesRelated;

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
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.Chat.MessageAdapter;
import paradise.ccclxix.projectparadise.Chat.Messages;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.EventManager;
import paradise.ccclxix.projectparadise.R;

import static android.app.Activity.RESULT_OK;
import static paradise.ccclxix.projectparadise.Chat.ChatActivity.GALLERY_PICK;

public class EventChat extends Fragment{

    private Toolbar toolbar;
    private FirebaseAuth mauth;


    private CredentialsManager credentialsManager;


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

    EventManager eventManager;
    private Button chatAddButton;
    private Button chatSendButton;
    private EditText chatMessageText;
    private RecyclerView chatMessages;

    private Query messageQuery;
    private ChildEventListener eventListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth = FirebaseAuth.getInstance();
        credentialsManager = new CredentialsManager(getContext());
        eventManager = new EventManager(getContext());
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
        messageAdapter = new MessageAdapter(messagesList, mauth.getUid());
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
                showSnackbar("You are good son.");
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
        System.out.println("CREATINGVIEW");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("events_us").child(eventManager.getEventID()).child("messages");
        messageQuery = databaseReference.limitToLast(ITEMS_TO_LOAD);
        messageQuery.addChildEventListener(eventListener);
        return inflater1;
    }

    private void showSnackbar(final String message) {
        TSnackbar snackbar = TSnackbar.make(inflater1.findViewById(android.R.id.content), message, TSnackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(R.drawable.fire_emoji, 24);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = firebaseDatabase.getReference();
            StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
            Uri imageUri = data.getData();

            final String currentUser = "events_us/"+eventManager.getEventID()+"/messages/" + credentialsManager.getUsername();

            String imageName = String.format("%s_.%s.jpg",String.valueOf(System.currentTimeMillis()),credentialsManager.getUsername());


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
            String currentUserRef = "events_us/"+eventManager.getEventID()+"/messages/";

            DatabaseReference databaseReference = firebaseDatabase.getReference();

            String pushID = databaseReference.child("events_us").child(mauth.getUid()).push().getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mauth.getUid());

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef +pushID, messageMap);

            chatMessageText.setText("");

            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
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
