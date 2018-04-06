package paradise.ccclxix.projectparadise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class InitialAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide(); //TODO solve problem when hidding action bar, it has something to do
                                //with the theme.
        setContentView(R.layout.activity_initial_acitivity);
    }
}
