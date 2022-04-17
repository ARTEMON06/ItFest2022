package com.example.arduinohandbookversion2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arduinohandbookversion2.Image;
import com.example.arduinohandbookversion2.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.ViewHolder> {

    private static final String TAG = "AddImageAdapter";
    private final LayoutInflater inflater;
    private final List<Image> imageList;
    ItemClickListener itemClickListener;

    public AddImageAdapter(Context context, List<Image> images) {
        this.imageList = images;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public AddImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddImageAdapter.ViewHolder holder, int position) {
        /*
        Заполняет каждый элемент recyclerView
        Скачиваеть картинку
        */

        Image image = imageList.get(position);
        holder.label_image.setText(image.getLabel());
        Picasso.get().load(image.getSrc()).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: картинка загружена");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: сломалась картинка", e.fillInStackTrace());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView image;
        final TextView label_image;


        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image_view);
            label_image = view.findViewById(R.id.label_image);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
