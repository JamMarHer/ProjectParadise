package paradise.ccclxix.projectparadise.Fragments.WaveRelated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;

public class WavesSubscribeActivity extends AppCompatActivity {

    private FirebaseBuilder firebase = new FirebaseBuilder();

    private List<HashMap<String, String>> waves;
    private AppManager appManager;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView testingText;
    private Button facebookLogout;

    final int sdk = android.os.Build.VERSION.SDK_INT;


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_post_subscribe);
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupFacebook(){
        testingText = findViewById(R.id.facebook_email);
        facebookLogout = findViewById(R.id.facebook_logout);
        facebookLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_posts", "user_likes"));
        login();
    }

    private void searchFb(AccessToken accessToken){
        final String userId = Profile.getCurrentProfile().getId();
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/"+userId+"/likes?fields=name,id,created_time",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            String foo = userId;
                            testingText.setText(AccessToken.getCurrentAccessToken().getPermissions().toString());
                            testingText.setText(response.getRawResponse());
                        } catch (Exception e) {
                            e.printStackTrace();
                            testingText.setText("We cannot access your Facebook likes. Please make sure you have enabled public access to your likes in your Facebook privacy settings.");
                        }
                    }

                }

        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void login(){
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(!isLoggedIn){
            testingText.setText("if");
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("FB", "SUCCESS");
                            searchFb(accessToken);
                        }

                        @Override
                        public void onCancel() {
                            Log.d("FB", "CANCEL");

                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.d("FB", "ERROR");
                        }
                    });
        }
        else{
            testingText.setText("else");
            searchFb(accessToken);
        }
    }
}
