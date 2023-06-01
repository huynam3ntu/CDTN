package ntu.nthuy.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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
    ImageButton imageButton;
    private FavoritesAdapter favoritesAdapter;
    protected List<FavoriteUtils> listFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();

        setEvent();

    }
    private void init(){
        favoritesRecyclerView = findViewById(R.id.recyler_favorite_recipes);
        emptyFavoritesTextView = findViewById(R.id.empty_favorites_text_view);
        imageButton = findViewById(R.id.imgBtnClear);


        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        favoritesRecyclerView.setHasFixedSize(true);
        listFav = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(listFav, FavoritesActivity.this, recipeClickedListener);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }
    private void setEvent(){
        imageButton.setOnClickListener(v -> onClickReadData());
    }
    private void onClickReadData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("recipes").addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFav.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FavoriteUtils fav = dataSnapshot.getValue(FavoriteUtils.class);
                    listFav.add(fav);
                }
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private final RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(FavoritesActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));
}