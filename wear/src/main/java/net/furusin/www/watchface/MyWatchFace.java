/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.furusin.www.watchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import net.furusin.www.watchface.service.MyImage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't shown. On
 * devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    public static GoogleApiClient mGoogleApiClient;
    public static Bitmap receivedBitmap = null;

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;
    private static final int TIMEOUT_MS = 2000;
    private static final int SEND_IMAGE = 1;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }


    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;

        private static final float DESIGNED_SIZE = 512f;
        final int[] BACKGROUND_RES_ID = {
/*
                R.drawable.background0,
                R.drawable.background1,
                R.drawable.background2,
                R.drawable.background3,
                R.drawable.background4
*/
                R.drawable.blank
        };


        private AsyncTask<Asset, Void, Void> mGetImage;

        private DataEventBuffer mDataEventBuffer;


        private Asset profileAsset;
        //背景描画用
        Bitmap mBitmapPaint;
        Bitmap mBackgroundScaledBitmap;
//        private Bitmap mBackground;


        //針描画用
        Paint mDrawPaint;
        boolean mAmbient;
        Time mTime;


        final Handler mLoadImageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case SEND_IMAGE:
                        // Loads images.
                        mGetImage = new GetImage();
                        mGetImage.execute(profileAsset);
                        break;
                }
            }
        };

        //タイムゾーンが変更になった時用
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        int mTapCount;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);


            mGoogleApiClient = new GoogleApiClient
                    .Builder(MyWatchFace.this)
                    .addConnectionCallbacks(this)
                    .addApi(Wearable.API)
                    .build();
            mGoogleApiClient.connect();


            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(true)
                    .setAcceptsTapEvents(true)
                    .build());

            Resources resources = MyWatchFace.this.getResources();
            mDrawPaint = new Paint();
            mDrawPaint.setColor(resources.getColor(R.color.analog_hands));
            mDrawPaint.setStrokeWidth(resources.getDimension(R.dimen.analog_hand_stroke));
            mDrawPaint.setAntiAlias(true);
            mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

            mTime = new Time();


        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mDrawPaint.setAntiAlias(!inAmbientMode);
                }
                if (mAmbient) {
                    //setWakeLock();
                } else {
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Resources resources = MyWatchFace.this.getResources();
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    mTapCount++;
                    break;
                default:
                    break;
            }
            invalidate();
        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();
            int imgResId;
            Resources resources = MyWatchFace.this.getResources();
            Drawable backgroundDrawable = null;


            // Draw the background.
            if (isInAmbientMode()) {                      //Ambientモードの時
                Log.d("test", "AmbientMode");
                imgResId = R.drawable.black_background;
                backgroundDrawable = resources.getDrawable(imgResId);
                mBitmapPaint = ((BitmapDrawable) backgroundDrawable).getBitmap();

            } else {
                //Log.d("test", "MyApplication.Bitmap " + MyApplication.getBitmapString());
                //if (receivedBitmap == null) {           //スマホ側で何も選択していない時
                if(TextUtils.isEmpty(MyApplication.getBitmapString())){
                    Log.d("test", "receivedBitmap == null");
                    imgResId = BACKGROUND_RES_ID[mTime.minute % BACKGROUND_RES_ID.length];
                    backgroundDrawable = resources.getDrawable(imgResId);
                    mBitmapPaint = ((BitmapDrawable) backgroundDrawable).getBitmap();

                } else {                                      //スマホ側で画像を選択した時
                    // String bitmapString = MyApplication.getBitmapString();
                    Log.d("test", "receivedBitmap != null");
                    receivedBitmap = new MyImage().bitmapStringComveterToBitmap(MyApplication
                            .getBitmapString());
                    mBitmapPaint = receivedBitmap;
                }
            }

            // mBitmapPaint = ((BitmapDrawable) backgroundDrawable).getBitmap();
            mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBitmapPaint,
                    bounds.width(), bounds.height(), true /* filter */);
            canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);


            // Find the center. Ignore the window insets so that, on round watches with a
            // "chin", the watch face is centered on the entire screen, not just the usable
            // portion.
            float centerX = bounds.width() / 2f;
            float centerY = bounds.height() / 2f;

            float secRot = mTime.second / 30f * (float) Math.PI;
            int minutes = mTime.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((mTime.hour + (minutes / 60f)) / 6f) * (float) Math.PI;

            float secLength = centerX - 20;
            float minLength = centerX - 40;
            float hrLength = centerX - 80;

            if (!mAmbient) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mDrawPaint);
            }

            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mDrawPaint);

            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mDrawPaint);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d("test", "onConnected");
            Wearable.DataApi.addListener(mGoogleApiClient, this);


        }


        @Override
        public void onConnectionSuspended(int i) {
            Log.d("test", "onConnectionSuspended");

        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d("test", "onDataChanged");
            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED &&
                        event.getDataItem().getUri().getPath().equals("/image")) {
                    Log.d("test", "onDataChanged");
                    Log.d("test", "uri: " + event.getDataItem().getUri().toString());
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                }
                if (event.getDataItem().getUri().getPath().equals("/batteryInfo")) {

                }
            }

            mLoadImageHandler.sendEmptyMessage(SEND_IMAGE);

        }


        private class GetImage extends AsyncTask<Asset, Void, Void> {
            Asset profileAsset;
            DataEvent event;

            @Override
            protected void onPreExecute() {
                //do something.

            }


            @Override
            protected Void doInBackground(Asset... params) {
                Log.d("test", "doInbackground");
                Log.d("test", "params.length = " + Integer.toString(params.length));

                for (int i = 0; i < params.length; i++) {
                    Log.d("test", "i : " + Integer.toString(i));
                    if (params[i] == null) {
                        Log.d("test", "params[" + Integer.toString(i) + "] = null");
                    } else {
                        Log.d("test", "params = " + params[i].toString());
                    }
                }


                profileAsset = params[0];


                try {

                    Log.d("test", "loadBitmapFromAsset");
                    if (profileAsset == null) {
                        Log.d("test", "asset is null");

                        throw new IllegalArgumentException("Asset must be non-null");
                    } else {
                        Log.d("test", "asset is not null");
                    }

                    GoogleApiClient googleApiClient = new GoogleApiClient
                            .Builder(MyWatchFace.this)
                            .addApi(Wearable.API)
                            .build();
                    googleApiClient.connect();


                    ConnectionResult result =
                            googleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    if (!result.isSuccess()) {
                        Log.d("test", "hogehoge");
                        return null;
                    }
                    Log.d("test", "hoge");
                    // convert asset into a file descriptor and block until it's ready
                    InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                            googleApiClient, profileAsset).await().getInputStream();
                    //mGoogleApiClient.disconnect();

                    if (assetInputStream == null) {
                        Log.w("test", "Requested an unknown Asset.");
                        return null;
                    }
                    // decode the stream into a bitmap
                    Bitmap imageSource = BitmapFactory.decodeStream(assetInputStream);
                    receivedBitmap = imageSource;

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    imageSource.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    String bitmapString = Base64.encodeToString(byteArrayOutputStream.toByteArray()
                            , Base64.DEFAULT);
                    MyApplication.setBitmapString(bitmapString);
                    //Log.d("test", "bitmapString = " + MyApplication.getBitmapString());


                    // Do something with the bitmap
                    //    }
                } catch (RuntimeException e) {
                    Log.d("test", "RuntimeException" + e.toString());
                }
                //   }


                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //    for (DataEvent event : params) {
            }

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.w("test", "onConnectionFailed");

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();

            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }


}
