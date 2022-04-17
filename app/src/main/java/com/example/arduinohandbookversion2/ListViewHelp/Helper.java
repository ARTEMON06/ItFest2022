package com.example.arduinohandbookversion2.ListViewHelp;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Helper {


    public static void getListViewSize(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int listWidth = listView.getMeasuredWidth();
        for (int size = 0; size < listAdapter.getCount(); size++) {
            View view = listAdapter.getView(size, null, listView);
            view.measure(View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += view.getMeasuredHeight();
            Log.d("Height", String.valueOf(view.getMeasuredHeight()));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        params.height = 2000;
        params.height = height;
        listView.setLayoutParams(params);

        Log.d("Height", "Всего: " + String.valueOf(height));

    }
}
