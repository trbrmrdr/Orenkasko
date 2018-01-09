package orenkasko.ru;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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

    //private UserLoginTask mAuthTask = null;
    private boolean mAuthTask = false;
    private String mName = "";

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


    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @OnClick(R.id.sign_next_button)
    public void next_button(@Nullable View v) {

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)) {
            new MaterialDialog.Builder(this)
                    .content("Для связи с сервером необходимо подтвердить разрешение на использования интернета!")
                    .positiveText("Да")
                    .negativeText("нет")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.INTERNET},
                                    MY_PERMISSIONS_REQUEST_INTERNET);

                        }
                    })
                    .show();
        } else
            /* maybe
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new MaterialDialog.Builder(this)
                    .content("Для автосохранения черновиков требуется разрешение на запись!")
                    .positiveText("Да")
                    .negativeText("нет")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        }
                    })
                    .show();
        } else

*/ {
            processCurrStage(mCurrStage);
        }

    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                     permission was granted, yay! Do the

                } else {

                     permission denied, boo! Disable the
                }
                return;
            }
        }
    }
    */

    private final String LAST_PHONE = "last_phone";
    private final String LAST_PASS = "last_ass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //mCurrStage = -1;//начало
        //mCurrStage = Stage_Phone;//
        String last_phone = Application.getData().getPhone();
        if (last_phone.length() > 0) {

            Helpers.SetAutoInputText(this, mPhoneText, 2000L, invertValidPhone(last_phone),
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


        mPhoneCodeText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                ///*
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    next_button(null);
                    return true;
                }
                /**/
                return false;
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        //may be
        //getIntent().putExtra(LAST_PHONE, mPhoneText.getText());
        //getIntent().putExtra(LAST_PASS, mPhoneCodeText.getText());
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

    Runnable phone_text_runnable = new Runnable() {
        @Override
        public void run() {
            if (!hasAddChangeListener) {
                hasAddChangeListener = true;
                mPhoneText.addTextChangedListener(new Helpers.TextWatcher_Phone(LoginActivity.this, mPhoneText));

                mPhoneCodeText.setFocusable(true);
                mPhoneCodeText.setFocusableInTouchMode(true);
            }
        }
    };

    private void setCurrStage(Boolean success) {
        switch (mCurrStage) {
            case Stage_Start: {
                if (success) {

                    mCurrStage = Stage_Phone;
/*
                    if (getIntent().hasExtra(LAST_PHONE)) {

                        mPhoneText.setText(getIntent().getStringExtra(LAST_PHONE));
                        mPhoneCodeText.setText(getIntent().getStringExtra(LAST_PASS));

                        getIntent().removeExtra(LAST_PHONE);

                        phone_text_runnable.run();

                    } else {
*/
                    mPhoneCodeText.setFocusable(false);
                    Helpers.SetAutoInputText(this, mPhoneText, 1000L, getText(R.string.phone_start_text), phone_text_runnable);
                    //                  }
                } else {
                    finish();
                }
            }
            break;
            case Stage_Phone:
                if (success) {

                    Application.getData().savePhone(validPhone(mPhoneText.getText().toString()), mPhoneCodeText.getText().toString());

                    new MaterialDialog.Builder(this)
                            .content("Привет " + mName)
                            .cancelable(false)
                            .positiveText("OK")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    Intent intent = new Intent(LoginActivity.this, OrdersActivity.class);
                                    intent.putExtra(OrdersActivity.FIRST_OPEN, true);
                                    Helpers.StartClean(LoginActivity.this, intent);

                                }
                            }).show();


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
        //if (mAuthTask != null) {
        if (mAuthTask) {
            return;
        }
        // Reset errors.
        mPhoneText.setError(null);
        mPhoneCodeText.setError(null);

        View focusView = null;

        switch (mCurrStage)

        {
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

        if (null != focusView)

        {
            focusView.requestFocus();
        } else

        {
            showProgress(true);

            mAuthTask = true;
            String phone = validPhone(mPhoneText.getText().toString());
            String pass = mPhoneCodeText.getText().toString();
            //tests
            //9123456789:password
            Helpers.SendLogIn(mLogInrequestCallback, phone, pass);
            //mAuthTask = new UserLoginTask();
            //mAuthTask.execute((Void) null);
        }
    }


    private String invertValidPhone(String str) {
        String ret = "+7("
                + str.substring(0, 3)
                + ")-"
                + str.substring(3, 6)
                + "-"
                + str.substring(6, 8)
                + "-"
                + str.substring(8, 10);//912)-345-67-89
        return ret;
    }

    private String validPhone(String s) {
        String ret = s.substring(2);
        ret = ret.replace(")", "");
        ret = ret.replace("(", "");
        ret = ret.replace("-", "");
        return ret;
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


    /* from simulate network accsess
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                return false;
            }
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

    }*/

    Helpers.RequestCallback mLogInrequestCallback = new Helpers.RequestCallback() {
        @Override
        public void callback(String[] result) {

            mName = result.length > 0 && result[0].length() > 0 ? result[0] : "";
            final boolean success = mName.length() > 0 ? true : false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCurrStage(success);
                    showProgress(false);
                }
            });
            mAuthTask = false;

        }
    };
}

