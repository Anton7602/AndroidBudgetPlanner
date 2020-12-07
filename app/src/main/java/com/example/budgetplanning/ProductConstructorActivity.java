package com.example.budgetplanning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ProductConstructorActivity extends AppCompatActivity {

    private SurfaceView barCodeCameraView;
    private TextView barCodeOutputTextView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_constructor);
        bindViews();
        initializeDetectorsAndSources();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializeDetectorsAndSources();
    }

    private void bindViews() {
        barCodeOutputTextView = (TextView) findViewById(R.id.barCodeOutputView);
        barCodeCameraView = (SurfaceView) findViewById(R.id.BarCodeSurfaceView);
    }

    private void initializeDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        barCodeCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ProductConstructorActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                    {
                        cameraSource.start(barCodeCameraView.getHolder());
                    }
                    else {
                        ActivityCompat.requestPermissions(ProductConstructorActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() !=0) {
                    barCodeOutputTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email!=null) {
                                barCodeOutputTextView.removeCallbacks(null);
                                barCodeOutputTextView.setText(barcodes.valueAt(0).email.address);
                            }
                            else {
                                barCodeOutputTextView.setText(barcodes.valueAt(0).displayValue);
                            }
                        }
                    });
                }
            }
        });
    }
}