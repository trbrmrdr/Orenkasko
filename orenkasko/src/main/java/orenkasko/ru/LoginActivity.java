package orenkasko.ru;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.Utils.Helpers;


import static orenkasko.ru.LoginActivity.Stage.Stage_End;
import static orenkasko.ru.LoginActivity.Stage.Stage_Phone;
import static orenkasko.ru.LoginActivity.Stage.Stage_Start;
import static orenkasko.ru.Utils.Helpers.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static void Log(final String msg) {
        Log.e(TAG, msg);
    }

    private UserLoginTask mAuthTask = null;

    @Bind(R.id.login_progress)
    View mProgressView;

    class Stage {
        static final short
                Stage_Start = -1,
                Stage_Phone = 0,
                Stage_End = 1;
    }

    private int mCurrStage = Stage_Start;

    @Bind(R.id.phone)
    EditText mPhoneText;
    @Bind(R.id.phone_pass)
    EditText mPhoneCodeText;

    @Bind(R.id.phone_form)
    LinearLayout mPhoneForm;

    @OnClick(R.id.sign_next_button)
    public void next_button(View v) {
        processCurrStage(mCurrStage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //mCurrStage = -1;//начало
        //mCurrStage = Stage_Phone;//
        String last_phone = Application.getData().getPhone();
        if (last_phone.length() > 0) {

            Helpers.SetAutoInputText(this, mPhoneText, 2000L, last_phone,
                    new Runnable() {
                        @Override
                        public void run() {
                            Helpers.StartClean(LoginActivity.this, OrdersActivity.class);
                        }
                    });
        } else {
            setCurrStage(true);
        }

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

    boolean hasAddChangeListener = false;

    private void setCurrStage(Boolean success) {
        switch (mCurrStage) {
            case Stage_Start: {
                if (success) {
                    mPhoneCodeText.setFocusable(false);
                    //mPhoneCodeText.setFocusable(false);
                    //mPhoneCodeText.clearFocus();
                    mCurrStage = Stage_Phone;

                    Helpers.SetAutoInputText(this, mPhoneText, 1000L, getText(R.string.phone_start_text),
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (!hasAddChangeListener) {
                                        hasAddChangeListener = true;
                                        mPhoneText.addTextChangedListener(new Helpers.TextWatcher_Phone(LoginActivity.this, mPhoneText));

                                        mPhoneCodeText.setFocusable(true);
                                        mPhoneCodeText.setFocusableInTouchMode(true);
                                    }
                                }
                            });

                } else {
                    finish();
                }
            }
            break;
            case Stage_Phone:
                if (success) {

                    Application.getData().savePhone(mPhoneText.getText().toString());

                    Intent intent = new Intent(this, OrdersActivity.class);
                    intent.putExtra(OrdersActivity.FIRST_OPEN, true);
                    Helpers.StartClean(this, intent);

                    mCurrStage = Stage_End;
                    return;
                } else {
                    //_____________
                    mPhoneText.setError(getString(R.string.error_incorrect_phone));
                    mPhoneText.setSelection(mPhoneText.getText().length());
                    //mPhoneText.requestFocus();

                    //mPhoneCodeText.setFocusableInTouchMode(true);
                    mPhoneCodeText.setError(getString(R.string.error_incorrect_phone_code));
                    mPhoneCodeText.setSelection(mPhoneCodeText.getText().length());
                    //mPhoneCodeText.requestFocus();
                    //Helpers.SetAutoInputText(this, mPhoneCodeText, 1000L);

                    Toast(this, getString(R.string.error_incorrect_phone_pass));
                }
                return;
            case Stage_End:
                break;
        }

        return;
    }

    private void processCurrStage(int stage) {
        mCurrStage = stage;
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mPhoneText.setError(null);
        mPhoneCodeText.setError(null);

        View focusView = null;

        switch (mCurrStage) {
            case Stage_Phone: {
                String phone = mPhoneText.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    mPhoneText.setError(getString(R.string.error_field_required));
                    focusView = mPhoneText;
                    break;
                } else if (!isPhoneValid(phone)) {
                    mPhoneText.setError(getString(R.string.error_invalid_phone));
                    focusView = mPhoneText;
                    break;
                }

                String phone_pass = mPhoneCodeText.getText().toString();
                if (TextUtils.isEmpty(phone_pass)) {
                    mPhoneCodeText.setError(getString(R.string.error_field_required));
                    focusView = mPhoneCodeText;
                    break;
                } else if (!isPhonePassValid(phone_pass)) {
                    mPhoneCodeText.setError(getString(R.string.error_invalid_phone_password));
                    focusView = mPhoneCodeText;
                    break;
                }
            }
            break;
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
        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        getWindow().getDecorView().clearFocus();
        mProgressView.setFocusable(show);
        if (show) {
            mPhoneText.setFocusable(false);
            mPhoneCodeText.setFocusable(false);
        } else {
            mPhoneText.setFocusable(true);
            mPhoneCodeText.setFocusable(true);

            mPhoneCodeText.setFocusableInTouchMode(true);
            mPhoneText.setFocusableInTouchMode(true);
        }
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
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return false;
            }

            //// TODO: 13.11.2017 register new phone or autorize

            return true;
            //return false;
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

