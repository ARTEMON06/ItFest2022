package com.example.arduinohandbookversion2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseTools {
    final static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    final static Integer counterFinal = 5;
    public static ArrayList<Article> articles = new ArrayList<>();

    public static void startListener() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (int i = 0; i < counterFinal; i++) {
                    Article article = dataSnapshot.child("published").child("data").getValue(Article.class);
                    articles.clear();
                    articles.add(article);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("FIREBASE", "loadPost:onCancelled");
            }
        };
        database.addValueEventListener(postListener);

    }

    public static ArrayList<Article> getData() {
        int count = getCounter();
        ArrayList<Article> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Article article = getArticle(i);
            result.add(article);
        }

        return result;
    }

    static Article getArticle(Integer i) {
        final Article[] data = new Article[1];
        database.child("published").child("data").child(Integer.toString(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    data[0] = task.getResult().getValue(Article.class);
                }
            }
        });
        return data[0];
    }

    static Integer getCounter() {
        final int[] counter = new int[1];
        database.child("published").child("info").child("counter").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    counter[0] = Integer.parseInt((String) task.getResult().getValue());
                    Log.e("FIREBASE", "COUNTER: " + counter[0]);
                }
            }
        });
        Log.e("FIREBASE", "COUNTER RETURN: " + counter[0]);
        return counter[0];
    }
}
