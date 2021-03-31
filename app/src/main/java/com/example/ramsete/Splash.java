package com.example.ramsete;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    //display splashscreen time variable
    private static int SPLASH_SCREEN_TIME = 2500;

    //animation variables
    Animation inAnim, outAnim;

    //Image variable(s?)
    ImageView mockup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //set app to fullscreen and splash layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        //set animations
        inAnim = AnimationUtils.loadAnimation(this,R.anim.fadein_anim);
        outAnim = AnimationUtils.loadAnimation(this,R.anim.fadeout_anim);

        //set image(s?)
        mockup = findViewById(R.id.mockup_image);

        //assign animation to image
        mockup.setAnimation(inAnim);

        //start main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fadein_anim, R.anim.fadeout_anim);
            }
        },SPLASH_SCREEN_TIME);

    }


}
