package com.kowalski.banknote;

import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

public class Banknote {
    private String title;
    private CascadeClassifier cascade;
    private Mat hist;
    private double scale;
    private int minNeighbors;
    private double histPercentage;

    Banknote(String title, CascadeClassifier cascade, Mat hist, double scale, int minNeighbors, double histPercentage) {
        this.title = title;
        this.cascade = cascade;
        this.hist = hist;
        this.scale = scale;
        this.minNeighbors = minNeighbors;
        this.histPercentage = histPercentage;
    }

    public String getTitle() {
        return title;
    }

    public CascadeClassifier getCascade() {
        return cascade;
    }

    public Mat getHist() {
        return hist;
    }

    public double getScale() {
        return scale;
    }

    public int getMinNeighbors() {
        return minNeighbors;
    }

    public double getHistPercentage() {
        return histPercentage;
    }
}
