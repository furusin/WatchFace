package net.furusin.www.watchface.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by furusin on 2017/03/14.
 */

public class MyImage {
    private Bitmap bitmap;

    public Bitmap bitmapStringComveterToBitmap(String bitmapString){
        byte[] base64Byte = Base64.decode(bitmapString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(base64Byte, 0, base64Byte.length).copy(Bitmap.Config
                .ARGB_8888, true);
    }
}
