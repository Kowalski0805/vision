package com.kowalski.banknote;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.Collections;

public class Recognition {
    private static Mat grayscaleImage;
    private static Mat gray;
    private static int absoluteFaceSize;
    private static MatOfFloat ranges = new MatOfFloat(0f, 256f);
    private static MatOfInt histSize = new MatOfInt(25);

    public static void initMats(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        gray = new Mat(height, width, CvType.CV_8UC4);
        absoluteFaceSize = (int) (height * 0.25);
    }

    public static boolean detect(Mat aInputFrame, CascadeClassifier cascadeClassifier, Mat hist,
                                 double scaleFactor, int minNeighbors, double minHistPercentage,
                                 String name) {
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
            Mat frameHist = rectToHist(rect);
            double res = compareHists(frameHist, hist);
            // If face passes histogram check, draw a rectangle around it
            if (res < minHistPercentage) {
                Core.rectangle(aInputFrame, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 3);
                return true;
            } else {
                Core.putText(aInputFrame, name, new Point(rect.x + 5, rect.y + 30), Core.FONT_HERSHEY_COMPLEX, 2, new Scalar(255, 0, 0, 255), 3);
                Core.rectangle(aInputFrame, rect.tl(), rect.br(), new Scalar(255, 0, 0, 255), 3);
            }
        }
        return false;
    }

    private static Mat rectToHist(Rect rect) {
        Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        Mat imageRoi = new Mat(grayscaleImage, rectCrop);
        return imgToHist(imageRoi);
    }

    private static double compareHists(Mat frameHist, Mat hist) {
        return Imgproc.compareHist(hist, frameHist, Imgproc.CV_COMP_BHATTACHARYYA);
    }

    static Mat bitmapToHist(Bitmap bitmap) {
        Mat img = new Mat();
        Utils.bitmapToMat(bitmap, img);
        return imgToHist(img);
    }

    private static Mat imgToHist(Mat img) {
        Mat hist = new Mat();

        img.convertTo(img, CvType.CV_8U);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2HSV);
        Imgproc.calcHist(Collections.singletonList(img), new MatOfInt(0),
                new Mat(), hist, histSize, ranges);

        return hist;
    }
}
