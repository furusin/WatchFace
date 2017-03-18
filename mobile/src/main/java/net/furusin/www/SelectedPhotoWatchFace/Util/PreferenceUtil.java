package net.furusin.www.SelectedPhotoWatchFace.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by furusin on 2017/03/09.
 */

public class PreferenceUtil {
    private static final String SP_KEY = "shared_preferences";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            PreferenceKey.Bitmap,
    })
    private @interface PreferenceKey {
        String Bitmap = "Bitmap";
    }

    private static SharedPreferences getSharedPreferences(final Context context) {
        return context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
    }

    private static void putString(final Context context, @PreferenceKey final String key, final String value) {
        final SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getBitmapString(final Context context) {
        Log.d("test", "PreferenceUtil.getBitmapString");
        return getSharedPreferences(context).getString(PreferenceKey.Bitmap, "");
    }

    public static void setBitmapString(final Context context, final String bitmapString) {
        putString(context, PreferenceKey.Bitmap, bitmapString);
    }

}