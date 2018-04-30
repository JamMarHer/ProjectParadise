package paradise.ccclxix.projectparadise.Chat;



import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import paradise.ccclxix.projectparadise.R;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);



        mChatUser = getIntent().getStringExtra("user_id");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        TextView barUsername = (TextView)action_bar_view.findViewById(R.id.usernameCustomActionBar);
        barUsername.setText(mChatUser);
        actionBar.setCustomView(action_bar_view);

    }


}
