package com.kowalski.myapplication.service;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.kowalski.banknote.ResourceServiceInterface;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class IOResourceService implements ResourceServiceInterface {
    private Activity activity;


    public IOResourceService(Activity activity) {
        this.activity = activity;
    }

    @Override
    public CascadeClassifier getCascade(String name) {
        try {
            String pkg = activity.getPackageName();
            int id = activity.getResources().getIdentifier(name, "raw", pkg);
            InputStream is = activity.getResources().openRawResource(id);
            File cascadeDir = activity.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "cascade.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            return new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("IOResourceService", "Error loading cascade", e);
            return null;
        }
    }

    @Override
    public Bitmap getHistBitmap(String name) {
        try {
            AssetManager assetManager = activity.getAssets();
            InputStream istr = assetManager.open(name + ".jpg");
            return BitmapFactory.decodeStream(istr);
        } catch (Exception e) {
            Log.e("IOResourceService", "Error loading file", e);
            return null;
        }
    }
}
