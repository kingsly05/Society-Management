package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class IntroActivity extends AppCompatActivity  implements View.OnClickListener{

    private ImageView  right_side, bullet1,bullet2,bullet3,bullet4;
    private LottieAnimationView paperless, backup;
    Float transitionY = 100f;
    private static int i = 0;
    private Intent intent = new Intent();
    private LinearLayout first, second, third, fourth;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    Float transitionX = 30f;
    private int walker = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        right_side = findViewById(R.id.rightside);
        bullet1 = findViewById(R.id.bottom1);
        bullet2 = findViewById(R.id.bottom2);
        bullet3 = findViewById(R.id.bottom3);
        bullet4 = findViewById(R.id.bottom4);
        paperless = findViewById(R.id.paperless);
        backup = findViewById(R.id.backup);
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);



        second.setAlpha(0f);
        third.setAlpha(0f);
        fourth.setAlpha(0f);
        first.setTranslationY(transitionX);
        second.setTranslationY(transitionX);
        third.setTranslationY(transitionX);
        fourth.setTranslationY(transitionX);
        right_side.setOnClickListener(this);
        if( i == 0 ) {
            intent.setClass(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            finish();
            i++;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.rightside:
                if(walker == 1)
                {
                    bullet1.setImageResource(R.drawable.stroke_circle);
                    bullet2.setImageResource(R.drawable.fill_circle);
                    bullet3.setImageResource(R.drawable.fill_circle);
                    bullet4.setImageResource(R.drawable.fill_circle);

                    second.setAlpha(0f);
                    third.setAlpha(0f);
                    fourth.setAlpha(0f);

                    first.setVisibility(View.VISIBLE);
                    second.setVisibility(View.GONE);

                    first.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(10000).start();
                    second.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(10000).start();
                    walker++;
                    break;
                }
                if(walker == 2)
                {
                    bullet1.setImageResource(R.drawable.fill_circle);
                    bullet2.setImageResource(R.drawable.stroke_circle);
                    bullet3.setImageResource(R.drawable.fill_circle);
                    bullet4.setImageResource(R.drawable.fill_circle);

                    first.setAlpha(0f);
                    third.setAlpha(0f);
                    fourth.setAlpha(0f);

                    first.setVisibility(View.GONE);
                    second.setVisibility(View.VISIBLE);

                    second.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(10000).start();
                    third.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(10000).start();
                    walker++;
                    break;
                }
                if(walker == 3)
                {
                    bullet1.setImageResource(R.drawable.fill_circle);
                    bullet2.setImageResource(R.drawable.fill_circle);
                    bullet3.setImageResource(R.drawable.stroke_circle);
                    bullet4.setImageResource(R.drawable.fill_circle);

                    first.setAlpha(0f);
                    second.setAlpha(0f);
                    fourth.setAlpha(0f);

                    first.setVisibility(View.GONE);
                    second.setVisibility(View.GONE);
                    third.setVisibility(View.VISIBLE);

                    third.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(10000).start();
                    fourth.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(10000).start();
                    walker++;
                    break;
                }
                if(walker == 4)
                {
                    bullet1.setImageResource(R.drawable.fill_circle);
                    bullet2.setImageResource(R.drawable.fill_circle);
                    bullet3.setImageResource(R.drawable.fill_circle);
                    bullet4.setImageResource(R.drawable.stroke_circle);

                    first.setAlpha(0f);
                    second.setAlpha(0f);
                    third.setAlpha(0f);

                    first.setVisibility(View.GONE);
                    second.setVisibility(View.GONE);
                    third.setVisibility(View.GONE);
                    fourth.setVisibility(View.VISIBLE);

                    fourth.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(10000).start();
//                    third.animate().translationX(transitionX).alpha(0f).setInterpolator(interpolator).setDuration(10000).start();
                    walker++;
                    break;
                }
                if(walker == 5)
                {
                    SharedPreferences file = getSharedPreferences("file", Activity.MODE_PRIVATE);
                    file.edit().putString("Introflag","1").apply();
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
//                break;
    }
    }
}