package net.furusin.www.SelectedPhotoWatchFace.service;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.furusin.www.SelectedPhotoWatchFace.R;

import static java.lang.String.valueOf;

/**
 * Created by furusin on 2017/03/21.
 */

public class Advertise {
    private Context mContext;
    private AdView mAdView;

    public Advertise(Context context, AdView adView) {
        mContext = context;
        mAdView = adView;
    }

    public void initAdMob() {
        MobileAds.initialize(mContext, String.valueOf(R.string.banner_ad_app_id));
        loadAdMob();
    }

    public void reloadAdMob() {
        loadAdMob();
    }

    private void loadAdMob() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(valueOf(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);
    }
}
