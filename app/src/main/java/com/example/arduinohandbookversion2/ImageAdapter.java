package com.example.arduinohandbookversion2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageAdapter extends ArrayAdapter<Image> {

    private final Context context;
    private ArrayList<Image> arr;
    private int count;

    public ImageAdapter(Context context, ArrayList<Image> arr) {
        super(context, R.layout.adapter_image, arr);
        this.context = context;
        this.arr = arr;
        this.count = arr.size();
        Log.d("ImageAdapterSet", "count: " + this.count);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        Image item = this.arr.get(position);
        Log.d("ImageGet", "Position: " + position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_image, null);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.labelImage = (TextView) convertView.findViewById(R.id.labelImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
// Заполняем адаптер
        holder.labelImage.setText(item.label);
        Picasso.get().load(item.src).into(holder.imageView);
        Log.d("ImageDownload", "Успешно загружено изображение: " + item.src);
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView labelImage;
        int position;
    }

    @Override
    public int getCount() {
        return this.arr.size();
    }

    @Override
    public Image getItem(int position) {
        return this.arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

