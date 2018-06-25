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
import android.widget.EditText;
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
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.YoutubeService;

public class WavesSubscribeActivity extends AppCompatActivity {

    private FirebaseBuilder firebase = new FirebaseBuilder();

    private List<HashMap<String, String>> waves;
    private AppManager appManager;

    // Youtube
    @BindView(R.id.resultText) TextView resultText;
    @BindView(R.id.searchBtn) Button searchBtn;
    @BindView(R.id.searchField) EditText searchField;
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    private GoogleAccountCredential mCredential;

    // Facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView testingText;
    private Button facebookLogout;

    final int sdk = android.os.Build.VERSION.SDK_INT;


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_post_subscribe);
        ButterKnife.bind(this);
        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);
        ImageView backButton = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        settings.setVisibility(View.INVISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try {
            setupYoutube();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setupYoutube() throws IOException {
        YoutubeService youtube = new YoutubeService();
        youtube.authorize();
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
