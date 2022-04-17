package com.example.arduinohandbookversion2;

import static android.view.animation.Animation.AnimationListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohandbookversion2.User.ProfileUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    static String[] names = {"Функция Setup", "Функция Loop", "Функция pinMode"};
    Boolean lightTheme = true;
    TextView title;
    ImageView circle;
    ImageButton note, theme_but, user;
    String fatherActivity = "none";
    ListView lv;
    Integer size = 3;
    ArrayList<GlobalMenuListItem> localData = new ArrayList<>();
    GlobalMenuItemAdapter adapter;
    AnimationListener animListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(), NotestActivity.class);
            intent.putExtra("data", names);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        FirebaseAuth.getInstance().signOut();
        Intent intent = getIntent();
        if (intent.hasExtra("data")) {
            fatherActivity = intent.getStringExtra("data");
        }

        buttonRelease(fatherActivity);
        SharedPreferences.Editor prefEditor = pref.edit();
//        Set<String> notes = new HashSet<>();
        prefEditor.putInt("size", size);
//        prefEditor.putStringSet("notes", notes);
        prefEditor.apply();

        setThemeIcon(pref);

        adapter = new GlobalMenuItemAdapter(this, localData);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GlobalMenuListItem item = ((GlobalMenuListItem) parent.getItemAtPosition(position));
                Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
                LinearLayout back = view.findViewById(R.id.back);
                back.startAnimation(alpha);
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                String activityName = "Main";
                String[] data = {item.id, item.name, activityName};
                Log.d("Article", item.id);
                intent.putExtra("data", data);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                finish();
            }
        });

        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationSet set = new AnimationSet(true);
                Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
                set.addAnimation(rotate);
                set.addAnimation(alpha);
                title.startAnimation(alpha);
                theme_but.startAnimation(alpha);
                user.startAnimation(alpha);
                note.startAnimation(set);
                note_activity();
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scale = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(scale);
                set.addAnimation(alpha);
                user.startAnimation(set);
                theme_but.startAnimation(alpha);
                title.startAnimation(alpha);
                circle.startAnimation(alpha);
                note.startAnimation(alpha);
                Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.down_slide_out);
                lv.startAnimation(anim);
                anim.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lv.setVisibility(View.INVISIBLE);
                        theme_but.setVisibility(View.INVISIBLE);
                        title.setVisibility(View.INVISIBLE);
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
        });

        theme_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTheme();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("data").child(
                "articles");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.i("FIREBASE", "startGet");
                localData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Article article = snapshot.getValue(Article.class);
                    localData.add(new GlobalMenuListItem(snapshot.getKey(), article.title));
//                    Log.i("FIREBASE", "getArticle: " + article.title);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("FIREBASE", "loadPost:onCancelled");
            }
        };
        database.addValueEventListener(postListener);
    }

    void init() {
        lv = findViewById(R.id.list);
        note = findViewById(R.id.notes);
        theme_but = findViewById(R.id.theme);
        user = findViewById(R.id.user);
        title = findViewById(R.id.title);
        circle = findViewById(R.id.circle);
    }

    void buttonRelease(String fatherActivity) {
        Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha_in);
        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_in);
        set.addAnimation(rotate);
        set.addAnimation(alpha);

        if (fatherActivity.equals("Notes")) {
            title.startAnimation(alpha);
            note.startAnimation(set);
            theme_but.startAnimation(alpha);
            user.startAnimation(alpha);
            anim_test();
        } else if (fatherActivity.equals("User")) {
            theme_but.startAnimation(alpha);
            title.startAnimation(alpha);
            user.startAnimation(set);
            circle.startAnimation(alpha);
            note.startAnimation(alpha);
            anim_test();
        }
    }

    void setThemeIcon(SharedPreferences pref) {
        boolean theme = pref.getBoolean("themeLight", true);

        if (theme) {
            theme_but.setImageResource(R.drawable.dark_button);
        } else {
            theme_but.setImageResource(R.drawable.light_button);
        }
    }

    void changeTheme() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean theme = pref.getBoolean("themeLight", true);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean("themeLight", !theme);
        prefEditor.apply();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    void set_theme(SharedPreferences pref) {

        boolean theme = pref.getBoolean("themeLight", true);
        if (theme) {
            setTheme(R.style.Theme_MyApplication);
        } else {
            setTheme(R.style.Theme_MyApplicationDark);
        }
    }

    void anim_test() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);

        lv.setLayoutAnimation(controller);
    }

    void note_activity() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.down_slide_out);
        lv.startAnimation(anim);
        lv.setVisibility(View.INVISIBLE);
        anim.setAnimationListener(animListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}