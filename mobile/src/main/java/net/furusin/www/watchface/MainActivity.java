package net.furusin.www.watchface;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.wearable.companion.WatchFaceCompanion;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult> {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private final int READ_REQUEST_CODE = 44;
    ImageView imageView;
    GoogleApiClient mGoogleApiClient;
    TextView textView;  //バッテリー状態表示テスト用


    private static final String PATH_WITH_FEATURE = "/watch_face_config/Digital";
    private String mPeerId;
    private MyBatteryManager myBatteryManager;
    //int batteryStatus = 0;
    String batteryStatusString = "";
    int batteryLevel = 0;


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReveiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        imageView = (ImageView) findViewById(R.id.imageView);
        Button button = (Button) findViewById(R.id.button);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        MobileAds.initialize(getApplicationContext(), valueOf(R.string.banner_ad_app_id));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(valueOf(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);


        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        myBatteryManager = new MyBatteryManager(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private BroadcastReceiver mBroadcastReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int batteryStatus = intent.getIntExtra("status", 0);
                batteryLevel = intent.getIntExtra("level", 0);

                switch (batteryStatus) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        batteryStatusString = "unknown";
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        batteryStatusString = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        batteryStatusString = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        batteryStatusString = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        batteryStatusString = "full";
                        break;
                }

                textView.setText(batteryStatusString);

                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/batteryInfo");
                DataMap dataMap = new DataMap();
                dataMap.putInt("BatteryLevel", batteryLevel);
                dataMap.putInt("BatteryStatus", batteryStatus);

                putDataMapRequest.getDataMap().putDataMap("/batteryInfo", dataMap);
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                com.google.android.gms.common.api.PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {

                        Log.d("test", "onResult2: " + dataItemResult);

                    }
                });


            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                ImageScaler imageScaler;
                Log.i("test", "Uri: " + uri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageScaler = new ImageScaler(bitmap);
                    bitmap = imageScaler.scale();
                    bitmap = imageScaler.crop();


                    Asset asset = createAssetFromBitmap(bitmap);

                    PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
                    dataMap.getDataMap().putAsset("profileImage", asset);
                    PutDataRequest request = dataMap.asPutDataRequest();
                    PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {

                            Log.d("test", "onResult2: " + dataItemResult);

                        }
                    });


                    imageView.setImageBitmap(bitmap);
                    Log.i("test", "bitmap set");
                    imageScaler = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }


    @Override
    public void onStart() {
        Log.d("test", "onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        Log.d("test", "onStop");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("test", "onConnected");
        Uri.Builder builder = new Uri.Builder();
        Log.d("test", "uri: " + builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build().toString());
        Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("test", "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("test", "onConnectionFailed: " + connectionResult);

    }

    @Override
    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
        Log.d("test", "onResult: " + dataItemResult);

    }
}
