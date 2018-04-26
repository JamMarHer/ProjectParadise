package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;

import iDaeAPI.IDaeClient;
import iDaeAPI.model.UserCheckTokenRequest;
import iDaeAPI.model.UserCheckTokenResponse;
import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;




public class InitialAcitivity extends AppCompatActivity {

    private CredentialsManager credentialsManager;
    final int ALPHA_TIME_ANIMATION= 1002;
    UserCheckTokenResponse userCheckTokenResponse;
    private ImageView logo_welcome;
    private TextView idae_title;
    private LinearLayout loginRegisterLayout;
    ApiClientFactory apiClientFactory;
    IDaeClient iDaeClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClientFactory = new ApiClientFactory();
        iDaeClient = apiClientFactory.build(IDaeClient.class);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
        logo_welcome.clearAnimation();
        logo_welcome.getLayoutParams().height = 120;
        logo_welcome.getLayoutParams().width = 120;
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
        ResizeAnimation resizeAnimation = new ResizeAnimation(logo_welcome, 90, 120);
        resizeAnimation.setRepeatCount(Animation.INFINITE);
        resizeAnimation.setRepeatMode(Animation.REVERSE);
        resizeAnimation.setDuration(501);
        loginRegisterLayout.setVisibility(View.INVISIBLE);
        idae_title.setVisibility(View.INVISIBLE);
        logo_welcome.clearAnimation();
        logo_welcome.startAnimation(resizeAnimation);


        if (!credentialsManager.checkLoggedIn()) {
            Thread showLogoAnim = new Thread() {
                int currentTime = 0;

                @Override
                public void run() {
                    try {
                        super.run();
                        while (currentTime < ALPHA_TIME_ANIMATION) {
                            sleep(36);
                            currentTime += 36;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showLoginRegistrarionButtons();
                            }
                        });
                    }
                }
            };
            showLogoAnim.start();
        }else {
            final Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
            Thread checkLoginStatus = new Thread() {
                int currentTime = 0;

                @Override
                public void run() {
                    final UserCheckTokenRequest userCheckTokenRequest = new UserCheckTokenRequest();

                    userCheckTokenRequest.setEmail(credentialsManager.getEmail());
                    userCheckTokenRequest.setToken(credentialsManager.getToken());
                    userCheckTokenResponse = iDaeClient.idaeUserAuthPost(userCheckTokenRequest);
                    try {
                        super.run();
                        while (userCheckTokenResponse == null || currentTime < ALPHA_TIME_ANIMATION) {
                            sleep(36);
                            currentTime += 36;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (userCheckTokenResponse.getStatus() == MessageCodes.OK) {
                                    if (userCheckTokenResponse.getAuth()) {
                                        intent.putExtra("source", "logged_in");
                                        finish();
                                        InitialAcitivity.this.startActivity(intent);
                                    } else {
                                        showSnackbar("Logged in from another device, logging out.");
                                        credentialsManager.clear();
                                        showLoginRegistrarionButtons();
                                    }
                                } else {
                                    showSnackbar("There has been a problem authenticating you. Try logging in again!");
                                    credentialsManager.clear();
                                    showLoginRegistrarionButtons();
                                }
                            }
                        });
                    }
                }

            };
            checkLoginStatus.start();
        }
    }

    private void showSnackbar(final String message) {
        Snackbar.make(findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }
}
