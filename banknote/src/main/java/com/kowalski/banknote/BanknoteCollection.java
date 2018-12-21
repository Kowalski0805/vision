package com.kowalski.banknote;

import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

public class BanknoteCollection {
    private Banknote[] banknotes;

    public BanknoteCollection(ResourceServiceInterface resourceService) {
        banknotes = new Banknote[Config.getLength()];
        for (int i = 0; i < Config.getLength(); i++) {
            String title = Config.getTitle(i);
            String cascName = Config.getCascName(i);
            String histName = Config.getFile(i);
            double scale = Config.getScale(i);
            int minNeighbors = Config.getNeighbors(i);
            double histPercentage = Config.getHistPercentage(i);

            CascadeClassifier cascade = resourceService.getCascade(cascName);
            Mat hist = Recognition.bitmapToHist(resourceService.getHistBitmap(histName));

            banknotes[i] = new Banknote(title, cascade, hist, scale, minNeighbors, histPercentage);
        }
    }

    public Banknote getBanknote(int i) {
        return banknotes[i];
    }

    public int getLength() {
        return banknotes.length;
    }
}
