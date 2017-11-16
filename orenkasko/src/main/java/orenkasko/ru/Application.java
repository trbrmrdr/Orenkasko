package orenkasko.ru;

import orenkasko.ru.Utils.AppResource;

public class Application extends android.app.Application {

    private static final String TAG = "Game";
    private static Application mApplication;
    private static AppResource appResources;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        appResources = new AppResource(mApplication);


    }

    public synchronized static AppResource getAppResources() {
        return appResources;
    }

    public static Application sharedApp() {
        return mApplication;
    }
}
