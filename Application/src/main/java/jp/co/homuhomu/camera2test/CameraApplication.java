package jp.co.homuhomu.camera2test;

import android.app.Application;
import android.content.Context;

public class CameraApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
