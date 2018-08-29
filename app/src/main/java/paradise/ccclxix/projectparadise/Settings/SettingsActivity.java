package paradise.ccclxix.projectparadise.Settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import paradise.ccclxix.projectparadise.BuildConfig;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.AppManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.Interfaces.Setting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsManager;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.BooleanSetting;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.SettingsRelated.StringSetting;
import paradise.ccclxix.projectparadise.Fragments.PersonalRelated.EditProfileActivity;
import paradise.ccclxix.projectparadise.InitialAcitivity;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.OkHttp3Helpers;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;

public class SettingsActivity extends AppCompatActivity {


    private TextView logout;
    private TextView appVersion;

    public static final String TAG = "Settings";

    RecyclerView settingsRecyclerView;
    SettingsAdapter settingsAdapter;

    AppManager appManager;
    SnackBar snackbar = new SnackBar();
    FirebaseBuilder firebase = new FirebaseBuilder();

    ArrayList<Setting> settingsList;

    View mProgressView;
    View mSettingsView;

    Picasso picasso;

    // User information
    TextView username;
    TextView userState;
    TextView userNumWaves;
    TextView userNumPosts;
    ImageView userProfilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appManager = new AppManager();
        appManager.initialize(getApplicationContext());
        this.picasso =  new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(
                OkHttp3Helpers.getOkHttpClient(this.TAG, getApplicationContext()))).build();


        AppBarLayout toolbar = findViewById(R.id.appBarLayout);
        ImageView back = toolbar.getRootView().findViewById(R.id.toolbar_back_button);
        ImageView settings = toolbar.getRootView().findViewById(R.id.main_settings);

        settingsList = new ArrayList<>();
        settingsAdapter = new SettingsAdapter(this);
        settingsRecyclerView = findViewById(R.id.settings_recyclerView);
        mProgressView = findViewById(R.id.settings_progress);
        mSettingsView = findViewById(R.id.settings_layout);

        settingsRecyclerView.setAdapter(settingsAdapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        username = findViewById(R.id.personal_username);
        userState = findViewById(R.id.personal_status);
        userNumWaves = findViewById(R.id.numberWaves);
        userNumPosts = findViewById(R.id.numberVerified);
        userProfilePic = findViewById(R.id.personal_profile_picture);

        username.setText(appManager.getCredentialM().getUsername());
        userNumWaves.setText(appManager.getCredentialM().getNumWaves());
        userNumPosts.setText(appManager.getCredentialM().getNumPermanents());

        if (!appManager.getCredentialM().getProfilePic().isEmpty()){
            picasso.load(appManager.getCredentialM().getProfilePic())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_import_export).into(userProfilePic);
        }

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });


        settings.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        appVersion = findViewById(R.id.app_version);
        appVersion.setText(String.format("Ver. %s", BuildConfig.VERSION_NAME));
        logout = findViewById(R.id.settings_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.auth().signOut();

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
        ProgressBar progressBar;


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
                if (settingName.equals(SettingsManager.EMAIL_TYPE)){
                    holder.settingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyDialogTheme);
                            builder.setTitle("Provide new email address");

                            final EditText input = new EditText(getApplicationContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                            input.setHint("Enter your desired email address");
                            builder.setView(input);
                            builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final FirebaseUser user = firebase.getCurrentUser();
                                    showProgress(true);
                                    String newEmail = input.getText().toString();
                                    if (TextUtils.isEmpty(newEmail)){
                                        snackbar.showEmojiBar(mSettingsView," ...", Icons.POOP);
                                    }else {
                                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()){
                                                    showProgress(false);
                                                    Log.d("CHANGING_EMAIL", "Failed");
                                                }else {
                                                    showProgress(false);
                                                    snackbar.showEmojiBar(mSettingsView,"Email updated", Icons.COOL);
                                                }
                                            }
                                        });
                                    }

                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                    showProgress(false);
                                }
                            });
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    showProgress(false);
                                }
                            });
                            showProgress(false);
                            builder.show();
                        }
                    });

                }
                if (settingName.equals(SettingsManager.PASSWORD_TYPE)){
                    holder.settingName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final FirebaseUser user = firebase.getCurrentUser();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.MyDialogTheme);
                            builder.setTitle("Provide old password");

                            final EditText input = new EditText(getApplicationContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            input.setHint("old password...");
                            builder.setView(input);


                            builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgress(true);
                                    String oldPass = input.getText().toString();
                                    if (TextUtils.isEmpty(oldPass)){
                                        snackbar.showEmojiBar(mSettingsView, " ...", Icons.POOP);
                                        return;
                                    }
                                    AuthCredential authCredential = EmailAuthProvider.getCredential(
                                            appManager.getCredentialM().getEmail(), oldPass);
                                    user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                AlertDialog.Builder buildernewPass = new AlertDialog.Builder(SettingsActivity.this, R.style.MyDialogTheme);
                                                buildernewPass.setTitle("Provide new password");


                                                final EditText input = new EditText(getApplicationContext());
                                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                input.setHint("new password...");
                                                buildernewPass.setView(input);


                                                buildernewPass.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        showProgress(true);
                                                        String newPass = input.getText().toString();
                                                        if (TextUtils.isEmpty(newPass)){
                                                            snackbar.showEmojiBar(mSettingsView," ...", Icons.POOP);
                                                       }else {
                                                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (!task.isSuccessful()){
                                                                        showProgress(false);
                                                                        Log.d("CHANGING_PASSWORD", "Failed");
                                                                    }else {
                                                                        showProgress(false);
                                                                        snackbar.showEmojiBar(mSettingsView,"Password updated", Icons.COOL);
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                                buildernewPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dialog.cancel();
                                                        showProgress(false);
                                                    }
                                                });
                                                buildernewPass.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        showProgress(false);
                                                    }
                                                });
                                                showProgress(false);
                                                buildernewPass.show();



                                            }else{
                                                snackbar.showEmojiBar(mSettingsView, "Password incorrect", Icons.POOP);
                                                showProgress(false);
                                            }
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    showProgress(false);
                                }
                            });

                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    showProgress(false);
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

        @Override
        public int getItemCount() {
            return settingsList.size();
        }
    }

    private void showProgress(boolean show){
        UINotificationHelpers.showProgress(show, settingsRecyclerView,
                mProgressView, getResources().getInteger(android.R.integer.config_shortAnimTime));
    }
}
