package com.mvavrill.logicGamesSolver.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import com.google.common.util.concurrent.ListenableFuture;
import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.model.image.ImageProcessing;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button takePicture;
    private Bitmap lastImage;
    private int rotation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.camera_activity_previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        takePicture = findViewById(R.id.camera_take_picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                Log.d("Mat","clicked");
                boolean[][] grid = processImage();
                Log.d("Mat", "afterProcess");
                Intent gridActivityIntent = new Intent(CameraActivity.this, DrawImageActivity.class);
                //gridActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gridActivityIntent.putExtra("grid", grid);
                Log.d("Mat", "beforeStartActivity");
                startActivity(gridActivityIntent);
            }
        });
    }


    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(500, 500))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void analyze(@NonNull ImageProxy image) {
                lastImage = toBitmap(image.getImage());
                rotation = image.getImageInfo().getRotationDegrees();
                image.close();
            }
        });//width = 27/0.8/0.8*9
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageAnalysis, preview);
    }
    private Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        Bitmap decoded = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        float ratio = Math.min(decoded.getWidth(), decoded.getHeight())/500f;
        return Bitmap.createScaledBitmap(decoded, Math.round(decoded.getWidth()/ratio), Math.round(decoded.getHeight()/ratio),true);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean[][] processImage() {
        Log.d("Mat", "processImage");
        double[][] processedImage = null;
        switch(rotation) {
            case 0:
                processedImage = processImage0();
                break;
            case 90:
                processedImage = processImage90();
                break;
            case 180:
                processedImage = processImage180();
                break;
            case 270:
                processedImage = processImage270();
                break;
        }
        Log.d("Mat", "image processed");
        boolean[][] grid = new ImageProcessing(processedImage).gaussianFilter(5,1.5).edgeFilter().binarizeBool(0.9);
        //SudokuGrid grid = ImageProcessing.weightedGridFromScratch(processedImage, getApplicationContext());
        Log.d("Mat", "grid done");
        return grid;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private double[][] processImage0() {
        double[][] image = new double[lastImage.getWidth()][lastImage.getWidth()];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                image[i][j] = colorToGray(lastImage.getColor(j,i));
            }
        }
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private double[][] processImage90() {
        Log.d("90","start");
        double[][] image = new double[lastImage.getHeight()][lastImage.getHeight()];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                //Log.d("processImage90",i + " " + j + " " + (lastImage.getHeight()-j-1) + " " + lastImage.getHeight());
                image[i][j] = colorToGray(lastImage.getColor(i,lastImage.getHeight()-j-1));
            }
        }
        return image;
    }

    private double[][] processImage180() {
        throw new IllegalStateException("Not yet implemented");
    }

    private double[][] processImage270() {
        throw new IllegalStateException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double colorToGray(final Color color) {
        return 0.299*color.red() + 0.587*color.green() + 0.114*color.blue();
    }

    private void printImage(double[][] image) {
        Log.d("WW",image.length + " " + image[0].length);
        for (int i = 0; i < image.length; i++) {
            if (i%3 == 0) {
                String s = "";
                for (int j = 0; j < image[i].length; j++) {
                    if (j % 3 == 0) {
                        s += (image[i][j] > 0.7) ? "X" : "·";
                    }
                }
                Log.d("WW",i + " " + s);
            }
        }
    }
}