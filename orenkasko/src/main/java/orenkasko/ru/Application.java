package orenkasko.ru;

import android.support.v7.app.AppCompatDelegate;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import orenkasko.ru.Utils.AppResource;

public class Application extends android.app.Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "Game";
    private static Application mApplication;
    private static Data appResources;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mApplication = this;
        appResources = new Data(mApplication);

    }

    public synchronized static AppResource getAppResources() {
        return appResources;
    }

    public synchronized static Data getData() {
        return appResources;
    }

    public static Application sharedApp() {
        return mApplication;
    }
}
