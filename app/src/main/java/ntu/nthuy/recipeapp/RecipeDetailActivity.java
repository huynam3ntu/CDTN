package ntu.nthuy.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import ntu.nthuy.recipeapp.Adapters.IngredientsAdapter;
import ntu.nthuy.recipeapp.Adapters.InstructionsAdapter;
import ntu.nthuy.recipeapp.Adapters.SimilarRecipesAdapter;
import ntu.nthuy.recipeapp.Listeners.InstructionsListener;
import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Listeners.RecipeDetailsListener;
import ntu.nthuy.recipeapp.Listeners.SimilarRecipesListener;
import ntu.nthuy.recipeapp.Model.InstructionsReponse;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;
import ntu.nthuy.recipeapp.Model.SimilarRecipesResponse;
import ntu.nthuy.recipeapp.MyFirebase.FirebaseDatabaseHelper;


public class RecipeDetailActivity extends AppCompatActivity {
    int id;
    TextView textView_meal_name, textView_meal_source, textView_meal_summary;
    ImageView imageView_meal_image;
    RecyclerView recyler_meal_ingredients, recyler_meal_similar, recyler_meal_instructions;
    RequestManager manager;
    AlertDialog.Builder builderDialog;
    AlertDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipesAdapter similarRecipesAdapter;
    InstructionsAdapter instructionsAdapter;
    ImageButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        findViews();

        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new RequestManager(this);

        manager.getRecipeDetails(recipeDetailsListener, id);

        manager.getSimilarRecipes(similarRecipesListener, id);
        manager.getInstructions(instructionsListener, id);

        builderDialog = new AlertDialog.Builder(this);
        builderDialog.setMessage("Loading Details...");
        builderDialog.setCancelable(false);
        dialog = builderDialog.create();
        dialog.show();
    }

    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        textView_meal_summary = findViewById(R.id.textView_meal_summary);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recyler_meal_ingredients = findViewById(R.id.recyler_meal_ingredients);
        recyler_meal_similar = findViewById(R.id.recyler_meal_similar);
        recyler_meal_instructions = findViewById(R.id.recyler_meal_instructions);
        favoriteButton = findViewById(R.id.favorite_button);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();

            FirebaseDatabaseHelper myData = new FirebaseDatabaseHelper();
            boolean isFavorite = myData.isFavorite(response.id);

            DatabaseReference myRef = myData.getmDatabase().child(String.valueOf(response.id));

            if(isFavorite){
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                // Món ăn đã được thêm vào danh sách yêu thích
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView_meal_name.setText(snapshot.child("title").getValue(String.class));

                        Document doc = Jsoup.parse(snapshot.child("summary").getValue(String.class));
                        String rpsummary = doc.text();
                        textView_meal_summary.setText(rpsummary);

                        //Hien thi them cai note cua nguoi dung: y tuong them 1 cai bieu tuong bong bong chat
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }else{
                favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                textView_meal_name.setText(response.title);
                Document doc = Jsoup.parse(response.summary);
                String rpsummary = doc.text();
                textView_meal_summary.setText(rpsummary);

            }

            textView_meal_source.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            recyler_meal_ingredients.setHasFixedSize(true);
            recyler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recyler_meal_ingredients.setAdapter(ingredientsAdapter);

            favoriteButton.setOnClickListener(v -> {
                if(isFavorite){
                    // Món ăn đã được thêm vào danh sách yêu thích
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                    // Nếu món ăn đã có trong danh sách yêu thích, xóa nó khỏi danh sách
                    myData.deleteRecipe(response);
                    //Thông báo
                    Toast.makeText(RecipeDetailActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                }else {
                    // Món ăn chưa được thêm vào danh sách yêu thích
                    favoriteButton.setImageResource(R.drawable.ic_favorite);
                    // Nếu món ăn chưa có trong danh sách yêu thích, thêm nó vào danh sách
                    myData.addRecipe(response);
                    //Thông báo
                    Toast.makeText(RecipeDetailActivity.this, "Added", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipesResponse> response, String message) {
            recyler_meal_similar.setHasFixedSize(true);
            recyler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipesAdapter = new SimilarRecipesAdapter(RecipeDetailActivity.this, response, recipeClickedListener);
            recyler_meal_similar.setAdapter(similarRecipesAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsReponse> reponse, String message) {
            recyler_meal_instructions.setHasFixedSize(true);
            recyler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailActivity.this, reponse);
            recyler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
}