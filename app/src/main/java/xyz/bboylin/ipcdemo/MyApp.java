package xyz.bboylin.ipcdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by lin on 2017/6/9.
 * Email: bboylin24@gmail.com
 * Blog: bboylin.github.io
 */

public class MyApp extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
