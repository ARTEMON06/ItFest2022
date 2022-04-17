package com.example.arduinohandbookversion2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class GlobalMenuItemAdapter extends ArrayAdapter<GlobalMenuListItem> {

    public GlobalMenuItemAdapter(Context context, ArrayList<GlobalMenuListItem> arr) {
        super(context, R.layout.adapter_item, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GlobalMenuListItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item, null);
        }

// Заполняем адаптер
        ((TextView) convertView.findViewById(R.id.item_text)).setText(item.name);

        return convertView;
    }
}