package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;




public class InitialAcitivity extends AppCompatActivity {

    private CredentialsManager credentialsManager;
    final int ALPHA_TIME_ANIMATION= 1002;
    private ImageView logo_welcome;
    private TextView idae_title;
    private LinearLayout loginRegisterLayout;
    ApiClientFactory apiClientFactory;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClientFactory = new ApiClientFactory();

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_initial_acitivity);
        idae_title = findViewById(R.id.idae_title);
        loginRegisterLayout = findViewById(R.id.login_register_layout);

        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        credentialsManager = new CredentialsManager(this);



        try {
            startAlphaAnimation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

    }


    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        logo_welcome = findViewById(R.id.welcome_logo);

        if (currentUser == null){
            showLoginRegistrarionButtons();
        }else {
            Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
            intent.putExtra("source", "logged_in");
            startActivity(intent);
            finish();
        }
    }

    private void showLoginRegistrarionButtons(){
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



    private void showSnackbar(final String message) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
