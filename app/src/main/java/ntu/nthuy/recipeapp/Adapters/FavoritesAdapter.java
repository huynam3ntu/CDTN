package ntu.nthuy.recipeapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;
import ntu.nthuy.recipeapp.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder>{
    private final List<RecipeDetailsResponse> listFav;
    Context context;
    RecipeClickedListener clickedListener;

    public FavoritesAdapter(List<RecipeDetailsResponse> listFav, Context context, RecipeClickedListener clickedListener) {
        this.listFav = listFav;
        this.context = context;
        this.clickedListener = clickedListener;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        RecipeDetailsResponse fav = listFav.get(position);
        if(fav != null) {
            holder.item_favorite_number.setText(String.valueOf(position + 1));
            Picasso.get().load(listFav.get(position).image).into(holder.item_favorite_image);
            holder.item_favorite_name.setText(listFav.get(position).title);
            holder.list_favorites.setOnClickListener(v -> clickedListener.onRecipeClicked(String.valueOf(listFav.get(holder.getAdapterPosition()).id)));
        }
    }

    @Override
    public int getItemCount() {
        if(listFav != null){
            return listFav.size();
        }
        return 0;
    }
}
class FavoritesViewHolder extends RecyclerView.ViewHolder {
    CardView list_favorites;
    TextView item_favorite_number;
    ImageView item_favorite_image;
    TextView item_favorite_name;


    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        list_favorites = itemView.findViewById(R.id.list_favorites);
        item_favorite_number = itemView.findViewById(R.id.item_favorite_number);
        item_favorite_image = itemView.findViewById(R.id.item_favorite_image);
        item_favorite_name = itemView.findViewById(R.id.item_favorite_name);

    }
}

