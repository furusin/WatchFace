package net.furusin.www.watchface;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.furusin.www.watchface.Util.PreferenceUtil;

/**
 * Created by furusin on 2017/03/09.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private static String mBitmapString;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public static String getBitmapString() {
        Log.d(TAG, "MyApplication.getBitmapString");
        Log.d(TAG, "mBitmapString = " + mBitmapString);
        return (TextUtils.isEmpty(mBitmapString)) ? PreferenceUtil.getBitmapString(mContext) : "";
    }

    public static void setBitmapString(final String bitmapString) {
        mBitmapString = bitmapString;
        PreferenceUtil.setBitmapString(mContext, mBitmapString);
    }

}
