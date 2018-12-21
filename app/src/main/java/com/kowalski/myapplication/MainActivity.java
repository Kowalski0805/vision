package com.kowalski.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.mainsound);
        mPlayer.start();
    }

    public void onPress(View view) {
        Intent i = new Intent(this, DetectActivity.class);
        startActivity(i);
    }
}
