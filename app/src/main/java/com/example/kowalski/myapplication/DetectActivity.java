package com.example.kowalski.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
//import android.view.View;
import android.view.WindowManager;

import org.opencv.android.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetectActivity extends Activity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int REQUEST_PERMISSION = 100;

    private int i = 0;

    private String[] files = {
            //face, back
            "1", "1b",
            "2", "2b",
            "5", "5b",
            "10", "10b",
            "20", "20b",
            "50", "50b",
            "100", "100b",
            "200", "200b",
            "500", "500b"
    };
    private String[] cascNames = {
            // face, back
            "one", "one_b",
            "two", "two_b",
            "five", "five_b",
            "ten", "ten_b",
            "twenty", "twenty_b",
            "fifty", "fifty_b",
            "oneh", "oneh_b",
            "twoh", "twoh_b",
            "fiveh", "fiveh_b"
    };
    private double[] scales = {
            //face, back
            2, 2,   // 1 UAH
            3, 3,   // 2 UAH
            2, 2,   // 5 UAH
            2, 2,   // 10 UAH
            2, 2,   // 20 UAH
            2, 2,   // 50 UAH
            2, 2,   // 100 UAH
            2, 2,   // 200 UAH
            2, 2    // 500 UAH
    };
    private int[] neighbors = {
            //face, back
            20, 20, // 1 UAH
            25, 25, // 2 UAH
            20, 20, // 5 UAH
            20, 20, // 10 UAH
            20, 20, // 20 UAH
            20, 20, // 50 UAH
            20, 20, // 100 UAH
            20, 20, // 200 UAH
            20, 20  // 500 UAH
    };
    private double[] histPercentages = {
            //face, back
            0.3, 0.3,   // 1 UAH
            0.3, 0.3,   // 2 UAH
            0.3, 0.3,   // 5 UAH
            0.3, 0.3,   // 10 UAH
            0.3, 0.3,   // 20 UAH
            0.3, 0.3,   // 50 UAH
            0.3, 0.3,   // 100 UAH
            0.3, 0.3,   // 200 UAH
            0.3, 0.3    // 500 UAH
    };

    private CameraBridgeViewBase openCvCameraView;
    private List<CascadeClassifier> cascades = new ArrayList<>();
    private List<Mat> hists = new ArrayList<>();
    private Mat grayscaleImage;
    private Mat gray;
    private int absoluteFaceSize;

    Mat img2 = new Mat();
    Mat hist2 = new Mat();
    MatOfFloat ranges = new MatOfFloat(0f, 256f);
    MatOfInt histSize = new MatOfInt(25);
//    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void initializeOpenCVDependencies() {

        try {
            for (String name : cascNames) {
                // Copy the resource into a temp file so OpenCV can load it
                String pkg = this.getPackageName();
                int id = getResources().getIdentifier(name, "raw", pkg);
                InputStream is = getResources().openRawResource(id);
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                File mCascadeFile = new File(cascadeDir, "cascade.xml");
                FileOutputStream os = new FileOutputStream(mCascadeFile);


                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();

                // Load the cascade classifier
                cascades.add(new CascadeClassifier(mCascadeFile.getAbsolutePath()));
            }
            for (String file : files) {
                createHist(file);
            }
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

        // And we are ready to go
        openCvCameraView.enableView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.btn_sound);
        mPlayer.start();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_detect);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }

        openCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        openCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        gray = new Mat(height, width, CvType.CV_8UC4);

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (int) (height * 0.25);
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat aInputFrame = inputFrame.rgba();

        i = ++i % files.length;
        detect(aInputFrame, cascades.get(i), hists.get(i), scales[i], neighbors[i], histPercentages[i], files[i]);

        return aInputFrame;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("INFO","Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d("INFO", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void createHist(String fileName) {
        try {
            Mat img1 = new Mat();
            Mat hist1 = new Mat();
            AssetManager assetManager = getAssets();
            InputStream istr = assetManager.open(fileName + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            Utils.bitmapToMat(bitmap, img1);
            img1.convertTo(img1, CvType.CV_8U);
            Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2HSV);
            Imgproc.calcHist(Collections.singletonList(img1), new MatOfInt(0),
                    new Mat(), hist1, histSize, ranges);
            hists.add(hist1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detect(Mat aInputFrame, CascadeClassifier cascadeClassifier, Mat hist, double scaleFactor,
                        int minNeighbors, double minHistPercentage, String name) {
        // Create a grayscale image
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(grayscaleImage, gray, Imgproc.COLOR_RGB2GRAY);

        MatOfRect faces = new MatOfRect();

        // Use the classifier to detect faces
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(gray, faces, scaleFactor, minNeighbors, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

        // If there are any faces found, check histograms
        Rect[] facesArray = faces.toArray();
        for (Rect rect : facesArray) {
            Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat imageRoi = new Mat(grayscaleImage, rectCrop);
            imageRoi.convertTo(imageRoi, CvType.CV_8U);
            Imgproc.cvtColor(imageRoi, img2, Imgproc.COLOR_RGB2HSV);
//            Imgproc.cvtColor(img2, img2, Imgproc.COLOR_HSV2RGB);
            Imgproc.calcHist(Collections.singletonList(img2), new MatOfInt(0), new Mat(), hist2, histSize, ranges);
            double res = Imgproc.compareHist(hist, hist2, Imgproc.CV_COMP_BHATTACHARYYA);
            // If face passes histogram check, draw a rectangle around it
            if (res < minHistPercentage) {
                Intent i = new Intent(this, ResultActivity.class);
                i.putExtra("banknote", name);
                startActivity(i);
                Core.rectangle(aInputFrame, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 3);
            } else {
                Core.putText(aInputFrame, name, new Point(rect.x + 5, rect.y + 30), Core.FONT_HERSHEY_COMPLEX, 2, new Scalar(255, 0, 0, 255), 3);
                Core.rectangle(aInputFrame, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255), 3);
            }
        }
    }

//    public void createNeg(View view) throws Exception {
//        Mat tmp = new Mat();
//        Imgproc.cvtColor(grayscaleImage, tmp, Imgproc.COLOR_RGB2RGBA, 4);
//        Bitmap bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(tmp, bmp);
//            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    "/Pictures/neg/";
//            File dir = new File(file_path);
//            if(!dir.exists())
//                dir.mkdirs();
//            File file = new File(dir, System.currentTimeMillis() + ".png");
//
//            FileOutputStream fOut = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
//            fOut.flush();
//            fOut.close();
//    }
}
