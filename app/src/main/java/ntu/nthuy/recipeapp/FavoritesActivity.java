package ntu.nthuy.recipeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ntu.nthuy.recipeapp.Adapters.FavoritesAdapter;
import ntu.nthuy.recipeapp.Adapters.IngredientsAdapter;
import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Model.ExtendedIngredient;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;
import ntu.nthuy.recipeapp.MyFirebase.FirebaseDatabaseHelper;

public class FavoritesActivity extends AppCompatActivity {
    RecyclerView favoritesRecyclerView;
    TextView emptyFavoritesTextView;
    private FavoritesAdapter favoritesAdapter;
    private List<RecipeDetailsResponse> listFav;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        init();
        
        favoritesRecyclerView.setHasFixedSize(true);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        listFav = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(listFav, FavoritesActivity.this, recipeClickedListener);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        onClickReadData();

        if(favoritesAdapter.getItemCount()!=0)
            emptyFavoritesTextView.setVisibility(View.VISIBLE);
        else
            emptyFavoritesTextView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.add_recipe){
            // Xử lý sự kiện khi người dùng chọn nút "Add Recipe"
            showAddRecipeDialog();
            //update lại data cho favoritesAdapter
            favoritesAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void init(){
        favoritesRecyclerView = findViewById(R.id.recyler_favorite_recipes);
        emptyFavoritesTextView = findViewById(R.id.empty_favorites_text_view);

        toolbar = findViewById(R.id.toolbarFav);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                    RecipeDetailsResponse fav = dataSnapshot.getValue(RecipeDetailsResponse.class);
                    listFav.add(fav);
                }
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoritesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showAddRecipeDialog() {
        // Hiển thị dialog để người dùng nhập liệu và thêm mới món ăn
        // Tạo dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Recipe");
        // Tạo layout cho dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_recipe, null);
        EditText titleEditText = view.findViewById(R.id.edt_dialogFav_title);
        EditText imageEditText = view.findViewById(R.id.edt_dialogFav_image);
        EditText sourceNameEditText = view.findViewById(R.id.edt_dialogFav_source);
        EditText summaryEditText = view.findViewById(R.id.edt_dialogFav_summary);
        RecyclerView ingredientsRecyclerView = view.findViewById(R.id.recyler_ingredients_fav);

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(this, new ArrayList<>());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Thêm listener cho nút "Add Ingredient"
        Button addIngredientButton = view.findViewById(R.id.button_add_ingredient);
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog(ingredientsAdapter));
        // Thiết lập layout cho dialog
        builder.setView(view);
        // Thêm các nút "Cancel" và "Add"

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Lấy giá trị từ các EditText và tạo đối tượng RecipeDetailsResponse mới
            int id = generateId();

            String title = titleEditText.getText().toString();
            String image = imageEditText.getText().toString();
            String sourceName = sourceNameEditText.getText().toString();
            String summary = summaryEditText.getText().toString();
            ArrayList<ExtendedIngredient> ingredients = ingredientsAdapter.getIngredient();
            RecipeDetailsResponse recipe = new RecipeDetailsResponse(id, title, image, sourceName, ingredients, summary, "");

            // Thêm mới món ăn vào Firebase Realtime Database
            FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();
            databaseHelper.addRecipe(recipe);

            dialog.dismiss();
        });
        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int generateId() {
        UUID uuid = UUID.randomUUID();
        return uuid.hashCode();
    }

    private void showAddIngredientDialog(IngredientsAdapter ingredientsAdapter) {
        // Tạo dialog builder
        int id = generateId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Ingredient");
        // Tạo layout cho dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ingredient, null);
        EditText nameEditText = view.findViewById(R.id.edt_dialogIngredient_name);
        EditText imageEditText = view.findViewById(R.id.edt_dialogIngredient_image);
        EditText originalEditText = view.findViewById(R.id.edt_dialogIngredient_original);
        // Thiết lập layout cho dialog
        builder.setView(view);
        // Thêm các nút "Cancel" và "Add"
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Add", (dialog, which) -> {
            // Lấy giá trị từ các EditText và tạo đối tượng ExtendedIngredient mới
            String name = nameEditText.getText().toString();
            String image = imageEditText.getText().toString();
            String original = originalEditText.getText().toString();
            ExtendedIngredient ingredient = new ExtendedIngredient(id, image, name, original);
            // Thêm mới nguyên liệu vào adapter
            ingredientsAdapter.addIngredient(ingredient);
            dialog.dismiss();
        });
        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private final  RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(FavoritesActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));
}