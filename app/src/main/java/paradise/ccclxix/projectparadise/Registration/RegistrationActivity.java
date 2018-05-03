package paradise.ccclxix.projectparadise.Registration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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


import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import paradise.ccclxix.projectparadise.BackendVals.MessageCodes;
import paradise.ccclxix.projectparadise.CredentialsAndStorage.CredentialsManager;
import paradise.ccclxix.projectparadise.MainActivity;
import paradise.ccclxix.projectparadise.R;


public class RegistrationActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;

    ApiClientFactory apiClientFactory;


    private View mProgressView;
    private View mRegistrationFormView;
    private boolean running = false;
    private CredentialsManager credentialsManager;
    private EditText usernameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText rePasswordView;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        apiClientFactory = new ApiClientFactory();

        credentialsManager = new CredentialsManager(this);
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
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.


            showProgress(true);
            running = true;

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()){
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String userID = current_user.getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference().child("users").child(userID);

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("username", username);
                        userMap.put("status", "We lit");
                        userMap.put("thumb_image", "default");
                        databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    credentialsManager.updateUsername(username);
                                    if (credentialsManager.getToken() != null){
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference eventDatabaseReference = database.getReference().child("users").child(mAuth.getUid()).child("token");
                                        eventDatabaseReference.setValue(credentialsManager.getToken()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Log.d("UPDATED_TOKEN", "New token has been added to db.");
                                                }
                                            }
                                        });
                                    }
                                    Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                    mainIntent.putExtra("source", "registration");
                                    startActivity(mainIntent);
                                    running = false;
                                    finish();
                                }
                            }
                        });

                    }else {
                        showProgress(false);
                        running = false;
                        showSnackbar("The authentication has failed.");
                    }
                }
            });
        }

    }
    private void showSnackbar(final String message) {
        Snackbar.make(findViewById(android.R.id.content),message,
                Snackbar.LENGTH_LONG).show();
    }

    private void setError(TextView textView, String error){
        textView.setError(error);
        textView.requestFocus();
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
        return password.length() > 4;

    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


}
