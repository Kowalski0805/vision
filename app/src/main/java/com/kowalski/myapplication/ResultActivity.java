package com.kowalski.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("banknote");
        amount = amount.replaceAll("\\D+","");
        String audio = "sound" + amount;
        String pkg = this.getPackageName();
        int id = getResources().getIdentifier(audio, "raw", pkg);
        MediaPlayer mPlayer = MediaPlayer.create(this, id);
        mPlayer.start();
        TextView view = findViewById(R.id.result);
        view.setText(amount + " UAH");
    }


    public void onClick(View view) {
        finish();
    }
}
