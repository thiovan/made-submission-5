package thiovan.favoritemovie.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import thiovan.favoritemovie.R;
import thiovan.favoritemovie.models.Movie;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.CardViewViewHolder> {
    private final ArrayList<Movie> listMovie;
    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public MovieListAdapter(ArrayList<Movie> list) {
        this.listMovie = list;
    }

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewViewHolder holder, int position) {
        Movie movie = listMovie.get(position);

        Glide.with(holder.imgPhoto.getContext())
                .load(movie.getPhoto())
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .into(holder.imgPhoto);
        holder.tvName.setText(movie.getName());
        holder.tvDescription.setText(!movie.getDescription().isEmpty()
                ? movie.getDescription()
                :holder.tvDescription.getContext().getResources().getString(R.string.error_no_transalation));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(listMovie.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMovie.size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPhoto;
        private final TextView tvName;
        private final TextView tvDescription;

        CardViewViewHolder(View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_item_photo);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(Movie data);
    }
}
