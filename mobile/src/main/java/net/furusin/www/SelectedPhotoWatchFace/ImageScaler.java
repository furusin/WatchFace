package net.furusin.www.SelectedPhotoWatchFace;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageScaler {
    private static int IMAGE_SIZE_MAX = 512;
    Bitmap mBitmap;

    public ImageScaler(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap scaleAndCropBitmap() {
        Bitmap scaledAndCroppedBitmap = mBitmap;
        scaledAndCroppedBitmap = scaleBitmap(scaledAndCroppedBitmap);
        scaledAndCroppedBitmap = cropBitmap(scaledAndCroppedBitmap);

        return scaledAndCroppedBitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        Bitmap scaledBitmap = bitmap;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if (bitmap.getWidth() > IMAGE_SIZE_MAX && bitmap.getHeight() > IMAGE_SIZE_MAX) {

            float scaleWidth = ((float) IMAGE_SIZE_MAX) / bitmapWidth;
            float scaleHeight = ((float) IMAGE_SIZE_MAX) / bitmapHeight;
            float scaleFactor = Math.min(scaleWidth, scaleHeight);

            Matrix scale = new Matrix();
            scale.postScale(scaleFactor, scaleFactor);
            scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, scale, false);
        }
        bitmap.recycle();
        return scaledBitmap;
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        Bitmap croppedBitmap = bitmap;
        int size = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            size = bitmap.getHeight();
            croppedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - size / 2, 0, size, size, null, true);
        } else {
            size = bitmap.getWidth();
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - size / 2, size, size, null, true);

        }
        bitmap.recycle();
        return croppedBitmap;
    }
}
