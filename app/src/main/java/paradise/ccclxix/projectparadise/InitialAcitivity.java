package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;




public class InitialAcitivity extends AppCompatActivity {

    private CredentialsManager credentialsManager;
    final int ALPHA_TIME_ANIMATION= 1002;
    private ImageView logo_welcome;
    private TextView idae_title;
    private LinearLayout loginRegisterLayout;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_initial_acitivity);
        idae_title = findViewById(R.id.idae_title2);
        loginRegisterLayout = findViewById(R.id.registration_buttons);
    }


    @Override
    public void onStart(){
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        logo_welcome = findViewById(R.id.welcome_logo);
        loginRegisterLayout.setVisibility(View.INVISIBLE);
        ResizeAnimation resizeAnimation = new ResizeAnimation(logo_welcome, 260);
        resizeAnimation.setDuration(999);
        logo_welcome.startAnimation(resizeAnimation);
        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                    long time = System.currentTimeMillis();
                    while(System.currentTimeMillis()-time <= 999){
                        sleep(498);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logo_welcome.clearAnimation();

                            if (currentUser == null){
                                loginRegisterLayout.setVisibility(View.VISIBLE);
                            }else {
                                Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
                                intent.putExtra("source", "logged_in");
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        };
        t.start();

    }



    public void register (View view){
        Intent intent = new Intent(InitialAcitivity.this, RegistrationActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }

    public void login (View view){
        Intent intent = new Intent(InitialAcitivity.this, LoginActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }



    private void showSnackbar(final String message) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
