package net.furusin.www.SelectedPhotoWatchFace;

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
    Bitmap mBitmap;

    public ImageScaler(Bitmap bitmap) {
        mBitmap = bitmap;
    }


    public Bitmap scale() {
        Bitmap scaledBitmap = null;
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();

        if (mBitmap.getWidth() > IMAGE_SIZE_MAX && mBitmap.getHeight() > IMAGE_SIZE_MAX) {

            float scaleWidth = ((float) IMAGE_SIZE_MAX) / bitmapWidth;
            float scaleHeight = ((float) IMAGE_SIZE_MAX) / bitmapHeight;
            float scaleFactor = Math.min(scaleWidth, scaleHeight);

            Matrix scale = new Matrix();
            scale.postScale(scaleFactor, scaleFactor);

            scaledBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bitmapWidth, bitmapHeight, scale, false);
            mBitmap.recycle();
            mBitmap = scaledBitmap;

        }
        return mBitmap;
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
        if(mBitmap.getWidth() > mBitmap.getHeight()) {
            size = mBitmap.getHeight();
            croppedBitmap = Bitmap.createBitmap(mBitmap, (mBitmap.getWidth() / 2) - size / 2, 0, size, size, null, true);
        }else{
            size = mBitmap.getWidth();
            croppedBitmap = Bitmap.createBitmap(mBitmap, 0, (mBitmap.getHeight() / 2) - size / 2, size, size, null, true);

        }
        mBitmap.recycle();

        return croppedBitmap;
    }

}
