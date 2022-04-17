package com.example.arduinohandbookversion2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {
    ImageButton home;
    TextView title, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);

        Animation scale = AnimationUtils.loadAnimation(UserActivity.this, R.anim.scale_in);
        home.startAnimation(scale);

        Animation alpha = AnimationUtils.loadAnimation(UserActivity.this, R.anim.alpha_in);
        title.startAnimation(alpha);

        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(UserActivity.this, R.anim.rotate_in);
        set.addAnimation(rotate);
        set.addAnimation(alpha);
        home.startAnimation(set);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation scale = AnimationUtils.loadAnimation(UserActivity.this, R.anim.rotate);
                Animation alpha = AnimationUtils.loadAnimation(UserActivity.this, R.anim.alpha);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(scale);
                set.addAnimation(alpha);
                home.startAnimation(set);
                title.startAnimation(alpha);
                Animation anim = AnimationUtils.loadAnimation(UserActivity.this, R.anim.down_slide_out);

                text.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        text.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("data", "User");
                        startActivity(intent);
                        overridePendingTransition  (0, 0);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

    }

    void set_theme(SharedPreferences pref) {
        boolean theme = pref.getBoolean("themeLight", true);

        if (theme) {
            setTheme(R.style.Theme_MyApplication);
        } else {
            setTheme(R.style.Theme_MyApplicationDark);
        }
    }
}