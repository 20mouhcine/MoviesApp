package com.example.moviesapp_latiris;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private List<GenreData> genres;
    private OnGenreClickListener listener;

    public interface OnGenreClickListener {
        void onGenreClick(GenreData genre);
    }

    public GenreAdapter(List<GenreData> genres, OnGenreClickListener listener) {
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenreData genre = genres.get(position);
        holder.genreName.setText(genre.getName());

        if (genre.isSelected()) {
            holder.cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
            holder.genreName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        } else {
            holder.cardView.setCardBackgroundColor(0xFF1A1A1D);
            holder.genreName.setTextColor(0xFFFFFFFF);
        }

        holder.itemView.setOnClickListener(v -> {
            for (GenreData g : genres) {
                g.setSelected(false);
            }
            genre.setSelected(true);
            notifyDataSetChanged();
            listener.onGenreClick(genre);
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genreName);
            cardView = itemView.findViewById(R.id.genreCard);
        }
    }
}
