package com.example.arduinohandbookversion2;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotestActivity extends AppCompatActivity {
    ImageButton home;
    ListView lv;
    LinearLayout warning;
    Boolean isEmpty;
    TextView title;
    ArrayList<GlobalMenuListItem> localData = new ArrayList<>();
    Set<String> notes;
    GlobalMenuItemAdapter adapter;
    Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("data", "Notes");
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        set_theme(pref);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notest);

        notes = pref.getStringSet("notes", new HashSet<>());

        init();
        warning.setVisibility(View.GONE);
        animateButtons();
        adapter = new GlobalMenuItemAdapter(this, localData);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("data").child("articles");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.i("FIREBASE", "startGet");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Article article = snapshot.getValue(Article.class);
                    for (String noteId : notes) {
                        if (noteId.equals(snapshot.getKey())) {
                            localData.add(new GlobalMenuListItem(snapshot.getKey(), article.title));
                        }
                    }
//                    Log.i("FIREBASE", "getArticle: " + article.title);
                }
                adapter.notifyDataSetChanged();
                fitList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("FIREBASE", "loadPost:onCancelled");
            }
        };
        database.addValueEventListener(postListener);
    }

    void fitList() {
        if (!localData.isEmpty()) {
            isEmpty = false;
            warning.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

            lv.setAdapter(adapter);

            anim_test();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GlobalMenuListItem item = ((GlobalMenuListItem) parent.getItemAtPosition(position));
                    Intent intent = new Intent(NotestActivity.this, ArticleActivity.class);
                    Animation alpha = AnimationUtils.loadAnimation(NotestActivity.this, R.anim.alpha);
                    LinearLayout back = view.findViewById(R.id.back);
                    back.startAnimation(alpha);
                    String activityName = "Note";
                    String[] data = {item.id, item.name, activityName};
                    intent.putExtra("data", data);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                    finish();
                }
            });
        } else {
            isEmpty = true;
            warning.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }
    }

    void init() {
        lv = findViewById(R.id.list);
        warning = findViewById(R.id.warning);
        title = findViewById(R.id.title);
        home = findViewById(R.id.home);
    }

    void animateButtons() {
        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(NotestActivity.this, R.anim.rotate_in);
        Animation alpha = AnimationUtils.loadAnimation(NotestActivity.this, R.anim.alpha_in);
        set.addAnimation(rotate);
        set.addAnimation(alpha);
        home.startAnimation(set);
        title.startAnimation(alpha);
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
        AnimationSet set = new AnimationSet(true);
        Animation rotate = AnimationUtils.loadAnimation(NotestActivity.this, R.anim.rotate);
        Animation alpha = AnimationUtils.loadAnimation(NotestActivity.this, R.anim.alpha);
        set.addAnimation(rotate);
        set.addAnimation(alpha);
        title.startAnimation(alpha);
        home.startAnimation(set);
        main_activity();
    }

    void anim_test() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(150);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);

        lv.setLayoutAnimation(controller);
        lv.setVisibility(View.VISIBLE);
    }

    void main_activity() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.down_slide_out);
        warning.startAnimation(anim);
        warning.setVisibility(View.GONE);
        lv.startAnimation(anim);
        lv.setVisibility(View.GONE);
        anim.setAnimationListener(animListener);
    }
}