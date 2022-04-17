package com.example.arduinohandbookversion2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arduinohandbookversion2.Adapters.MyArticlesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyArticles extends AppCompatActivity {
    private static final String TAG = "MyArticles";
    ArrayList<GlobalMenuListItem> list;
    ListView listMyArticles;
    DatabaseReference database;
    ImageButton addArticleButton;
    FirebaseUser user;
    LinearLayout warning;
    MyArticlesAdapter adapter;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles);
        init();

        addArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateArticle.class);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreate: " + list.toString());
    }

    private void getMyArticles() {
        Log.d(TAG, "getMyArticles called");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: " + snapshot.getValue());
                if (snapshot.getValue() != null) {
                    String[] articlesId = snapshot.getValue().toString().split(",");
                    list.clear();
                    for (String s : articlesId) {
                        database.child("articles").child(s).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: " + task.getResult().getValue(Article.class).title);
                                    Article article = task.getResult().getValue(Article.class);
                                    list.add(new GlobalMenuListItem(task.getResult().getKey(), article.title));
                                    Log.d(TAG, "onComplete: 1" + list.toString());
                                    setAdapter(list);

                                } else {
                                    Log.e(TAG, "onComplete: ошибка", task.getException());
                                    Toast.makeText(MyArticles.this, "Ошибка вывода Ваших статей", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    warning.setVisibility(View.GONE);
                } else {
                    warning.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onComplete: Нет статей");
                    Toast.makeText(MyArticles.this, "У Вас нет своих статей", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ошибка", error.toException());
            }

        };


    }

    private void setAdapter(ArrayList<GlobalMenuListItem> data) {

        GlobalMenuItemAdapter adapter = new GlobalMenuItemAdapter(MyArticles.this, data);

        listMyArticles.setAdapter(adapter);
    }



    private void init() {
        addArticleButton = findViewById(R.id.add_my_article);
        warning = findViewById(R.id.warning_my_articles);
        warning.setVisibility(View.GONE);
        listMyArticles = findViewById(R.id.list_my_articles);

        listMyArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalMenuListItem item = ((GlobalMenuListItem) parent.getItemAtPosition(position));
                Log.d(TAG, "ITEM ID: " + item.id);
                Animation alpha = AnimationUtils.loadAnimation(MyArticles.this, R.anim.alpha);
                LinearLayout back = view.findViewById(R.id.back);
                back.startAnimation(alpha);
                Intent intent = new Intent(MyArticles.this, ArticleActivity.class);
                String activityName = "MyArticles";
                String[] data = {item.id, item.name, activityName};
                Log.d("Article Intent", item.id);
                intent.putExtra("data", data);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                finish();
            }
        });

//        listMyArticles.addItemDecoration(new VerticalSpaceItemDecoration(5));
        database = FirebaseDatabase.getInstance().getReference("data");
        user = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        getMyArticles();
        database.child("authors").child(user.getUid()).addValueEventListener(listener);
    }

    private void set_theme(SharedPreferences pref) {

        boolean theme = pref.getBoolean("themeLight", true);
        if (theme) {
            setTheme(R.style.Theme_MyApplication);
        } else {
            setTheme(R.style.Theme_MyApplicationDark);
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}