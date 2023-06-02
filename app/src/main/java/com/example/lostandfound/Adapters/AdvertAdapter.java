package com.example.lostandfound.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.DataClasses.Advert;
import com.example.lostandfound.R;
import com.example.lostandfound.ShowIndividualAdvertActivity;

import java.util.List;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    private List<Advert> adverts;
    private OnItemClickListener clickListener;

    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Advert advert);
    }



    public AdvertAdapter(List<Advert> adverts, OnItemClickListener clickListener) {
        this.adverts = adverts;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        Advert advert = adverts.get(position);
        holder.bind(advert);
    }

    @Override
    public int getItemCount() {
        return adverts.size();
    }

    public class AdvertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);
        }

        public void bind(Advert advert) {
            tvName.setText(advert.getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Advert clickedAdvert = adverts.get(position);
                clickListener.onItemClick(clickedAdvert);
            }
        }
    }
}
