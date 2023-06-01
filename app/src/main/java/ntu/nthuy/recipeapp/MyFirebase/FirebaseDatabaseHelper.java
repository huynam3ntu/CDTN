package ntu.nthuy.recipeapp.MyFirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Model.FavoriteUtils;

public class FirebaseDatabaseHelper {
    private   DatabaseReference mDatabase;
    private final List<Integer> listId = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public void addRecipe(FavoriteUtils recipe) {
        DatabaseReference myRef = mDatabase.child(String.valueOf(recipe.id));
        listId.add(recipe.id);
        FavoriteUtils fav = new FavoriteUtils(recipe.id, recipe.title, recipe.image, recipe.summary, "");
        myRef.setValue(fav);
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
