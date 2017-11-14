package orenkasko.ru.Utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

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


    public static class TextWatcher_Phone implements TextWatcher {

        private Context mContext;
        private EditText mPhoneText;

        public TextWatcher_Phone(final Context context, EditText phoneText) {
            mContext = context;
            mPhoneText = phoneText;
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
            if (s.length() <= 0) return;
            String newText = s.toString();
            if (newText.length() < 3) {
                newText = mContext.getString(R.string.phone_start_text);
            }

            if (mLastText.length() == 0 || !newText.equals(mLastText))
                mSelectPos = mPhoneText.getSelectionStart();

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
            Log(mLastText + " " + newText);
            if (null == mLastText || !newText.equals(mLastText)) {
                mLastText = newText;
                mPhoneText.setText(newText);
                if (!mHasAddToLast) {
                    if (mSelectPos == -1 || mSelectPos > newText.length())
                        mSelectPos = newText.length();
                    mPhoneText.setSelection(mSelectPos);
                }
            }
            if (mHasAddToLast) {
                if (mTextErace && newText.length() > mSelectPos) {
                    mSelectPos = mSelectPos;
                    mTextErace = false;
                } else if (newText.length() >= 3)
                    mSelectPos = newText.length();
                mPhoneText.setSelection(mSelectPos);
                mLastText = newText;
            }
            return;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
