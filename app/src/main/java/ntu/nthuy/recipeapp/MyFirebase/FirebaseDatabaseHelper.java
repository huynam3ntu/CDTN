package ntu.nthuy.recipeapp.MyFirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Model.FavoriteUtils;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public class FirebaseDatabaseHelper {
    public  DatabaseReference mDatabase;
    private final List<Integer> listId = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public void addRecipe(RecipeDetailsResponse recipe) {
        listId.add(recipe.id);
        FavoriteUtils fav = new FavoriteUtils(recipe.id, recipe.title, recipe.image, recipe.summary, "");
        mDatabase.setValue(fav);
    }

    public void deleteRecipe(RecipeDetailsResponse recipe) {
        listId.remove(recipe.id);
        mDatabase.removeValue();
    }

    public boolean isFavorite(int id) {
        return true;
    }

}
