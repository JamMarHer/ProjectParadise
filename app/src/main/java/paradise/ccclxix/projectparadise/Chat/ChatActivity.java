package paradise.ccclxix.projectparadise.Chat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;

public class ChatActivity extends AppCompatActivity {

    private String mChatUserID;
    private String mChatUserName;
    private Toolbar toolbar;
    private String username;

    private Button chatAddButton;
    private Button chatSendButton;
    private EditText chatMessageText;
    private RecyclerView chatMessages;
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
    private SnackBar snackbar = new SnackBar();
    private FirebaseBuilder firebase = new FirebaseBuilder();

    /*
        TODO Enhance this class so it can also handle the chats inside the events.
             Basically each event should have its own general chat.
                    Shares tab.
             _____________________
             |Friends|Convos| Blob|
             |       |      |     |
             |       |      |     |
             |       |      |     |


     */

    private ImageView otherThumbnailTitle;
    private TextView otherUsernameTitle;

    AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        setContentView(R.layout.activity_chat);
        if(firebase.getCurrentUser() != null){
            username = appManager.getCredentialM().getUsername();
        }
        mChatUserID = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("username_other");

        otherThumbnailTitle = findViewById(R.id.other_thumbnail_title);
        otherUsernameTitle = findViewById(R.id.other_username_title);
        otherUsernameTitle.setText(mChatUserName);

        chatAddButton = findViewById(R.id.chat_add);
        chatSendButton = findViewById(R.id.chat_send);
        chatMessageText = findViewById(R.id.chat_message);
        chatMessages = findViewById(R.id.chat_messages);
        swipeRefreshLayout = findViewById(R.id.chat_swipe_layout);
        linearLayoutManager = new LinearLayoutManager(this);
        messageAdapter = new MessageAdapter(messagesList, firebase.auth_id(), getApplicationContext());
        chatMessages.setHasFixedSize(false);
        chatMessages.setLayoutManager(linearLayoutManager);
        chatMessages.setAdapter(messageAdapter);

        loadMessages();
        final DatabaseReference chatDb = firebase.get("chat", firebase.auth_id());
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUserID)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/"+ firebase.auth_id()+"/"+mChatUserID,chatAddMap);
                    chatUserMap.put("chat/"+ mChatUserID + "/" + firebase.auth_id(),chatAddMap);

                    firebase.getDatabase().updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG", databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatMessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessages.scrollToPosition(messagesList.size()-1);
            }
        });
        chatMessageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    chatMessages.scrollToPosition(messagesList.size()-1);
                }
            }
        });

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                snackbar.showEmojiBar(findViewById(android.R.id.content),"You are good son.", Icons.COOL);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            StorageReference imageStorage = FirebaseStorage.getInstance().getReference();
            Uri imageUri = data.getData();

            final String currentUser = "messages/"+firebase.auth_id()+"/" + mChatUserID;
            final String chatUserRef = "messages/"+mChatUserID+"/" + firebase.auth_id();

            DatabaseReference userMessagePush = firebase.getMessages(mChatUserID).push();

            final String pushID = userMessagePush.getKey();

            StorageReference filePath = imageStorage.child("message_images").child(pushID+".jpg");
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
                        messageMap.put("from", firebase.auth_id());

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(currentUser + "/" + pushID, messageMap);
                        messageUserMap.put(chatUserRef + "/" + pushID, messageMap);

                        chatMessageText.setText("");

                        firebase.getDatabase().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
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

    private void loadMessages(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebase.getMessages(mChatUserID);
        Query messageQuery = databaseReference.limitToLast(ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
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
        });
    }

    private void sendMessage(){
        final String message = chatMessageText.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String currentUserRef = "messages/" + firebase.auth_id()+ "/"+ mChatUserID;
            String chatUserRef = "messages/" + mChatUserID + "/" + firebase.auth_id();

            DatabaseReference userMessagePush = firebase.getMessages(mChatUserID).push();


            String pushID = userMessagePush.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", firebase.auth_id());
            messageMap.put("fromUsername", username);
            messageMap.put("toUsername", mChatUserName);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/"+ pushID, messageMap);
            messageUserMap.put(chatUserRef + "/"+ pushID, messageMap);

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
