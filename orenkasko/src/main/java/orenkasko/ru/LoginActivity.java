package orenkasko.ru;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import orenkasko.ru.Utils.Helpers;

import static orenkasko.ru.Utils.Helpers.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    private static void Log(final String msg) {
        Log.e(TAG, msg);
    }

    private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;

    private long mTime_send_sms = -1;
    private int mCurrStage = -1;
    private View mPhoneView;
    private EditText mPhoneText;

    private View mPhoneCodeView;
    private EditText mPhoneCodeText;
    private TextView mPhoneCodeReplayText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPhoneView = findViewById(R.id.phone_form);
        mPhoneText = (EditText) findViewById(R.id.phone);
        mPhoneText.addTextChangedListener(new Helpers.TextWatcher_Phone(this, mPhoneText));
        mPhoneText.setSelection(mPhoneText.getText().length());
        mPhoneText.clearFocus();

        mPhoneCodeView = findViewById(R.id.phone_pass_form);
        mPhoneCodeText = (EditText) findViewById(R.id.phone_pass);
        mPhoneCodeReplayText = (TextView) findViewById(R.id.replay_pass_textview);
        mPhoneCodeReplayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long delay = (4 * 60 * 1000) - (System.currentTimeMillis() - mTime_send_sms);
                if (delay <= 0) {
                    mCurrStage = 0;
                    attemptLogin();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                    Date resultdate = new Date(delay);
                    Toast(LoginActivity.this, "отпраивть можно тольлко через " + sdf.format(resultdate));
                }
            }
        });
        /*
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView newTextView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
*/


        Button mSignNextButton = (Button) findViewById(R.id.sign_next_button);
        mSignNextButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   attemptLogin();
                                               }
                                           }

        );

        //mCurrStage = 0;
        setCurrStage(true);
        mProgressView = findViewById(R.id.login_progress);
        getWindow().getDecorView().clearFocus();
        mProgressView.setFocusable(true);
    }

    @Override
    public void onBackPressed() {
        mCurrStage -= 1;
        if (mCurrStage >= -1) {
            setCurrStage(false);
        } else {
            finish();
            //super.onBackPressed();
        }

    }

    private void setCurrStage(Boolean success) {
        if (mCurrStage == -1) {

            mPhoneView.setVisibility(View.VISIBLE);
            mPhoneCodeView.setVisibility(View.GONE);

            mPhoneText.setFocusable(false);
            mPhoneText.setText("");

            new Handler(this.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPhoneText.setText(LoginActivity.this.getText(R.string.phone_start_text));

                    mPhoneText.setFocusable(true);
                    mPhoneText.setFocusableInTouchMode(true);
                }
            }, 1000L);
            mCurrStage += 1;
            return;
        }

        if (mCurrStage == 0) {
            if (success) {
                mTime_send_sms = System.currentTimeMillis();
                Toast(LoginActivity.this, "пароль отправлен");
                mCurrStage += 1;

                mPhoneView.setVisibility(View.GONE);
                mPhoneCodeView.setVisibility(View.VISIBLE);

                mPhoneCodeText.setFocusable(false);
                new Handler(this.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPhoneCodeText.setFocusable(true);
                        mPhoneCodeText.setFocusableInTouchMode(true);
                    }
                }, 1000L);
                return;
            } else {
                mPhoneText.setError(getString(R.string.error_incorrect_phone));
                mPhoneText.requestFocus();
                return;
            }
        }

        if (mCurrStage == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mPhoneText.setError(null);
        View focusView = null;

        if (mCurrStage == 0) {
            String phone = mPhoneText.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                mPhoneText.setError(getString(R.string.error_field_required));
                focusView = mPhoneText;

            } else if (!isPhoneValid(phone)) {
                mPhoneText.setError(getString(R.string.error_invalid_phone));
                focusView = mPhoneText;
            }
        } else if (mCurrStage == 1) {
            String phone_pass = mPhoneCodeText.getText().toString();
            if (TextUtils.isEmpty(phone_pass)) {
                mPhoneCodeText.setError(getString(R.string.error_field_required));
                focusView = mPhoneCodeText;

            } else if (!isPhonePassValid(phone_pass)) {
                mPhoneCodeText.setError(getString(R.string.error_invalid_phone_password));
                focusView = mPhoneCodeText;
            }
        }

        if (null != focusView) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 17;
    }

    private boolean isPhonePassValid(String phone_pass) {
        return phone_pass.length() >= 4;
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
        }
        getWindow().getDecorView().clearFocus();
        mProgressView.setFocusable(show);
        mPhoneText.setFocusable(!show);
        mPhoneCodeText.setFocusable(!show);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return false;
            }

            //// TODO: 13.11.2017 register new phone or autorize

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            setCurrStage(success);

            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

