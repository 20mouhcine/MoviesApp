package com.example.moviesapp_latiris;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MyMovieAdapter extends RecyclerView.Adapter<MyMovieAdapter.ViewHolder> {
    private List<MyMovieData> myMovieData;
    private Context context;

    public MyMovieAdapter(List<MyMovieData> myMovieData, Context context) {
        this.myMovieData = myMovieData;
        this.context = context;
    }

    public void addMovies(List<MyMovieData> newMovies) {
        int startPos = myMovieData.size();
        myMovieData.addAll(newMovies);
        notifyItemRangeInserted(startPos, newMovies.size());
    }

    public void setMovies(List<MyMovieData> movies) {
        this.myMovieData = new ArrayList<>(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyMovieData movie = myMovieData.get(position);
        holder.textViewName.setText(movie.getMovieName());
        holder.textViewDate.setText(movie.getMovieDate());

        // Set rating
        float ratingValue = (float) (movie.getRating() / 2.0); // Convert from 10-point to 5-point scale
        holder.movieRating.setRating(Math.min(5, ratingValue));
        holder.movieRatingScore.setText(String.format("%.1f", movie.getRating()));

        String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getMovieImage();

        Glide.with(context)
                .load(imageUrl)
                .into(holder.movieImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getId());
            intent.putExtra("movieName", movie.getMovieName());
            intent.putExtra("movieDate", movie.getMovieDate());
            intent.putExtra("movieImage", movie.getMovieImage());
            intent.putExtra("movieRating", movie.getRating());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return myMovieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImage;
        TextView textViewName;
        TextView textViewDate;
        RatingBar movieRating;
        TextView movieRatingScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.moviePoster);
            textViewName = itemView.findViewById(R.id.movieTitle);
            textViewDate = itemView.findViewById(R.id.textdate);
            movieRating = itemView.findViewById(R.id.movieRating);
            movieRatingScore = itemView.findViewById(R.id.movieRatingScore);
        }
    }
}