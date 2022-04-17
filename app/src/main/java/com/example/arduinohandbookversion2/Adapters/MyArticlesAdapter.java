package com.example.arduinohandbookversion2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arduinohandbookversion2.R;

import java.util.List;

public class MyArticlesAdapter extends RecyclerView.Adapter<MyArticlesAdapter.ViewHolder> {
    private static final String TAG = "MyArticlesAdapter";
    private final LayoutInflater inflater;
    private final List<String> list;
    ItemClickListener itemCLickListener;

    public MyArticlesAdapter(Context context, List<String> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public MyArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.header.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemCLickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView header;

        public ViewHolder(View view) {
            super(view);
            header = view.findViewById(R.id.item_text);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemCLickListener != null) {
                itemCLickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
