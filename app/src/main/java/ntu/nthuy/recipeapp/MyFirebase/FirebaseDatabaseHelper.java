package ntu.nthuy.recipeapp.MyFirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public class FirebaseDatabaseHelper {
    private   DatabaseReference mDatabase;
    private final List<Integer> listId = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public List<Integer> getListId() {
        return listId;
    }

    public void addRecipe(RecipeDetailsResponse recipe) {
        DatabaseReference myRef = mDatabase.child(String.valueOf(recipe.id));
        listId.add(recipe.id);
        myRef.setValue(recipe);
    }

    public void deleteRecipe(int recipeId) {
        DatabaseReference myRef = mDatabase.child(String.valueOf(recipeId));
        listId.remove(recipeId);
        mDatabase.removeValue();
    }

    public boolean isFavorite(int id) {
        return listId.contains(id);
    }

}
