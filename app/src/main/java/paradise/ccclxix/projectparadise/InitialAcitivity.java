package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import paradise.ccclxix.projectparadise.APIServices.iDaeClient;
import paradise.ccclxix.projectparadise.BackendVals.ConnectionUtils;
import paradise.ccclxix.projectparadise.BackendVals.ErrorCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class InitialAcitivity extends AppCompatActivity {

    CredentialsManager credentialsManager;
    final int TIME_ANIMATION = 2000;
    private boolean connectionDone = false;

    private ImageView logo_welcome;
    private LinearLayout loginRegisterLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Hides The action bar from the users view
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //getActionBar().hide(); //TODO solve problem when hidding action bar, it has something to do
                                //with the theme.

        credentialsManager = new CredentialsManager(this);
        setContentView(R.layout.activity_initial_acitivity);
        logo_welcome = findViewById(R.id.welcome_logo);
        loginRegisterLayout = findViewById(R.id.login_register_layout);
        try {
            startAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void checkCurrentLoginStatus(){

        if (credentialsManager.checkLoggedIn()){
            System.out.println(credentialsManager.getToken());
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(ConnectionUtils.MAIN_SERVER_API)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            iDaeClient iDaeClient = retrofit.create(paradise.ccclxix.projectparadise.APIServices.iDaeClient.class);

            Call<UserResponse> call = iDaeClient.check_token(credentialsManager.getUser());


            call.enqueue(new Callback<UserResponse>() {

                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.body().getStatus() == 100) {
                        Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
                        intent.putExtra("source","logged_in");
                        finish();
                        InitialAcitivity.this.startActivity(intent);
                    } else if(response.body().getStatus() == ErrorCodes.INCORRECT_TOKEN) {
                        credentialsManager.clear();
                        Toast.makeText(InitialAcitivity.this, "Incorrect formatting", Toast.LENGTH_SHORT).show();
                    }else {
                        credentialsManager.clear();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    t.printStackTrace();
                    credentialsManager.clear();
                    Toast.makeText(InitialAcitivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLoginRegistrarionButtons(){
        logo_welcome.setVisibility(View.INVISIBLE);
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


    private void startAnimation() throws InterruptedException {
        loginRegisterLayout.setVisibility(View.INVISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        logo_welcome.clearAnimation();
        logo_welcome.startAnimation(anim);

        Thread welcomeThread = new Thread() {
            int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    checkCurrentLoginStatus();
                    while (wait < TIME_ANIMATION) {
                        sleep(100);
                        wait += 100;
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
        welcomeThread.start();
     }
}
