package com.example.arduinohandbookversion2.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohandbookversion2.MainActivity;
import com.example.arduinohandbookversion2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignUpUser extends AppCompatActivity {
    private static final String TAG = "SignUpUser";
    Button signUpUser;
    ImageView footer_image;
    EditText emailUser, passwordUser, loginUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user);
        init();
        signUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

    }

    private void signUpUser() {
        String email = emailUser.getText().toString();
        String password = passwordUser.getText().toString();
        String login = loginUser.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpUser.this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signUpUser.onComplete: успешная регистрация пользователя");
                                Toast.makeText(SignUpUser.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(login).build());
                                Log.d(TAG,
                                        "signUpUser.onComplete: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                startActivity(new Intent(getApplicationContext(), ProfileUser.class));
                                finish();
                            } else {
                                Log.e(TAG, "signUpUser.onComplete:", task.getException());
                                Toast.makeText(SignUpUser.this,
                                        String.format("Ошибка регистрации: %s",
                                                task.getException().getLocalizedMessage()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void init() {
        footer_image = findViewById(R.id.footer_image);
        firebaseAuth = FirebaseAuth.getInstance();
        emailUser = findViewById(R.id.email_user);
        passwordUser = findViewById(R.id.password_user);
        signUpUser = findViewById(R.id.sign_up_user);
        loginUser = findViewById(R.id.login_user);
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