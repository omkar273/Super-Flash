package com.lnstudio.SuperFlash;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;


public class ColorsActivity extends AppCompatActivity {

    SeekBar speedColor,colors;
    Integer speed = 1000;
    Integer blink=0;
    Integer seekVis=0;
    Handler handler;
    Float darkness;
    int aab,abb;
    boolean iSon;
    RelativeLayout colorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        darkness = layoutParams.screenBrightness;

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);


        aab = Color.parseColor("#ff0000");
        abb = Color.parseColor("#000cff");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        colors = (SeekBar) findViewById(R.id.speedcolor1);
        speedColor = (SeekBar) findViewById(R.id.speedcolor2);
        colorScreen = findViewById(R.id.relativelayout);
        colorFlash();
        colors.setProgress(2);
        colors.setMax(5);

        colors.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    aab = Color.parseColor("#ffffff");
                    abb = Color.parseColor("#ffffff");
                }
                if (i == 1) {
                    aab = Color.parseColor("#ffffff");
                    abb = Color.parseColor("#484848");
                }
                if (i == 2) {
                    aab = Color.parseColor("#ff0000");
                    abb = Color.parseColor("#000cff");
                }
                if (i == 3) {
                    aab = Color.parseColor("#ff00fc");
                    abb = Color.parseColor("#0cff00");
                }
                if (i == 4) {
                    aab = Color.parseColor("#ff0000");
                    abb = Color.parseColor("#fcff00");
                }
                if (i == 5) {
                    aab = Color.parseColor("#ff00fc");
                    abb = Color.parseColor("#ff0000");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedColor.setMax(5);
        speedColor.setProgress(1);
        speedColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i==0) {
                    speed = 1500;
                }
                    if (i == 1) {
                        speed = 1000;
                    }
                    if (i == 2) {
                        speed = 500;
                    }
                    if (i == 3) {
                        speed = 200;
                    }
                    if (i == 4) {
                        speed = 100;
                    }
                    if (i == 5) {
                        speed = 50;
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seekVis==0) {
                    colors.setVisibility(View.INVISIBLE);
                    speedColor.setVisibility(View.INVISIBLE);
                    seekVis=1;
                } else {
                    colors.setVisibility(View.VISIBLE);
                    speedColor.setVisibility(View.VISIBLE);
                    seekVis=0;
                }

            }
        });
    }
    public void colorFlash() {
        if (!iSon) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (blink == 0) {
                        colorScreen.setBackgroundColor(aab);
                        blink = 1;
                    } else {
                        colorScreen.setBackgroundColor(abb);
                        blink = 0;
                    }
                    colorFlash();
                }
            }, speed);
        }
    }

    @Override
    public void onBackPressed() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = darkness;
        getWindow().setAttributes(layout);
        super.onBackPressed();
    }
}
