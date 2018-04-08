package paradise.ccclxix.projectparadise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.Registration.RegistrationActivity;


public class InitialAcitivity extends AppCompatActivity {

    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //getActionBar().hide(); //TODO solve problem when hidding action bar, it has something to do
                                //with the theme.
        register = findViewById(R.id.register_button);
        setContentView(R.layout.activity_initial_acitivity);
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
