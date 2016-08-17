package net.furusin.www.watchface;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by furusin on 2016/08/15.
 */
public class ImageScaler {

    int IMAGE_SIZE_MAX = 512;
    Bitmap bitmap;
    int scaleWidth;
    int scaleHeight;

    public ImageScaler(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public Bitmap scale() {
        Bitmap scaledBitmap = null;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (bitmap.getWidth() > IMAGE_SIZE_MAX && bitmap.getHeight() > IMAGE_SIZE_MAX) {

            float scaleWidth = ((float) IMAGE_SIZE_MAX) / bitmapWidth;
            float scaleHeight = ((float) IMAGE_SIZE_MAX) / bitmapHeight;
            float scaleFactor = Math.min(scaleWidth, scaleHeight);

            Matrix scale = new Matrix();
            scale.postScale(scaleFactor, scaleFactor);

//          scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, scale, false);
            scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, scale, false);
            bitmap.recycle();
            bitmap = scaledBitmap;

        }
        return bitmap;
    }

    public Bitmap crop() {
        Bitmap croppedBitmap = null;
        if(bitmap.getWidth() > bitmap.getHeight()) {
            croppedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - (bitmap.getWidth() / 4), 0, (bitmap.getWidth() / 2) + (bitmap.getWidth() / 4), bitmap.getHeight(), null, true);
        }else{
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - (bitmap.getHeight() / 4), bitmap.getWidth(), (bitmap.getHeight() / 2) + (bitmap.getHeight() / 4), null, true);

        }
        bitmap.recycle();

        bitmap = croppedBitmap;

        return bitmap;
    }

}