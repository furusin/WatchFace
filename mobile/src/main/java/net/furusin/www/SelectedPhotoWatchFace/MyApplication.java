package net.furusin.www.SelectedPhotoWatchFace;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static java.lang.String.valueOf;

/**
 * Created by furusin on 2017/03/09.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
    }

    public void initAdMob(AdView adView){
        MobileAds.initialize(mContext, String.valueOf(R.string.banner_ad_app_id));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(valueOf(R.string.test_device_id)).build();
        adView.loadAd(adRequest);
    }
}
