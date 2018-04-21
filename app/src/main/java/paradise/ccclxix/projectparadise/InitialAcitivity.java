package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Network.NetworkHandler;
import paradise.ccclxix.projectparadise.Network.NetworkResponse;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class InitialAcitivity extends AppCompatActivity {

    private CredentialsManager credentialsManager;
    final int ALPHA_TIME_ANIMATION= 1002;
    final int TRANSLATE_TIME_ANIMATION = 501;

    private ImageView logo_welcome;
    private TextView idae_title;
    private LinearLayout loginRegisterLayout;
    NetworkHandler networkHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        networkHandler = new NetworkHandler();

        //Hides The action bar from the users view
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //getActionBar().hide(); //TODO solve problem when hidding action bar, it has something to do
                                //with the theme.

        credentialsManager = new CredentialsManager(this);
        setContentView(R.layout.activity_initial_acitivity);
        logo_welcome = findViewById(R.id.welcome_logo);
        idae_title = findViewById(R.id.idae_title);
        loginRegisterLayout = findViewById(R.id.login_register_layout);
        try {
            startAlphaAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void showLoginRegistrarionButtons(){
        idae_title.setVisibility(View.VISIBLE);
        loginRegisterLayout.setVisibility(View.VISIBLE);
    }


    public void register (View view){
        Intent intent = new Intent(InitialAcitivity.this, RegistrationActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }

    public void login (View view){
        Intent intent = new Intent(InitialAcitivity.this, LoginActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }


    private void startAlphaAnimation() throws InterruptedException {
        loginRegisterLayout.setVisibility(View.INVISIBLE);
        idae_title.setVisibility(View.INVISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        logo_welcome.clearAnimation();
        logo_welcome.startAnimation(anim);

        networkHandler.checkLoggedInNetworkRequest(credentialsManager.getUser());
        Thread welcomeThread = new Thread() {
            int currentTime = 0;
            @Override
            public void run() {
                try {
                    super.run();
                    while (networkHandler.isRunning() || currentTime<ALPHA_TIME_ANIMATION) {
                        sleep(36);
                        currentTime += 36;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(credentialsManager.checkLoggedIn()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NetworkResponse networkResponse = networkHandler.getNetworkResponse();
                                Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
                                switch (networkResponse.getStatus()) {
                                    case MessageCodes.OK:
                                        intent.putExtra("source", "logged_in");
                                        finish();
                                        InitialAcitivity.this.startActivity(intent);
                                        break;
                                    case MessageCodes.INCORRECT_TOKEN:
                                        credentialsManager.clear();
                                        showLoginRegistrarionButtons();
                                        break;
                                    case MessageCodes.INCORRECT_FORMAT:
                                        Toast.makeText(InitialAcitivity.this, "There has been a problem with the server response. :/",
                                                Toast.LENGTH_SHORT).show();
                                        credentialsManager.clear();
                                        showLoginRegistrarionButtons();
                                        break;
                                    case MessageCodes.FAILED_CONNECTION:
                                        Toast.makeText(InitialAcitivity.this, "There has been a problem with the connection. :/",
                                                Toast.LENGTH_SHORT).show();
                                        intent.putExtra("source", "logged_in_no_network");
                                        finish();
                                        InitialAcitivity.this.startActivity(intent);
                                        break;
                                }
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoginRegistrarionButtons();
                            }
                        });

                    }
                }
            }
        };
        try {
            welcomeThread.start();
        }catch (Exception e){
            Log.d("INITIAL_A_ANIMATION", e.getMessage());
        }

     }
}
