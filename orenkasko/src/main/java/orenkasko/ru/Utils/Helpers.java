package orenkasko.ru.Utils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import orenkasko.ru.LoginActivity;
import orenkasko.ru.R;

/**
 * Created by trbrm on 13.11.2017.
 */

public class Helpers {
    private final static String TAG = "Helpers";

    private final static void Log(final String msg) {
        Log.e(TAG, msg);
    }


    public static void Toast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void setAutoInputText(final Context context, final EditText editText, long delay) {
        setAutoInputText(context, editText, delay, null, null);
    }

    public static void setAutoInputText(final Context context, final EditText editText, long delay, CharSequence text, final Runnable callback) {

        editText.setFocusable(false);
        editText.setText("");

        if (null == text || text.length() == 0) {
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);

                }
            }, delay);
            return;
        }

        final int lenght = text.length();
        long delay_one_sumbol = lenght * 100;
        long delay_start = delay - delay_one_sumbol;
        if (delay_start <= 0) {
            delay_start = delay;
            delay_one_sumbol = 0;
        }
        for (int i = 0; i < lenght; ++i) {

            final CharSequence new_text = text.subSequence(0, i + 1);
            final boolean hasLast = i == lenght - 1;
            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.setText(new_text);

                    if (hasLast) {

                        editText.setSelection(lenght);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        if (null != callback)
                            callback.run();

                    }
                }
            }, delay_start + delay_one_sumbol * i);
        }
    }


    public static class TextWatcher_Phone implements TextWatcher {

        private Context mContext;
        private EditText editText;

        public TextWatcher_Phone(final Context context, EditText phoneText) {
            mContext = context;
            editText = phoneText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        String mLastText = "";
        int mSelectPos = -1;
        boolean mHasAddToLast = false;
        boolean mTextErace = false;

        String erace(String str, int pos) {
            return str.substring(0, pos) + str.substring(pos + 1, str.length());
        }

        String insert(String str, int pos, char ch) {
            return str.substring(0, pos) + ch + str.substring(pos, str.length());
        }

        String getNumber(String str) {
            char _ch[] = {'(', ')', '-', '+'};
            String ret = str;
            for (char ch : _ch) {
                ret = ret.replace(ch + "", "");
            }
            return ret;
        }

        String setFromFormat(String phone) {
            String newText = phone.toString();
            int lenght = newText.length();

            if (lenght > 0 && newText.charAt(0) != '+') {
                newText = "+" + newText;
                lenght = newText.length();
            }

            if (lenght > 2 && newText.charAt(2) != '(') {
                newText = insert(newText, 2, '(');
                lenght = newText.length();
            }
            if (lenght > 6 && newText.charAt(6) != ')') {
                newText = insert(newText, 6, ')');
                lenght = newText.length();
            }
            //7 11 14
            int t_pos[] = {7, 11, 14};
            for (int _pos : t_pos) {
                if (lenght > _pos && newText.charAt(_pos) != '-') {
                    newText = insert(newText, _pos, '-');
                    lenght = newText.length();
                }
            }

            //__________

            mHasAddToLast = false;
            if (lenght == 0) {
                newText = "+";
                mHasAddToLast = true;
            } else if (lenght == 2) {
                newText += "(";
                mHasAddToLast = true;
            } else if (lenght == 6) {
                newText += ")";
                lenght += 1;
                mHasAddToLast = true;
            }
            if (lenght == 7 || lenght == 11 || lenght == 14) {
                newText += "-";
                mHasAddToLast = true;
            }

            return newText;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //if (s.length() <= 3 && mLastText.length() < s.length()) return;
            String newText = s.toString();
            if (newText.length() < 3) {
                newText = mContext.getString(R.string.phone_start_text);
            }

            if (mLastText.length() == 0 || !newText.equals(mLastText))
                mSelectPos = editText.getSelectionStart();

            String phoneString = getNumber(newText);
            newText = setFromFormat(phoneString);


            if (s.length() < mLastText.length()) {
                mTextErace = true;
                if (newText.equals(mLastText)) {
                    Log(mSelectPos + "");

                    if (mSelectPos != 0 || mSelectPos != 2) {
                        int posErace = phoneString.length() - 1;
                        if (mSelectPos == 6 || mSelectPos == 7) {
                            posErace = 3;
                            mSelectPos = 5;
                        } else if (mSelectPos == 11) {
                            posErace = 6;
                            mSelectPos = 10;
                        } else if (mSelectPos == 14) {
                            posErace = 8;
                            mSelectPos = 13;
                        }
                        if (phoneString.length() > 1) {
                            phoneString = erace(phoneString, posErace);
                        } else {
                            phoneString = "";
                        }

                        newText = setFromFormat(phoneString);
                    } else {
                        mTextErace = false;
                    }
                }
            }


            //+7(981)-699-18-77
            //Log(mLastText + " " + newText);
            if (null == mLastText || !newText.equals(mLastText)) {
                mLastText = newText;
                editText.setText(newText);
                if (!mHasAddToLast) {
                    if (mSelectPos == -1 || mSelectPos > newText.length())
                        mSelectPos = newText.length();
                    editText.setSelection(mSelectPos);
                }
            }
            if (mHasAddToLast) {
                if (mTextErace && newText.length() > mSelectPos) {
                    mSelectPos = mSelectPos;
                    mTextErace = false;
                } else if (newText.length() >= 3)
                    mSelectPos = newText.length();
                editText.setSelection(mSelectPos);
                mLastText = newText;
            }
            return;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
