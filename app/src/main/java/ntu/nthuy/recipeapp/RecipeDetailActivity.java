package ntu.nthuy.recipeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Adapters.IngredientsAdapter;
import ntu.nthuy.recipeapp.Adapters.InstructionsAdapter;
import ntu.nthuy.recipeapp.Adapters.SimilarRecipesAdapter;
import ntu.nthuy.recipeapp.Listeners.InstructionsListener;
import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Listeners.RecipeDetailsListener;
import ntu.nthuy.recipeapp.Listeners.RecipeFavoriteDetailsListener;
import ntu.nthuy.recipeapp.Listeners.SimilarRecipesListener;
import ntu.nthuy.recipeapp.Model.InstructionsReponse;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;
import ntu.nthuy.recipeapp.Model.SimilarRecipesResponse;
import ntu.nthuy.recipeapp.MyFirebase.FirebaseDatabaseHelper;


public class RecipeDetailActivity extends AppCompatActivity {
    int id;
    private boolean fromMain;
    private boolean fromFavorites;
    TextView textView_meal_name, textView_meal_source, textView_meal_summary;
    ImageView imageView_meal_image;
    RecyclerView recyler_meal_ingredients, recyler_meal_similar, recyler_meal_instructions;
    RequestManager manager;
    AlertDialog.Builder builderDialog;
    AlertDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipesAdapter similarRecipesAdapter;
    InstructionsAdapter instructionsAdapter;
    ImageButton favoriteButton, deleteButton;
    boolean isFavorite = false;
    FirebaseDatabaseHelper myData;
    private RecipeFavoriteDetailsListener recipeFavoriteDetailsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        findViews();
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        fromMain = getIntent().getBooleanExtra("fromMain", false);
//        fromFavorites = getIntent().getBooleanExtra("fromFav", false);

        if(fromMain){
            Toast.makeText(RecipeDetailActivity.this, "NOT FROM FAVORITE!!!!!!", Toast.LENGTH_SHORT).show();

            manager = new RequestManager(this);
            manager.getRecipeDetails(recipeDetailsListener, id);
            manager.getSimilarRecipes(similarRecipesListener, id);
            manager.getInstructions(instructionsListener, id);
        } else {
            Toast.makeText(RecipeDetailActivity.this, "FROM FAVORITE!!!!!!", Toast.LENGTH_SHORT).show();
            loadRecipeDetails();
            setRecipeFavoriteDetailsListener(recipeFavoriteDetailsListener);
            showDetails();
        }

        builderDialog = new AlertDialog.Builder(this);
        builderDialog.setMessage("Loading Details...");
        builderDialog.setCancelable(false);
        dialog = builderDialog.create();
        dialog.show();
    }
    private void loadRecipeDetails() {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(String.valueOf(id));
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RecipeDetailsResponse recipeDetailsResponse = snapshot.getValue(RecipeDetailsResponse.class);
                if (recipeDetailsResponse != null) {
                    // Nếu có đối tượng trả về, thì sẽ truyền đối tượng cho bộ lắng nghe
                    recipeFavoriteDetailsListener.didFetch(recipeDetailsResponse);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showDetails() {
        setRecipeFavoriteDetailsListener(response -> {
            dialog.dismiss();
            Toast.makeText(this, String.valueOf(response.id), Toast.LENGTH_SHORT).show();

            favoriteButton.setImageResource(R.drawable.ic_edit_button);

            textView_meal_name.setText(response.title);
            Document doc = Jsoup.parse(response.summary);
            String rpsummary = doc.text();
            textView_meal_summary.setText(rpsummary);
            textView_meal_source.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            //Hiển thị dữ liệu lên Recycler View
            recyler_meal_ingredients.setHasFixedSize(true);
            recyler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recyler_meal_ingredients.setAdapter(ingredientsAdapter);

            //Hiển thị dữ liệu lên Recycler View -- đang bị lỗi
//            recyler_meal_instructions.setHasFixedSize(true);
//            recyler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.VERTICAL, false));
//            instructionsAdapter = new InstructionsAdapter(RecipeDetailActivity.this, response.instructionsReponses);
//            recyler_meal_instructions.setAdapter(instructionsAdapter);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecipeDetailActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecipeDetailActivity.this, EditDetailsRecipeActivity.class);
                    intent.putExtra("fromDetailFav", true);
                    intent.putExtra("id", String.valueOf(id));
                    startActivity(intent);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Thực hiện xóa đối tượng RecipeDetailsResponse trên Firebase Realtime
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("recipes").child(String.valueOf(id));
                            myRef.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(RecipeDetailActivity.this, "Delete completed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        });
    }

    public void setRecipeFavoriteDetailsListener(RecipeFavoriteDetailsListener listener) {
        this.recipeFavoriteDetailsListener = listener;
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
        deleteButton = findViewById(R.id.favorite_delete_button);

        myData = new FirebaseDatabaseHelper();
    }

    // Các bộ lắng nghe dữ liệu API
    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();

            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            textView_meal_name.setText(response.title);
            Document doc = Jsoup.parse(response.summary);
            String rpsummary = doc.text();
            textView_meal_summary.setText(rpsummary);

            textView_meal_source.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            recyler_meal_ingredients.setHasFixedSize(true);
            recyler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recyler_meal_ingredients.setAdapter(ingredientsAdapter);

            favoriteButton.setOnClickListener(v -> {
                // Món ăn chưa được thêm vào danh sách yêu thích
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                // Nếu món ăn chưa có trong danh sách yêu thích, thêm nó vào danh sách
                RecipeDetailsResponse favR = new RecipeDetailsResponse(response.id, response.title, response.image, response.sourceName, response.extendedIngredients, new ArrayList<>(),response.summary, "");
                myData.addRecipe(favR);
                //Thông báo
                Toast.makeText(RecipeDetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
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
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(ArrayList<InstructionsReponse> reponse, String message) {
            myData.addInstructions(id, reponse);

            //Hiển thị dữ liệu lên Recycler View
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
    private final RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));

}