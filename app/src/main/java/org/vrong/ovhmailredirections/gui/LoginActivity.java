package org.vrong.ovhmailredirections.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import org.vrong.ovhmailredirections.R;
import org.vrong.ovhmailredirections.data.DomainIdLoader;
import org.vrong.ovhmailredirections.data.OvhApiKeys;
import org.vrong.ovhmailredirections.ovh.OvhApi;
import org.vrong.ovhmailredirections.ovh.OvhApiWrapper;

import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mDomain;
    private AutoCompleteTextView mApplicationKey;
    private AutoCompleteTextView mSecretApplicationKey;
    private AutoCompleteTextView mConsumerKey;
    private Spinner mEndPoint;
    private Switch mSave;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mDomain = (AutoCompleteTextView) findViewById(R.id.domain);
        mApplicationKey = (AutoCompleteTextView) findViewById(R.id.application_key);
        mSecretApplicationKey = (AutoCompleteTextView) findViewById(R.id.secret_application_key);
        mConsumerKey = (AutoCompleteTextView) findViewById(R.id.consumer_key);
        mSave = (Switch) findViewById(R.id.save_locally);

        mEndPoint = (Spinner) findViewById(R.id.endpoint);
        ArrayAdapter<String> mEndpointAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, OvhApi.getEndpointList());
        mEndPoint.setAdapter(mEndpointAdapter);

        OvhApiKeys id = DomainIdLoader.loadDomainID(this);
        if (id != null) {
            mDomain.setText(id.getDomain());
            mSecretApplicationKey.setText(id.getSecretApplicationKey());
            mApplicationKey.setText(id.getApplicationKey());
            mConsumerKey.setText(id.getConsumerKey());
            int endpointPosition = mEndpointAdapter.getPosition(id.getEndPoint());
            mEndPoint.setSelection(endpointPosition);
        }


        Button mEmailSignInButton = (Button) findViewById(R.id.verify);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mDomain.setError(null);
        mApplicationKey.setError(null);
        mConsumerKey.setError(null);
        mSecretApplicationKey.setError(null);


        // Store values at the time of the login attempt.
        OvhApiKeys id = new OvhApiKeys(mApplicationKey.getText().toString().trim(),
                mSecretApplicationKey.getText().toString().trim(),
                mConsumerKey.getText().toString().trim(),
                mDomain.getText().toString().trim(),
                mEndPoint.getSelectedItem().toString().trim());

        if (mSave.isChecked())
            DomainIdLoader.saveDomainID(LoginActivity.this, id);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (!checkDomain(id.getDomain())) {
            cancel = true;
            focusView = mDomain;
            mDomain.setError("Not a valid domain");
        } else if (!checkKey(id.getApplicationKey(), 16)) {
            cancel = true;
            focusView = mApplicationKey;
            mApplicationKey.setError("Not a valid format");
        } else if (!checkKey(id.getSecretApplicationKey(), 32)) {
            cancel = true;
            focusView = mSecretApplicationKey;
            mSecretApplicationKey.setError("Not a valid format");
        } else if (!checkKey(id.getConsumerKey(), 32)) {
            cancel = true;
            focusView = mConsumerKey;
            mConsumerKey.setError("Not a valid format");
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(id);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean checkDomain(String domain) {
        return Pattern.matches("[a-zA-Z0-9][a-zA-Z0-9-_]{0,61}[a-zA-Z0-9]{0,1}\\.([a-zA-Z]{1,6}|[a-zA-Z0-9-]{1,30}\\.[a-zA-Z]{2,3})", domain);
    }

    private boolean checkKey(String key, int nbchar) {
        if (key.length() != nbchar)
            return false;
        return Pattern.matches("[a-zA-Z0-9]+", key);
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final OvhApiKeys id;

        UserLoginTask(OvhApiKeys id) {
            this.id = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            OvhApiWrapper ovh = new OvhApiWrapper(id);
            return ovh.checkIds();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                Snackbar.make(LoginActivity.this.findViewById(android.R.id.content), "Failed to authenticate, check whether the keys are correct or you have the right access to the API.", 5000)
                        .setAction("Action", null).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

