package com.acpagro.tareopalta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Splash extends Activity {

    ImageView logo_splash;
    Animation animations;
    LinearLayout ly_logos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ly_logos = findViewById(R.id.ly_logos);
        logo_splash = (ImageView)findViewById(R.id.logo_splash);
        animations = AnimationUtils.loadAnimation(getBaseContext(), R.anim.aparecer_mil);

        ly_logos.startAnimation(animations);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        animations.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(Splash.this, PantallaLogin.class);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
