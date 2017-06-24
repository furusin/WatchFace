package net.furusin.www.SelectedPhotoWatchFace;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.wearable.companion.WatchFaceCompanion;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import net.furusin.www.SelectedPhotoWatchFace.databinding.ActivityMainBinding;
import net.furusin.www.SelectedPhotoWatchFace.service.Advertise;
import net.furusin.www.SelectedPhotoWatchFace.viewInterface.MainViewInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>, MainViewInterface {
    private static final String PATH_WITH_FEATURE = "/watch_face_config/Digital";
    private static final int READ_REQUEST_CODE = 44;

    private ActivityMainBinding mBinding;
    private GoogleApiClient mGoogleApiClient;

    private String mPeerId;
    private Advertise advertize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setMainViewInterface(this);
        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        advertize = new Advertise(getApplicationContext(), mBinding.adView);
        advertize.initAdMob();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        advertize.reloadAdMob();
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    mBinding.imageView.setImageBitmap(cropImage(originalBitmap));

                    Bitmap scaledAndCroppedBitmap;
                    scaledAndCroppedBitmap = new ImageScaler(originalBitmap).scaleAndCropBitmap();

                    Asset asset = createAssetFromBitmap(scaledAndCroppedBitmap);

                    PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
                    dataMap.getDataMap().putAsset("profileImage", asset);
                    PutDataRequest request = dataMap.asPutDataRequest();
                    PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            Toast.makeText(MainActivity.this, getResources().getText(R.string.photo_set), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Bitmap cropImage(Bitmap bitmap) {
        Bitmap croppedBitmap = bitmap;
        int size = 0;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            size = bitmap.getHeight();
            croppedBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - size / 2, 0, size, size, null, true);
        } else {
            size = bitmap.getWidth();
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - size / 2, size, size, null, true);
        }
        return croppedBitmap;
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {

    }

    @Override
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

}
