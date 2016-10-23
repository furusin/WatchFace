package net.furusin.www.watchface;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by furusin on 2016/08/15.
 * アプリから選択した画像を切り抜くクラス。
 * 縦長の写真の場合、中心から横幅のサイズを正方形に切り出す。
 * 横長の写真の場合、中心から縦幅のサイズを正方形に切り出す。
 */
public class ImageScaler {

    int IMAGE_SIZE_MAX = 512;
    Bitmap bitmap;

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

            scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, scale, false);
            bitmap.recycle();
            bitmap = scaledBitmap;

        }
        return bitmap;
    }



    /*
     * アプリから選択した画像を切り抜くクラス。
     * 縦長の写真の場合、中心から横幅のサイズを正方形に切り出す。
     * 横長の写真の場合、中心から縦幅のサイズを正方形に切り出す。
     *
     *
     */

    public Bitmap crop() {
        Bitmap croppedBitmap = null;
        int size = 0;
        if(bitmap.getWidth() > bitmap.getHeight()) {
            size = bitmap.getHeight();
            croppedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - size / 2, 0, size, size, null, true);
        }else{
            size = bitmap.getWidth();
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - size / 2, size, size, null, true);

        }
        bitmap.recycle();

        bitmap = croppedBitmap;

        return bitmap;
    }

}
