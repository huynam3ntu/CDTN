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

        DatabaseReference myRef = mDatabase.child(String.valueOf(recipe.id));
        listId.add(recipe.id);
        FavoriteUtils favoriteUtils = new FavoriteUtils(recipe.id, recipe.title, recipe.image, recipe.summary, "");
        myRef.setValue(favoriteUtils);
    }

    public void deleteRecipe(RecipeDetailsResponse recipe) {
        DatabaseReference myRef = mDatabase.child(String.valueOf(recipe.id));
        listId.remove(recipe.id);
        myRef.removeValue();
    }

    public boolean isFavorite(int id) {
        return listId.contains(id);
    }

}
