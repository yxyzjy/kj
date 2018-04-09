package kj.dph.com.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;


public class MainApplication extends Application {
    private static MainApplication me;
    private static ArrayList<Activity> list = new ArrayList<Activity>();
    public static Context appContext;

    public static MainApplication getInstance() {
        return me;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        me = this;
        appContext = MainApplication.this.getApplicationContext();
        setAppContext(this);
    }


    public static void setAppContext(Context appContext) {
        MainApplication.appContext = appContext;
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public static void finishActivity(boolean isNeedLogin) {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

}
