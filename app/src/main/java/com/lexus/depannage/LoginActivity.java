package com.lexus.depannage;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LoginActivity extends AppCompatActivity {

    private Button phoneButton;
    Animation phoneAnimate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneButton = (Button) findViewById(R.id.phone);

        phoneAnimate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.up2_animation);
        phoneButton.setAnimation(phoneAnimate);
    }

    public void phoneLoginClick(View view) {
        Intent i = new Intent(LoginActivity.this, PhoneLoginActivity.class);

        startActivity(i);

    }
}