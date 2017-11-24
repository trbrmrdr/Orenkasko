package orenkasko.ru;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import orenkasko.ru.Utils.Helpers;

import static orenkasko.ru.LoginActivity.Stage.Stage_Code;
import static orenkasko.ru.LoginActivity.Stage.Stage_End;
import static orenkasko.ru.LoginActivity.Stage.Stage_Name;
import static orenkasko.ru.LoginActivity.Stage.Stage_Phone;
import static orenkasko.ru.LoginActivity.Stage.Stage_Start;
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

    private final Long mSimulate_network_access_delay = 1500L;

    @Bind(R.id.login_progress)
    View mProgressView;

    private long mTime_send_sms = -1;

    class Stage {
        static final short
                Stage_Start = -1,
                Stage_Phone = 0,
                Stage_Name = 1,
                Stage_Code = 2,
                Stage_End = 3;
    }

    private int mCurrStage = Stage_Start;

    @Bind(R.id.phone_form)
    View mPhoneView;

    @Bind(R.id.phone)
    EditText mPhoneText;

    @Bind(R.id.phone_pass_form)
    View mPhoneCodeView;
    @Bind(R.id.phone_pass)
    EditText mPhoneCodeText;
    @Bind(R.id.replay_pass_textview)
    TextView mPhoneCodeReplayText;

    @OnClick(R.id.replay_pass_textview)
    public void replay_stage_code(View v) {
        long delay = (4 * 60 * 1000) - (System.currentTimeMillis() - mTime_send_sms);
        if (delay <= 0) {
            processCurrStage(Stage_Code);
        } else {
            Toast(LoginActivity.this, getText(R.string.error_text_replay_pass) + new SimpleDateFormat("mm:ss").format(new Date(delay)));
        }
    }

    @Bind(R.id.name_form)
    View mNameView;
    @Bind(R.id.name)
    EditText mNameText;
    @Bind(R.id.credits_check_box)
    CheckBox mCreditCheckBox;

    @OnClick(R.id.text_credits)
    public void credits_click(View v) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_link)));
            startActivity(browserIntent);
        } catch (Exception e) {
        }
    }

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
        String last_phone = Data.getPhone(this);
        if (last_phone.length() > 0) {
            setVisible(mPhoneView);

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

    void setVisible(View view) {
        if (mPhoneView == view) {
            mPhoneView.setVisibility(View.VISIBLE);
            mNameView.setVisibility(View.GONE);
            mPhoneCodeView.setVisibility(View.GONE);

            mPhoneCodeText.setFocusable(false);
        } else if (mNameView == view) {
            mPhoneView.setVisibility(View.GONE);
            mNameView.setVisibility(View.VISIBLE);
            mPhoneCodeView.setVisibility(View.GONE);
        } else if (mPhoneCodeView == view) {
            mPhoneView.setVisibility(View.GONE);
            mNameView.setVisibility(View.GONE);
            mPhoneCodeView.setVisibility(View.VISIBLE);
        }
    }

    boolean hasAddChangeListener = false;

    private void setCurrStage(Boolean success) {
        switch (mCurrStage) {
            case Stage_Start: {
                if (success) {
                    //показываем окно набора телефона
                    mCurrStage = Stage_Phone;
                    setVisible(mPhoneView);

                    Helpers.SetAutoInputText(this, mPhoneText, 1000L, getText(R.string.phone_start_text),
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (!hasAddChangeListener) {
                                        hasAddChangeListener = true;
                                        mPhoneText.addTextChangedListener(new Helpers.TextWatcher_Phone(LoginActivity.this, mPhoneText));
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
                    //показываем окно имени
                    mCurrStage = Stage_Name;
                    setVisible(mNameView);
                } else {
                    //показываем ошибку набора телефона
                    setVisible(mPhoneView);
                    //_____________
                    mPhoneText.setFocusableInTouchMode(true);
                    mPhoneText.setError(getString(R.string.error_incorrect_phone));
                    mPhoneText.setSelection(mPhoneText.getText().length());
                    mPhoneText.requestFocus();
                }
                return;
            case Stage_Name:
                if (success) {
                    //показываем окно ввода пароля СМС
                    mCurrStage = Stage_Code;
                    setVisible(mPhoneCodeView);
                    //_________________
                    mTime_send_sms = System.currentTimeMillis();
                    Toast(LoginActivity.this, "пароль отправлен");

                    Helpers.SetAutoInputText(this, mPhoneCodeText, 1000L);

                } else {
                    //ошибка ввода имени
                    setVisible(mNameView);

                    mNameText.setFocusableInTouchMode(true);
                    mNameText.setError(getString(R.string.error_name_incorrect));
                    mNameText.requestFocus();
                }
                break;
            case Stage_Code: {
                if (success) {
                    Data.savePhone(this, mPhoneText.getText().toString());

                    Intent intent = new Intent(this, BalanceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    this.startActivity(intent);
                } else {
                    //ошибка ввода пароля из СМС
                    setVisible(mPhoneCodeView);
                    mPhoneCodeText.setFocusableInTouchMode(true);
                    mPhoneCodeText.setError(getString(R.string.error_incorrect_phone_code));
                    mPhoneCodeText.requestFocus();
                }
            }
            break;
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
        mNameText.setError(null);
        mPhoneCodeText.setError(null);

        View focusView = null;

        switch (mCurrStage) {
            case Stage_Phone: {
                String phone = mPhoneText.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    mPhoneText.setError(getString(R.string.error_field_required));
                    focusView = mPhoneText;
                } else if (!isPhoneValid(phone)) {
                    mPhoneText.setError(getString(R.string.error_invalid_phone));
                    focusView = mPhoneText;
                }
            }
            break;
            case Stage_Name: {
                String name = mNameText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    mNameText.setError(getString(R.string.error_name_empty));
                    focusView = mNameText;
                } else if (!mCreditCheckBox.isChecked()) {
                    focusView = mCreditCheckBox;
                    Toast(this, getString(R.string.credits_checked_error));
                }
            }
            break;
            case Stage_Code: {
                String phone_pass = mPhoneCodeText.getText().toString();
                if (TextUtils.isEmpty(phone_pass)) {
                    mPhoneCodeText.setError(getString(R.string.error_field_required));
                    focusView = mPhoneCodeText;

                } else if (!isPhonePassValid(phone_pass)) {
                    mPhoneCodeText.setError(getString(R.string.error_invalid_phone_password));
                    focusView = mPhoneCodeText;
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

            if (mCurrStage == Stage_Name)//skip set name
                return true;
            try {
                // Simulate network access.
                Thread.sleep(mSimulate_network_access_delay);
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

