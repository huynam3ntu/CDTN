package ntu.nthuy.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Adapters.FavoritesAdapter;
import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Model.FavoriteUtils;

public class FavoritesActivity extends AppCompatActivity {
    RecyclerView favoritesRecyclerView;
    TextView emptyFavoritesTextView;
    private FavoritesAdapter favoritesAdapter;
    private List<FavoriteUtils> listFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();

        getListFavorites();

        if (listFav == null) {
            emptyFavoritesTextView.setVisibility(View.VISIBLE);
        } else {
            emptyFavoritesTextView.setVisibility(View.GONE);
        }
    }
    private void init(){
        favoritesRecyclerView = findViewById(R.id.recyler_favorite_recipes);
        emptyFavoritesTextView = findViewById(R.id.empty_favorites_text_view);

        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
//        favoritesRecyclerView.setHasFixedSize(true);
        listFav = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(listFav, FavoritesActivity.this, recipeClickedListener);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }
    private void getListFavorites(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FavoriteUtils favoriteUtils = dataSnapshot.getValue(FavoriteUtils.class);
                    listFav.add(favoriteUtils);
                }
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoritesActivity.this, "Get list failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private final RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(FavoritesActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));
}