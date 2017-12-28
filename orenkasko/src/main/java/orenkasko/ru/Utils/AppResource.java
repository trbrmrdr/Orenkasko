package orenkasko.ru.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import orenkasko.ru.ui.base.ImageLoader;

import static orenkasko.ru.Utils.Helpers.CompressBitmap;

public class AppResource {

    private static final String TAG = "GameResource";
    private Context mContext;

    private static final String RESOURCE_STRING = "string";
    private static final String RESOURCE_DRAWABLE = "drawable";
    private static final String RESOURCE_ANIM = "anim";
    private static final String RESOURCES_INTEGER = "integer";
    private static final String RESOURCES_BOOLEAN = "bool";
    private static final String RESOURCES_ARRAY = "array";

    // ###########################################################################

    public int getStringId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCE_STRING, key);
    }

    public int getIntegerId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCES_INTEGER, key);
    }

    public int getBooleanId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCES_BOOLEAN, key);
    }

    public int getArrayId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCES_ARRAY, key);
    }

    public int getAnimId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCE_ANIM, key);
    }

    public int getDrawableId(final String key) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        return getResourceId(RESOURCE_DRAWABLE, key);
    }

    public int getDrawableId(final String key, int defValue) {
        int ret;
        try {
            ret = getResourceId(RESOURCE_DRAWABLE, key);
        } catch (Exception e) {
            ret = defValue;
        }
        return ret;
    }

    public int getResourceId(final String type, final String key) throws NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        return getResourceId(mContext, type, key);
    }

    public static int getResourceId(final Context context, final String type, final String key) throws NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        String rc = context.getPackageName() + ".R$" + type;
        Field f = Class.forName(rc).getField(key);
        return f.getInt(null);
    }
    //#################################################################

    public boolean getBoolean(final String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(final String key, boolean defValue) {
        boolean ret;
        try {
            int resId = getBooleanId(key);
            ret = mContext.getResources().getBoolean(resId);
        } catch (Exception e) {
            ret = defValue;
        }
        return ret;
    }

    public int getInteger(final String key, Integer defValue) {
        int ret;
        try {
            int resId = getIntegerId(key);
            ret = mContext.getResources().getInteger(resId);
        } catch (Exception e) {
            ret = defValue;
        }
        return ret;
    }

    public String getString(final String key, String defValue) {
        String ret;
        try {
            int resId = getStringId(key);
            ret = mContext.getResources().getString(resId);
        } catch (Exception e) {
            //Log.e(TAG, "Can't get resource \"" + key + "\"\n", e);
            ret = defValue;
        }
        return ret;
    }

    // ###########################################################################

    private final String NAME_DB = "orenkasko_0.0";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPref_editor;

    public AppResource(final Context context) {
        mContext = context;
        sharedPref = mContext.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        sharedPref_editor = sharedPref.edit();
    }

    private SharedPreferences getSharedPreferences() {
        //return mContext.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
        return sharedPref;
    }

    private SharedPreferences.Editor getEditor() {
        //return getSharedPreferences().edit();
        return sharedPref_editor;
    }

    public void Delete() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.commit();
    }

    public void SaveString(String name, String str) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(name, str);
        editor.commit();
    }

    public String GetString(String name) {
        SharedPreferences sharedPref = getSharedPreferences();
        return sharedPref.getString(name, "");
    }

    public void DelString(String name) {
        SharedPreferences.Editor editotr = getEditor();
        editotr.remove(name);
        editotr.commit();
    }

    public boolean isEmpty(String name) {
        SharedPreferences sharedPref = getSharedPreferences();
        return !sharedPref.contains(name);
    }

    public void SaveInt(String name, int value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(name, value);
        editor.commit();
    }

    public void DelInt(String name) {
        SharedPreferences.Editor editor = getEditor();
        editor.remove(name);
        editor.commit();
    }

    public int GetInt(String name) {
        return GetInt(name, -1);
    }

    public int GetInt(String name, int defValue) {
        SharedPreferences sharedPref = getSharedPreferences();
        return sharedPref.getInt(name, defValue);
    }


    public String[] GetStringArray(String name) {
        SharedPreferences sharedPref = getSharedPreferences();
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            return new String[0];
        }
        return set.toArray(new String[set.size()]);
    }

    public void AddInArray(String name, int val) {
        SharedPreferences sharedPref = getSharedPreferences();
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            set = new HashSet<String>();
        }
        set.add(String.valueOf(val));

        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(name, set);
        editor.commit();
    }

    public void RmInArray(String name, int val) {
        SharedPreferences sharedPref = getSharedPreferences();
        Set<String> set = sharedPref.getStringSet(name, null);
        if (null == set) {
            return;
        }
        set.remove(String.valueOf(val));

        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(name, set);
        boolean ret = editor.commit();
        return;
    }


    public void ClearImages(String name, ArrayList<ImageLoader> images) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(name, images.size());
        editor.commit();

        String path = GetFileCache(mContext).getAbsolutePath() + "/" + name + "/";
        File dirs = new File(path);
        boolean test = false;
        if (!dirs.exists())
            return;

        if (dirs.delete())
            return;

        String path_s = dirs.getAbsolutePath() + "/image_";
        int i = -1;
        for (ImageLoader image : images) {
            ++i;
            new File(path_s + i).delete();
        }
    }

    public void SaveImages(String name, ArrayList<ImageLoader> images) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(name, images.size());
        editor.commit();

        String path = GetFileCache(mContext).getAbsolutePath() + "/" + name + "/";
        File dirs = new File(path);
        boolean test = false;
        if (!dirs.exists())
            test = dirs.mkdirs();
        String path_s = dirs.getAbsolutePath() + "/image_";
        int i = -1;
        for (ImageLoader image : images) {
            ++i;
            Bitmap bitmap = Helpers.GetImage(image.getImageView());
            if (null == bitmap) {
                new File(path_s + i).delete();
            } else {
                SaveImageFile(bitmap, path_s + i);
            }
        }
    }

    public ArrayList<Bitmap> GetImages(String name) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences();
        int count = sharedPref.getInt(name, -1);
        if (-1 == count) return bitmaps;

        String path = GetFileCache(mContext).getAbsolutePath() + "/" + name + "/";
        for (int i = 0; i < count; ++i) {
            Bitmap bitmap = BitmapFactory.decodeFile(path + "/image_" + i);
            bitmaps.add(bitmap);
        }
        return bitmaps;
    }

    public void RmImages(String name) {
        File file = new File(GetFileCache(mContext).getAbsolutePath() + "/" + name);
        file.delete();
    }

    public void SaveImage(Bitmap bitmap, String name) {
        String path = GetFileCache(mContext).getAbsolutePath() + "/" + name;
        SaveImageFile(bitmap, path);
    }

    public Bitmap GetImage(String name) {
        String path = GetFileCache(mContext).getAbsolutePath() + "/" + name;
        return BitmapFactory.decodeFile(path);
    }

    public boolean SaveImageFile(Bitmap bitmap, String file_name) {
        try {
            //todo dublicate in Helper ImgRequest doInBackground
            FileOutputStream out = new FileOutputStream(file_name);
            CompressBitmap(bitmap, out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private File cacheDir;

    public File GetFileCache(Context context) {
        if (null != cacheDir) return cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/files/");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir;
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
