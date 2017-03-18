package net.furusin.www.SelectedPhotoWatchFace;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by furusin on 2016/09/18.
 */
public class MyBatteryManager {
    Context mContext;
    IntentFilter intentFilter;
    Intent batteryStatus;

    public MyBatteryManager(Context context){
        mContext = context;
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = mContext.registerReceiver(null, intentFilter);
    }

    /**
     *
     * @return バッテリー残量:int
     */
    private int getBatteryLevel(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }


    /**
     *
     * @return バッテリー温度:int
     */

    private int getBatteryTemperture(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

    }

    /**
     *
     * @return バッテリーステータス：充電中か否か
     * int	BATTERY_STATUS_CHARGING:2
     * int	BATTERY_STATUS_DISCHARGING:3
     * int	BATTERY_STATUS_FULL:5
     * int	BATTERY_STATUS_NOT_CHARGING:4
     * int	BATTERY_STATUS_UNKNOWN:1
     */

    private int getBatteryStatus(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, 1);
    }



}
