package orenkasko.ru;

import android.support.v7.app.AppCompatDelegate;


import orenkasko.ru.Utils.AppResource;

public class Application extends android.app.Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "Game";
    private static Application mApplication;
    private static AppResource appResources;

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());

        mApplication = this;
        appResources = new AppResource(mApplication);
        Data.setContext(this);

    }

    public synchronized static AppResource getAppResources() {
        return appResources;
    }

    public static Application sharedApp() {
        return mApplication;
    }
}
