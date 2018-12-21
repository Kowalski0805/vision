package com.kowalski.banknote;

import android.graphics.Bitmap;

import org.opencv.objdetect.CascadeClassifier;

public interface ResourceServiceInterface {
    CascadeClassifier getCascade(String name);
    Bitmap getHistBitmap(String name);
}
