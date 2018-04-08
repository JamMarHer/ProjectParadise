package paradise.ccclxix.projectparadise.Registration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import paradise.ccclxix.projectparadise.APIForms.RegistrationResponseForm;
import paradise.ccclxix.projectparadise.APIForms.UserRegistrationForm;
import paradise.ccclxix.projectparadise.Login.LoginActivity;
import paradise.ccclxix.projectparadise.R;

import static android.Manifest.permission.READ_CONTACTS;

public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {


    private static final int REQUEST_READ_CONTACTS = 0;

    private RegistrationTask registrationTask = null;

    private View mProgressView;
    private View mRegistrationFormView;

    private EditText usernameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText rePasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        mRegistrationFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.register_progress);
        setContentView(R.layout.activity_registration);
    }


    public void register(View view){
        attemptRegistration();
    }

    public void attemptRegistration(){
        if (registrationTask != null){
            return;
        }

        // Reset errors.
        usernameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        rePasswordView.setError(null);

        // Store values at the time for the registration attempt.
        String username =  usernameView.getText().toString();
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
        if (isPasswordValid(password)){
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
            UserRegistrationForm  userRegistrationForm = new UserRegistrationForm(
                                                        username,
                                                        email,
                                                        password
            );

            showProgress(true);
            registrationTask = new RegistrationTask(userRegistrationForm);
            registrationTask.execute((Void) null);
        }

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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegistrationActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegistrationTask extends AsyncTask<Void, Void, RegistrationResponseForm> {

        private final UserRegistrationForm userRegistrationForm;

        RegistrationTask(UserRegistrationForm userRegistrationForm) {
            this.userRegistrationForm = userRegistrationForm;
        }

        @Override
        protected RegistrationResponseForm doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // TODO: finish the registration response form. and its formating.
            // TODO: implement the network exchange.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {

                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            registrationTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            registrationTask = null;
            showProgress(false);
        }
    }
}
