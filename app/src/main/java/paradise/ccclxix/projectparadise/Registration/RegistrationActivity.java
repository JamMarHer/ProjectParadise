package paradise.ccclxix.projectparadise.Registration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.utils.FirebaseBuilder;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.Models.User;
import paradise.ccclxix.projectparadise.R;
import paradise.ccclxix.projectparadise.utils.Icons;
import paradise.ccclxix.projectparadise.utils.SnackBar;
import paradise.ccclxix.projectparadise.utils.UINotificationHelpers;


public class RegistrationActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;


    private View mProgressView;
    private View mRegistrationFormView;
    private boolean running = false;
    private CredentialsManager credentialsManager;
    private EditText usernameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText rePasswordView;

    private FirebaseBuilder firebase = new FirebaseBuilder();
    private SnackBar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        firebase = new FirebaseBuilder();

        credentialsManager = new CredentialsManager();
        credentialsManager.initialize(getApplicationContext());
        usernameView = findViewById(R.id.registration_username);
        emailView = findViewById(R.id.registration_email);
        passwordView = findViewById(R.id.registration_password);
        rePasswordView = findViewById(R.id.registration_re_password);
        rePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });
        mRegistrationFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

    }


    public void register(View view){
        attemptRegistration();
    }

    public void attemptRegistration(){
        if (running){
            return;
        }

        // Reset errors.
        usernameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        rePasswordView.setError(null);

        // Store values at the time for the registration attempt.
        final String username =  usernameView.getText().toString();
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        String passwordRe = rePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid password. Handles all the checks for correct form.
        if (TextUtils.isEmpty(username)){
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)){
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        }
        if (!isEmailValid(email)){
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)){
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwordRe)){
            rePasswordView.setError(getString(R.string.error_field_required));
            focusView = rePasswordView;
            cancel = true;
        }
        if (!isPasswordValid(password)){
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }
        if (!passwordsMatch(password, passwordRe)){
            rePasswordView.setError(getString(R.string.error_passwords_not_match));
            focusView = rePasswordView;
            cancel = true;
        }
        if(!cancel) {
            usernameExist(username, focusView, cancel, new MyCallback() {
                @Override
                public void onCallback(boolean value, View focusView, boolean cancel) {
                    if (value) {
                        usernameView.setError(getString(R.string.error_field_required));
                        focusView = usernameView;
                        cancel = true;
                        snackbar.showEmojiBar("Username already exist", Icons.NON);
                    } else {
                        firebase.auth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("AUTH", "Starting to setup Users");
                                    FirebaseUser current_user = firebase.auth().getCurrentUser();
                                    String userID = current_user.getUid();
                                    DatabaseReference databaseReference = firebase.get("users", userID);
                                    User new_user = new User(username);
                                    firebase.setValue(firebase.get("users", userID), new_user.render(),
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("Setup User", "Starting to setup Users");
                                                        credentialsManager.updateUsername(username);
                                                        if (credentialsManager.getToken() != null) {
                                                            DatabaseReference eventDatabaseReference = firebase.get_user_authId("token");
                                                            firebase.setValue(eventDatabaseReference, credentialsManager.getToken(),
                                                                    new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d("UPDATED_TOKEN", "New token has been added to db.");
                                                                            }
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                                        mainIntent.putExtra("source", "registration");
                                                        startActivity(mainIntent);
                                                        running = false;
                                                        finish();
                                                    }
                                                }
                                            });

                                } else {
                                    showProgress(false);
                                    running = false;
                                    Log.d("ELSE", "Something went wrong");
                                    snackbar.showEmojiBar("The authentication has failed.", Icons.NON);
                                }
                            }
                        });
                    }

                }
            });
        }

    }

    private void setError(TextView textView, String error){
        textView.setError(error);
        textView.requestFocus();
    }

    private void showProgress(final boolean show) {
        UINotificationHelpers.showProgress(show, mRegistrationFormView,mProgressView,
                getResources().getInteger(android.R.integer.config_shortAnimTime));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }


    private boolean passwordsMatch(String password, String passwordRe) {
        return password.equals(passwordRe);
    }

    private boolean isPasswordValid(String password){
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}";
        Log.d("REGEX", Boolean.toString(password.matches(pattern)));
        boolean yes = password.matches(pattern);
        return password.length() > 4 && yes;
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private void usernameExist(String username, final View view, final boolean cancel, final MyCallback callback){
        firebase.get("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    callback.onCallback(true, view, cancel);
                } else {
                    callback.onCallback(false, view, cancel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(false, view, cancel);
            }
        });
    }

    public interface MyCallback {
        void onCallback(boolean value, View view, boolean cancel);
    }



}
