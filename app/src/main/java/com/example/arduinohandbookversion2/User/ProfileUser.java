package com.example.arduinohandbookversion2.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohandbookversion2.MainActivity;
import com.example.arduinohandbookversion2.MyArticles;
import com.example.arduinohandbookversion2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileUser extends AppCompatActivity {
    private static final String TAG = "ProfileUser";
    LinearLayout layout;
    Button actionUserButton, myArticles;
    ImageButton home;
    ImageView footer_image;
    TextView title;
    Boolean isAuth = false;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        init();
        myArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyArticles.class));
            }
        });
        actionUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAuth) {
                    signOutUser();
                } else {
                    authUser();
                }
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }


    void signOutUser() {
        auth.signOut();
        title.setText("Пользователь");

    }

    private void authUser() {
        Log.d(TAG, "onAuthStateChanged: пользователь перенаправлен на авторизацию");
        Animation scale = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.rotate);
        Animation alpha = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.alpha);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        home.startAnimation(set);
        title.startAnimation(alpha);
        Animation anim = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.down_slide_out);

        layout.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), SignInUser.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    void init() {
        footer_image = findViewById(R.id.footer_image);
        layout = findViewById(R.id.linear_profile);
        home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        initAnimations();
        /* Инициализация всяких приколов*/
        auth = FirebaseAuth.getInstance();
        actionUserButton = findViewById(R.id.user_button_action);
        myArticles = findViewById(R.id.my_articles);
        myArticles.setVisibility(View.GONE);
        actionUserButton.setText("Войти в аккаунт");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                /*
                Срабатывает при изменении статуса авторизации пользователя
                 */
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    isAuth = true;
                    title.setText(user.getDisplayName());
                    myArticles.setVisibility(View.VISIBLE);
                    actionUserButton.setText("Выйти из аккаунта");
                    Log.d(TAG, "onAuthStateChanged:user:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: пользователь не авторизован");
                    isAuth = false;
                    actionUserButton.setText("Войти в аккаунт");
                    myArticles.setVisibility(View.GONE);
                }
            }
        };
    }

    private void initAnimations() {
        Animation alpha = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.alpha_in);
        title.startAnimation(alpha);

        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.rotate_in);
        set.addAnimation(rotate);
        set.addAnimation(alpha);
        home.startAnimation(set);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    void set_theme(SharedPreferences pref) {
        boolean theme = pref.getBoolean("themeLight", true);

        if (theme) {
            setTheme(R.style.Theme_MyApplication);
        } else {
            setTheme(R.style.Theme_MyApplicationDark);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
//        home.startAnimation(set);
    }

    private void back() {
        Animation scale = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.rotate);
        Animation alpha = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.alpha);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        home.startAnimation(set);
        title.startAnimation(alpha);
        Animation anim = AnimationUtils.loadAnimation(ProfileUser.this, R.anim.down_slide_out);

        footer_image.startAnimation(anim);
        layout.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.INVISIBLE);
                footer_image.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("data", "User");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }
}