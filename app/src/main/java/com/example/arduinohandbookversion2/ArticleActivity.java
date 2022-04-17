package com.example.arduinohandbookversion2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ArticleActivity extends AppCompatActivity {
    static final int MIN_DISTANCE = 100;
    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ListView images;
    TextView title;
    TextView text;
    String titleText, id;
    ImageButton noteBut;
    String fatherActivity;
    ArrayList<Image> imagesData = new ArrayList<>();
    ImageAdapter adapter;
    //    CodeView codeView;
    private float x1, x2, y1, y2;


    // TODO: закончить этот метод
    // TODO: добавить поле id или переделать базу данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        String[] data = getIntent().getStringArrayExtra("data");
        Log.d("Article", data[0]);
        fatherActivity = data[2];
        titleText = data[1];
        id = data[0];

        init();

//        codeView = findViewById(R.id.codeView);

        setNote(pref, id);

//        context = getApplicationContext();
//        String name_string =  "text_"+id;
//        String textData =  context.getString(context.getResources().getIdentifier(name_string, "string", context.getPackageName()));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            text.setText(Html.fromHtml(textData, Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            text.setText(Html.fromHtml(textData));
//        }

//        text.setText(textData);

//        CharSequence code = "import math";
//        codeView.setText(code);

        boolean theme = pref.getBoolean("themeLight", true);

//        String uri = "@drawable/img_" + id + "_d";
//        if (theme) {uri = "@drawable/img_" + id + "_l";}

        setTexts();
        setImages();


        noteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation scale = AnimationUtils.loadAnimation(ArticleActivity.this, R.anim.scale);
                noteBut.startAnimation(scale);
                scale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        noteHandler(pref, id);
                        Animation scale = AnimationUtils.loadAnimation(ArticleActivity.this, R.anim.scale_in);
                        noteBut.startAnimation(scale);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

            }
        });

    }

    void init() {
        images = findViewById(R.id.images);
        noteBut = findViewById(R.id.add_note);
        title = findViewById(R.id.title_article);
        title.setText(titleText);
//        text = findViewById(R.id.text_article);
//        adapter = new ImageAdapter(getApplicationContext(), imagesData, images);
//        images.setAdapter(adapter);

    }

    // создание Header
    View createHeader(Spanned text) {
        View v = getLayoutInflater().inflate(R.layout.header_article, null);
        ((TextView)v.findViewById(R.id.text_article_header)).setText(text);
        return v;
    }


    void setImages() {
        imagesData.clear();
        final DatabaseReference data = database.getReference().child("data").child("articles").child(id).child(
                "images");
        data.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DataSnapshot images : task.getResult().getChildren()) {
                        Image image = images.getValue(Image.class);
                        imagesData.add(image);
                        Log.d("ImageLoad", "Добавлено : " + image.src);
                    }
//                    adapter.notifyDataSetChanged();
                    adapter = new ImageAdapter(getApplicationContext(), imagesData);
                    images.setAdapter(adapter);
                }
            }
        });
    }

    void setTexts() {
        final DatabaseReference data = database.getReference().child("data").child("articles").child(id);

        data.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Article article = task.getResult().getValue(Article.class);
                    Log.d("Article", article.title.toString());
                    Spanned text;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        text = Html.fromHtml(article.text, Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        text = Html.fromHtml(article.text);
                    }
                    images.addHeaderView(createHeader(text));
                }
            }
        });
    }

    void setNote(SharedPreferences pref, String thisId) {
        if (isNote(pref, thisId)) {
            noteBut.setImageResource(R.drawable.del_note);
        } else {
            noteBut.setImageResource(R.drawable.add_note);
        }
    }

    Boolean isNote(SharedPreferences pref, String thisId) {
        Set<String> ids = pref.getStringSet("notes", new HashSet<>());
        for (String id : ids) {
            if (thisId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    void noteHandler(SharedPreferences pref, String thisId) {
        Set<String> ids = pref.getStringSet("notes", new HashSet<>());
        if (isNote(pref, thisId)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Удалено из закладок", Toast.LENGTH_SHORT);
            toast.show();
            ids.remove(thisId);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Добавлено в закладки", Toast.LENGTH_SHORT);
            toast.show();
            ids.add(thisId);
        }

        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putStringSet("notes", ids);
        prefEditor.apply();

        setNote(pref, thisId);
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
        if (fatherActivity.equals("Main")) {
            Intent intent = new Intent(ArticleActivity.this, MainActivity.class);
            intent.putExtra("data", "Article");
            startActivity(intent);
        } else if (fatherActivity.equals("Note")) {
            Intent intent = new Intent(ArticleActivity.this, NotestActivity.class);
            startActivity(intent);
        } else if (fatherActivity.equals("MyArticles")){
            Intent intent = new Intent(ArticleActivity.this, MyArticles.class);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                y2 = ev.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                if (deltaX > MIN_DISTANCE && Math.abs(deltaY) < 70) {
                    onBackPressed();
//                    Toast.makeText(this, deltaX+ "  :sw:  " + deltaY, Toast.LENGTH_SHORT).show ();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
