package com.mvavrill.logicGamesSolver.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;
import com.mvavrill.logicGamesSolver.model.ProbabilisticDigit;

public class SudokuCameraActivity extends CameraActivity implements CvCameraViewListener2 {
    private final TextRecognizer textRecognizer = TextRecognition.getClient();
    private static final String TAG = "OpenCV";

    private final ProbabilisticDigit[][] probabilisticGrid = new ProbabilisticDigit[9][9];
    private int nbGridsTested = 0;
    private static final int nbDetectionToGood = 10;

    private ProgressBar progressBar;

    private CameraBridgeViewBase mOpenCvCameraView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sudoku_camera);
        mOpenCvCameraView = findViewById(R.id.sudoku_camera_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        progressBar = findViewById(R.id.sudoku_camera_progress_bar);
        progressBar.setMax(nbDetectionToGood);
    }

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private boolean isComputing = false;

    public SudokuCameraActivity() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                probabilisticGrid[i][j] = new ProbabilisticDigit();
            }
        }
    }

    private Mat withContour(final CvCameraViewFrame inputFrame) {
        if (nbGridsTested == nbDetectionToGood) {
            int[][] grid = new int[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int v = probabilisticGrid[i][j].extractDigit(nbDetectionToGood);
                    if (v != -1)
                        grid[i][j] = v;
                }
            }
            Intent drawImageIntent = new Intent(com.mvavrill.logicGamesSolver.controller.SudokuCameraActivity.this, SudokuActivity.class);
            drawImageIntent.putExtra("grid",grid);
            startActivity(drawImageIntent);
            finish();
        }
        Mat gray = inputFrame.gray();
        MatOfPoint contour = getGridContour(gray);
        if (!isComputing && contour != null) {
            Mat res = applyPerspective(gray,new MatOfPoint2f(contour.toArray()));
            Log.d("Mat","got res");
            Bitmap bmp = convertToBitmap(res);
            Bitmap resizedBmp = Bitmap.createBitmap(bmp, 0, 0, 360, 360);
            Bitmap rotatedBmp = rotateBitmap(resizedBmp, 90f);
            /*Log.d("Mat","converted");
            Intent drawImageIntent = new Intent(com.mvavrill.ctvest.ImageManipulationsActivity.this, DrawImage.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            drawImageIntent.putExtra("image",byteArray);
            startActivity(drawImageIntent);*/
            extractDigits(rotatedBmp);
            return gray;
        }
        return gray;
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        original.recycle();
        return rotatedBitmap;
    }

    private MatOfPoint getGridContour(final Mat grayImage) {
        int goalArea = grayImage.cols()*grayImage.cols()/16;
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(grayImage, blurred, new Size(7,7), 3);
        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(blurred, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY,11,2);
        Core.bitwise_not(thresh, thresh);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double bestArea = 0;
        MatOfPoint bestContour = null;
        for (int currentId = 0; currentId < contours.size(); currentId++) {
            double area = Imgproc.contourArea(contours.get(currentId));
            if (area > bestArea) {
                MatOfPoint2f c2f = new MatOfPoint2f(contours.get(currentId).toArray());
                double perimeter = Imgproc.arcLength(c2f, true);
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                Imgproc.approxPolyDP(c2f, approxCurve, 0.04*perimeter, true);
                if (approxCurve.size(0) == 4) {
                    bestArea = area;
                    bestContour = new MatOfPoint(approxCurve.toArray());
                }
            }
        }
        if (bestArea > goalArea && isSquareContour(bestContour)) {
            return bestContour;
        }
        return null;
    }

    private void extractDigits(final Bitmap image) {
        isComputing = true;
        Task<Text> task = textRecognizer.process(InputImage.fromBitmap(image,0));
        task.addOnSuccessListener(text -> {
            //int[][] grid = new int[9][9];
            processText(text);
            isComputing = false;
            progressBar.incrementProgressBy(1);
        })
                .addOnFailureListener(exception -> {isComputing = false;});
    }

    private void processText(final Text text) {
        int[][] grid = new int[9][9];
        List<Text.TextBlock> blocks = text.getTextBlocks();
        for (Text.TextBlock block : blocks) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements()) {
                    Pair<Integer,Integer> p = positionFromCorners(element.getCornerPoints());
                    int i = p.second;
                    int j = p.first;
                    int v = parseText(element.getText());
                    grid[i][j] = v;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    probabilisticGrid[i][j].addDigit(grid[i][j]);
                }
            }
        }
        nbGridsTested++;
        Log.d("Mat",""+nbGridsTested);
    }

    private Pair<Integer,Integer> positionFromCorners(final android.graphics.Point[] corners) {
        int x = 0, y = 0;
        for (android.graphics.Point p : corners) {
            x += p.x;
            y += p.y;
        }
        return new Pair<Integer,Integer>(x/160, y/160);
    }

    private int parseText(final String text) {
        String head = text.substring(0,1);
        try {
            return Integer.parseInt(head);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private void printGrid(final int[][] grid) {
        Log.d("Mat","printGrid");
        for (int[] line : grid) {
            String s = "";
            for (int v : line) {
                if (v == 0) {
                    s = s+"·";
                }
                else
                    s = s+v;
            }
            Log.d("Mat",s);
        }
    }

    private boolean isSquareContour(final MatOfPoint contour) {
        List<Point> points = contour.toList();
        List<Double> distances = new ArrayList<Double>();
        double perimeter = 0;
        for (int i = 0; i < 4; i++) {
            double l = norm(getVector(points.get(i), points.get((i+1)%4)));
            distances.add(l);
            perimeter += l;
        }
        for (double distance : distances) {
            if (perimeter/distance > 5) {
                return false;
            }
        }
        Log.d("Mat",""+distances + " " + perimeter);
        return true;
    }

    private Mat applyPerspective(final Mat image, final MatOfPoint2f contour) {
        Moments moment = Imgproc.moments(contour);
        int x = (int) (moment.get_m10() / moment.get_m00());
        int y = (int) (moment.get_m01() / moment.get_m00());
        Point[] sortedPoints = new Point[4];
        double[] data;
        for(int i=0; i<contour.rows(); i++){
            data = contour.get(i, 0);
            double datax = data[0];
            double datay = data[1];
            if(datax < x && datay < y){
                sortedPoints[0]=new Point(datax,datay);
            }else if(datax > x && datay < y){
                sortedPoints[1]=new Point(datax,datay);
            }else if (datax < x && datay > y){
                sortedPoints[2]=new Point(datax,datay);
            }else if (datax > x && datay > y){
                sortedPoints[3]=new Point(datax,datay);
            }
        }
        MatOfPoint2f src = new MatOfPoint2f(sortedPoints[0],sortedPoints[1],sortedPoints[2],sortedPoints[3]);
        int maxPT = 360;
        MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0),new Point(maxPT,0),new Point(0,maxPT),new Point(maxPT,maxPT));
        Mat warpMat = Imgproc.getPerspectiveTransform(src,dst);
        //This is you new image as Mat
        Mat destImage = new Mat();
        Imgproc.warpPerspective(image, destImage, warpMat, image.size());
        return destImage;
    }

    private Bitmap convertToBitmap(final Mat image) {
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(image, bmp);
        }
        catch (CvException e){Log.d("Mat",e.getMessage());}
        return bmp;
    }

    private Point getVector(final Point a, final Point b) {
        return new Point(b.x-a.x, b.y-a.y);
    }

    private double norm(final Point v) {
        return Math.sqrt(v.x*v.x + v.y*v.y);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {}

    public void onCameraViewStopped() {}

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return withContour(inputFrame);
    }

}
