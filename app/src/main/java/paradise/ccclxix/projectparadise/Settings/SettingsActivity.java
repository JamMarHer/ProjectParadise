package paradise.ccclxix.projectparadise.Settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.BooleanSetting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.StringSetting;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;

import static java.security.AccessController.getContext;

public class SettingsActivity extends AppCompatActivity {


    private TextView logout;


    private FirebaseAuth mAuth;
    RecyclerView settingsRecyclerView;
    SettingsAdapter settingsAdapter;


    AppManager appManager;

    ArrayList<Setting> settingsList;

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

        settingsList = new ArrayList<>();
        settingsAdapter = new SettingsAdapter(this);
        settingsRecyclerView = findViewById(R.id.settings_recyclerView);
        settingsRecyclerView.setAdapter(settingsAdapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


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


    private class SettingViewHolder extends RecyclerView.ViewHolder{

        TextView settingName;
        Switch settingSwitch;
        TextView settingString;
        TextView settingTitle;
        View mView;

        public SettingViewHolder(View itemView) {
            super(itemView);
            settingName = itemView.findViewById(R.id.setting_name);
            settingSwitch = itemView.findViewById(R.id.setting_switch);
            settingString =itemView.findViewById(R.id.setting_string);
            settingTitle = itemView.findViewById(R.id.setting_title);
            mView = itemView;
        }
    }

    private class SettingsAdapter extends RecyclerView.Adapter<SettingViewHolder>{

        private LayoutInflater inflater;
        ProgressDialog progressDialog;


        public SettingsAdapter(final Context context){
            Map<String, Map<String, Setting>> settings = appManager.getSettingsM().getSettings();

            for (final String parentSettingKey : SettingsManager.PARENT_ORDER){
                StringSetting stringSetting = new StringSetting("TITLE", parentSettingKey);
                settingsList.add(stringSetting);
                for (String settingKey : settings.get(parentSettingKey).keySet()){
                    settingsList.add(settings.get(parentSettingKey).get(settingKey));
                    inflater = LayoutInflater.from(context);
                }
            }


        }



        @Override
        public SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.single_setting, parent, false);
            SettingViewHolder holder = new SettingViewHolder(view);
            return holder;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final SettingViewHolder holder, final int position) {
            final String settingName = settingsList.get(position).getName();
            final String settingDescription = settingsList.get(position).getDescription();
            final String settingType = settingsList.get(position).getType();

            if (settingName.equals("TITLE")){
                StringSetting ss = (StringSetting)settingsList.get(position);
                holder.settingTitle.setText(ss.getDescription());
                holder.settingString.setVisibility(View.INVISIBLE);
                holder.settingSwitch.setVisibility(View.INVISIBLE);
                holder.settingName.setVisibility(View.INVISIBLE);

            }else if (settingType.equals("STR")){
                StringSetting ss = (StringSetting)settingsList.get(position);
                holder.settingName.setText(SettingsManager.getSettingChildType(settingName));
                if (settingName.equals(SettingsManager.PASSWORD_TYPE)){
                    holder.settingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final FirebaseUser user = mAuth.getCurrentUser();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyDialogTheme);
                            builder.setTitle("Provide old password");

                            final EditText input = new EditText(getApplicationContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            input.setHint("old password...");
                            builder.setView(input);
                            if (progressDialog == null) {
                                progressDialog = new ProgressDialog(SettingsActivity.this);
                                progressDialog.setMessage("one sec...");
                                progressDialog.setIndeterminate(true);
                            }



                            builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    String oldPass = input.getText().toString();
                                    AuthCredential authCredential = EmailAuthProvider.getCredential(
                                            appManager.getCredentialM().getEmail(), oldPass);
                                    user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                progressDialog.setMessage("another sec...");
                                                progressDialog.setIndeterminate(true);
                                                AlertDialog.Builder buildernewPass = new AlertDialog.Builder(SettingsActivity.this);
                                                buildernewPass.setTitle("Provide new password");


                                                final EditText input = new EditText(getApplicationContext());
                                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                input.setHint("new password...");
                                                buildernewPass.setView(input);


                                                buildernewPass.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        progressDialog.show();
                                                        String newPass = input.getText().toString();
                                                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()){
                                                                    progressDialog.dismiss();
                                                                    Log.d("CHANGING_PASSWORD", "Failed");
                                                                }else {
                                                                    progressDialog.dismiss();
                                                                    showTopSnackBar(holder.mView,"Password updated", Icons.COOL);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                buildernewPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dialog.cancel();
                                                    }
                                                });

                                                buildernewPass.show();



                                            }else{
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();


                        }
                    });
                    //TODO Add prompt.
                }

                holder.settingString.setVisibility(View.INVISIBLE);
                holder.settingSwitch.setVisibility(View.INVISIBLE);
                holder.settingTitle.setVisibility(View.INVISIBLE);
                // TODO Come up with a simple way to edit text for the settings.`


            }else if (settingType.equals("BOOL")){
                final BooleanSetting bs = (BooleanSetting) settingsList.get(position);
                holder.settingName.setText(SettingsManager.getSettingChildType(settingName));
                holder.settingSwitch.setChecked(bs.getValue());
                holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        try{
                            bs.setValue(b);
                        }catch (Exception e){
                            Log.d("UPDATING_BOOL_SETTING", e.getMessage());
                        }
                    }
                });


                holder.settingString.setVisibility(View.INVISIBLE);
                holder.settingTitle.setVisibility(View.INVISIBLE);
            }


        }


        public void showTopSnackBar(View view, String message, int icon){
            TSnackbar snackbar = TSnackbar.make(view, message, TSnackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setIconLeft(icon, 24);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#CC000000"));
            TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

        @Override
        public int getItemCount() {
            return settingsList.size();
        }
    }

}
