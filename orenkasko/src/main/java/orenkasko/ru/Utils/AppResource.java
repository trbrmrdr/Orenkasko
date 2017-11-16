package orenkasko.ru.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;

public class AppResource {

    private static final String TAG = "GameResource";
    private Context mContext;

    private static final String RESOURCE_STRING = "string";
    private static final String RESOURCE_DRAWABLE = "drawable";
    private static final String RESOURCE_ANIM = "anim";
    private static final String RESOURCES_INTEGER = "integer";
    private static final String RESOURCES_BOOLEAN = "bool";
    private static final String RESOURCES_ARRAY = "array";

    private static final String VERSION_TYPE = "_.VERSION_TYPE";
    // ###########################################################################

    public AppResource(final Context context) {
        mContext = context;
    }

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

    //#################################################################

    public String getSetting(final String key, String defValue) {
        return mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE).getString(key, defValue);
    }

    public int getSetting(final String key, int defValue) {
        return mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE).getInt(key, defValue);
    }

    public boolean getSetting(final String key, boolean defValue) {
        return mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public void setSetting(final String key, final String value) {
        SharedPreferences prefs = mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (value != null && !value.isEmpty())
            editor.putString(key, value);
        else
            editor.remove(key);
        editor.commit();
    }

    public void setSetting(final String key, final int value) {
        SharedPreferences prefs = mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);

        editor.commit();
    }

    public void setSetting(final String key, final boolean value) {
        SharedPreferences prefs = mContext.getSharedPreferences(VERSION_TYPE, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


}
