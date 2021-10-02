package com.nova.rawad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import render.animations.Attention;
import render.animations.Render;

import static java.lang.Thread.sleep;

public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar pg;
    int prog = 0;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        Intro();
        StartHomeScreen();
    }

    public void Intro() {

        final TextView txt = findViewById(R.id.txt);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Render render = new Render(SplashScreenActivity.this);
                render.setAnimation(Attention.Wobble(txt));
                render.start();
            }
        }, 1000);

    }

    public void StartHomeScreen() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();

            }
        }).start();
    }
}