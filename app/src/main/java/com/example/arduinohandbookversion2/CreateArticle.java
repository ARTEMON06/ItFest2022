package com.example.arduinohandbookversion2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arduinohandbookversion2.Adapters.AddImageAdapter;
import com.example.arduinohandbookversion2.User.SignInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CreateArticle extends AppCompatActivity {
    private static final String TAG = "CreateArticle";
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    EditText titleArticle, textArticle;
    Button addArticle, addImage;
    RecyclerView imagesArticle;
    AddImageAdapter adapter;
    ArrayList<Image> images = new ArrayList<>();
    FirebaseUser user;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_article);

        init();
        addArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArticle();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        adapter = new AddImageAdapter(getApplicationContext(), images);
        adapter.setItemClickListener(new AddImageAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                removeImage(position);
            }
        });
        imagesArticle.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    private void addArticle() {
        /*
        Добавить статью в базу данных (Потом всё прокомечку)
         */
        String title = titleArticle.getText().toString();
        String text = textArticle.getText().toString();

        if (!title.isEmpty() && !text.isEmpty()) {
            Article article = new Article(title, text, images);
            DatabaseReference databaseReference =
                    firebaseDatabase.getReference("data");

            databaseReference.child("articles").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String articleId = String.valueOf(snapshot.getChildrenCount());
                    databaseReference.child("articles").child(articleId).setValue(article);
                    databaseReference.child("authors").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String articles = snapshot.getValue(String.class);
                            if (articles != null) {
                                databaseReference.child("authors").child(user.getUid()).setValue(snapshot.getValue(String.class) + "," + articleId);
                            } else {
                                databaseReference.child("authors").child(user.getUid()).setValue(articleId);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(CreateArticle.this, "Статья добалена", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "addArticle.onDataChange: статья добавлена");
                    onBackPressed();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CreateArticle.this, "Ошибка при добавлении статьи", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "addArticle.onCancelled: статья не добавилась", error.toException().fillInStackTrace());
                }

            });


        }
    }

    private void removeImage(int position) {
        /*
        Удалить картинку из recyclerView при создании статьи
         */
        AlertDialog alertDialog = new AlertDialog.Builder(CreateArticle.this).create();
        alertDialog.setTitle("Удалить?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                images.remove(position);
                adapter.notifyItemRemoved(position);
                Log.d(TAG, "removeImage.onClick: картинка удалена из recyclerView");
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "removeImage.onClick: пользователь закрыл диалоговое окно");
            }
        });
        alertDialog.show();
    }


    private void addImage() {
        /*
        Добавить изображение в recyclerView
         */
        AlertDialog alertDialog = new AlertDialog.Builder(CreateArticle.this).create();
        alertDialog.setTitle("Добавить изображение");

        View view = getLayoutInflater().inflate(R.layout.add_image_dialog, null);
        EditText label_image = view.findViewById(R.id.label_image);
        EditText url_image = view.findViewById(R.id.url_image);

        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!label_image.getText().toString().isEmpty() && !url_image.getText().toString().isEmpty()) {
                    images.add(new Image(label_image.getText().toString(), url_image.getText().toString()));
                    adapter.notifyItemInserted(images.size() - 1);
                    Log.d(TAG, "addImage.onClick: картинка добавлена успешно в recyclerView");
                }
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "addImage.onClick: пользователь закрыл диалоговое окно");
            }
        });
        alertDialog.show();
    }


    private void init() {
        /*
        Инициализировать все элементы окна
         */
        titleArticle = findViewById(R.id.title_article);
        textArticle = findViewById(R.id.text_article);
        addArticle = findViewById(R.id.add_article);
        imagesArticle = findViewById(R.id.images_article); // recyclerView
        addImage = findViewById(R.id.add_image);
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(CreateArticle.this, SignInUser.class));
                    finish();
                }
            }
        };
    }

    private void set_theme(SharedPreferences pref) {

        boolean theme = pref.getBoolean("themeLight", true);
        if (theme) {
            setTheme(R.style.Theme_MyApplication);
        } else {
            setTheme(R.style.Theme_MyApplicationDark);
        }
    }

}