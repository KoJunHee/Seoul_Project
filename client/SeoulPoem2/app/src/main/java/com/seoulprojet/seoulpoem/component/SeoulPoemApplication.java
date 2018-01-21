package com.seoulprojet.seoulpoem.component;

import android.app.Application;
import android.content.Context;

/**
 * Created by KoJunHee on 2017-05-30.
 */

public class SeoulPoemApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getSeoulPeomApplication() {
        return mContext;
    }

}

