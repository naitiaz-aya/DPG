package com.lexus.depannage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    private TextView animatingText;
    Animation animateText, animateImage;
    private ImageView markerI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //handler to redirect on next page on selected time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 7000);

        init();
    }


    private void init() {
        animatingText = (TextView) findViewById(R.id.appName);
        markerI = (ImageView) findViewById(R.id.marker);
        animateText = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.up_animation);
        animatingText.setAnimation(animateText);
        animateImage = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.up3_animation);
        markerI.setAnimation(animateImage);

    }


}