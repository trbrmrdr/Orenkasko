package orenkasko.ru.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import orenkasko.ru.R;
import orenkasko.ru.ui.base.ImageLoader;

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

    public static void SetAutoInputText(final Context context, final EditText editText, long delay) {
        SetAutoInputText(context, editText, delay, null, null);
    }

    public static void SetAutoInputText(final Context context, final EditText editText, long delay, CharSequence text, final Runnable callback) {

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
        } else {
            delay_one_sumbol = 50;
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

    public static void StartClean(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static void StartClean(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static Bitmap GetImage(ImageView imageView) {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap;
        if (bitmapDrawable == null) {
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            imageView.buildDrawingCache(false);
        } else {
            bitmap = bitmapDrawable.getBitmap();
        }
        return bitmap;
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

    public static String ReadFromfile(Context context, String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line + "\n");
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    //_________________________________
    private static final String NAME_DB = "orenkasko_0.0";

    public static void Delete(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void SaveString(Context context, String name, String str) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, str);
        editor.commit();
    }

    public static String GetString(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        return sharedPref.getString(name, "");
    }

    public static void DelString(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editotr = sharedPref.edit();
        editotr.remove(name);
        editotr.commit();
    }

    public static boolean isEmpty(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        return !sharedPref.contains(name);
    }

    public static void SaveInt(Context context, String name, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void DelInt(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(name);
        editor.commit();
    }

    public static int GetInt(Context context, String name) {
        return GetInt(context, name, -1);
    }

    public static int GetInt(Context context, String name, int defValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        return sharedPref.getInt(name, defValue);
    }


    public static String[] GetStringArray(Context context, String name) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            return new String[0];
        }
        return set.toArray(new String[set.size()]);
    }

    public static void AddInArray(Context context, String name, int val) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            set = new HashSet<String>();
        }
        set.add(String.valueOf(val));

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(name, set);
        editor.commit();
    }

    public static void RmInArray(Context context, String name, int val) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            return;
        }
        set.remove(String.valueOf(val));

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(name, set);
        boolean ret = editor.commit();
        return;
    }


    public static void SaveImages(Context context, String name, ArrayList<ImageLoader> images) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, images.size());
        editor.commit();

        String path = GetFileCache(context).getAbsolutePath() + "/" + name + "/";
        File dirs = new File(path);
        boolean test = false;
        if (!dirs.exists())
            test = dirs.mkdirs();
        int i = -1;
        for (ImageLoader image : images) {
            ++i;
            Bitmap bitmap = GetImage(image.getImageView());
            if (null == bitmap) continue;
            SaveImageFile(bitmap, dirs.getAbsolutePath() + "/image_" + i);
        }
    }

    public static ArrayList<Bitmap> GetImages(Context context, String name) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        int count = sharedPref.getInt(name, -1);
        if (-1 == count) return bitmaps;

        String path = GetFileCache(context).getAbsolutePath() + "/" + name + "/";
        for (int i = 0; i < count; ++i) {
            Bitmap bitmap = BitmapFactory.decodeFile(path + "/image_" + i);
            bitmaps.add(bitmap);
        }
        return bitmaps;
    }

    public static void RmImages(Context context, String name) {
        File file = new File(GetFileCache(context).getAbsolutePath() + "/" + name);
        file.delete();
    }

    public static void SaveImage(Context context, Bitmap bitmap, String name) {
        String path = GetFileCache(context).getAbsolutePath() + "/" + name;
        SaveImageFile(bitmap, path);
    }

    public static Bitmap GetImage(Context context, String name) {
        String path = GetFileCache(context).getAbsolutePath() + "/" + name;
        return BitmapFactory.decodeFile(path);
    }

    //##############################################################################################

    public static Integer[] getIntArray(Context context, int array_strings_id) {
        return getIntArray(context.getResources().getStringArray(array_strings_id));
    }

    public static float[] getFloatArray(Context context, int array_strings_id) {
        return getFloatArray(context.getResources().getStringArray(array_strings_id));
    }

    public static float[] getFloatArray(String[] strings) {
        float[] ret = new float[strings.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Float.parseFloat(strings[i]);
        }
        return ret;
        //return Arrays.stream(strings).map(Float::valueOf).toArray(Float[]::new);
        //return Arrays.stream(strings).mapToDouble(Float::parseFloat).toArray();
    }

    public static Integer[] getIntArray(String[] strings) {
        Integer[] ret = new Integer[strings.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.parseInt(strings[i]);
        }
        return ret;
    }

    //##############################################################################################

    public static boolean SaveImageFile(Bitmap bitmap, String file_name) {
        try {
            FileOutputStream out = new FileOutputStream(file_name);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static File cacheDir;

    public static File GetFileCache(Context context) {
        if (null != cacheDir) return cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/files/");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

}
