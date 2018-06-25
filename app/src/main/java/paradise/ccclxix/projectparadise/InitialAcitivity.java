package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import paradise.ccclxix.projectparadise.Animations.ResizeAnimation;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;


public class InitialAcitivity extends AppCompatActivity {

    private ImageView logo_welcome;
    private TextView idae_title;
    private LinearLayout loginRegisterLayout;
    private FirebaseBuilder firebase = new FirebaseBuilder();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial_acitivity);
        idae_title = findViewById(R.id.idae_title2);
        loginRegisterLayout = findViewById(R.id.registration_buttons);
        firebase = new FirebaseBuilder();
    }


    @Override
    public void onStart(){
        super.onStart();
        final FirebaseUser currentUser = firebase.auth().getCurrentUser();
        logo_welcome = findViewById(R.id.welcome_logo);
        loginRegisterLayout.setVisibility(View.INVISIBLE);
        ResizeAnimation resizeAnimation = new ResizeAnimation(logo_welcome, 450);
/*
        ConstraintLayout constraintLayout = findViewById(R.id.initial_activity_constraint_layout);
        AnimationDrawable ad = (AnimationDrawable)constraintLayout.getBackground();
        ad.setEnterFadeDuration(2000);
        ad.setExitFadeDuration(4000);
        ad.start();
*/
        resizeAnimation.setDuration(1500);
        logo_welcome.startAnimation(resizeAnimation);

        if (currentUser == null){
            loginRegisterLayout.setVisibility(View.VISIBLE);
        }else {
            Intent intent = new Intent(InitialAcitivity.this, MainActivity.class);
            intent.putExtra("source", "logged_in");
            startActivity(intent);
            finish();
        }




    }

    public void register (View view){
        Intent intent = new Intent(InitialAcitivity.this, RegistrationActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }

    public void login (View view){
        Intent intent = new Intent(InitialAcitivity.this, LoginActivity.class);
        InitialAcitivity.this.startActivity(intent);
    }
}
