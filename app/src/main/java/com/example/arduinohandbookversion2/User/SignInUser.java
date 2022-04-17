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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohandbookversion2.MainActivity;
import com.example.arduinohandbookversion2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInUser extends AppCompatActivity {
    private static final String TAG = "SignInUser";
    ImageButton home;
    TextView title;
    ImageView footer_image;
    LinearLayout signInLinear;
    Button signInUser, signUpUser;
    EditText emailUser, passwordUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user);
        init();


        signInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        signUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInUser.this, SignUpUser.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void signInUser() {
        String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInUser.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInUser.onComplete: успешная авторизация");
                        Toast.makeText(SignInUser.this, "Успешная авторизация", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Log.e(TAG, "signInUser.onComplete: ошибка при авторизации ", task.getException());
                        Toast.makeText(SignInUser.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }

    private void back() {
        Animation scale = AnimationUtils.loadAnimation(SignInUser.this, R.anim.rotate);
        Animation alpha = AnimationUtils.loadAnimation(SignInUser.this, R.anim.alpha);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scale);
        set.addAnimation(alpha);
        home.startAnimation(set);
        title.startAnimation(alpha);

        Animation anim = AnimationUtils.loadAnimation(SignInUser.this, R.anim.down_slide_out);

        signInLinear.startAnimation(anim);
        footer_image.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signInLinear.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                footer_image.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void init() {
        emailUser = findViewById(R.id.email_user);
        passwordUser = findViewById(R.id.password_user);
        signInUser = findViewById(R.id.sign_in_user);
        signUpUser = findViewById(R.id.sign_up_user);
        firebaseAuth = FirebaseAuth.getInstance();
        footer_image = findViewById(R.id.footer_image);

        home = findViewById(R.id.home);
        title = findViewById(R.id.title);
        signInLinear = findViewById(R.id.signInLinear);

        Animation alpha = AnimationUtils.loadAnimation(SignInUser.this, R.anim.alpha_in);
        title.startAnimation(alpha);

        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(SignInUser.this, R.anim.rotate_in);
        set.addAnimation(rotate);
        set.addAnimation(alpha);
        home.startAnimation(set);

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
    public void onBackPressed() {
        back();
    }
}