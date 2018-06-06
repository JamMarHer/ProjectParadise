package paradise.ccclxix.projectparadise.Settings;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.R;

public class SettingsActivity extends AppCompatActivity {


    private TextView logout;


    private FirebaseAuth mAuth;

    AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appManager = new AppManager();
        appManager.initialize(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();

        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView back = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);
        ImageView info = toolbar.getRootView().findViewById(R.id.main_info);

        settings.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logout = findViewById(R.id.settings_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                try{
                    appManager.logout();
                }catch (Exception e){
                    Log.d("LOG_OUT", e.getMessage());
                }
                Intent intent = new Intent(SettingsActivity.this, InitialAcitivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SettingsActivity.this.finish();
            }
        });

    }
}
