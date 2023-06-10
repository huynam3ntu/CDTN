package ntu.nthuy.recipeapp.MyFirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ntu.nthuy.recipeapp.Model.InstructionsReponse;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public class FirebaseDatabaseHelper {
    //  Tham chiếu đến nút "recipes" trong Firebase Realtime Database
    private   DatabaseReference mDatabase;
    // Khởi tạo
    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
    }
    // Thêm một đối tượng RecipeDetailsResponse mới
    public void addRecipe(RecipeDetailsResponse recipe) {
        DatabaseReference myRef = mDatabase.child(String.valueOf(recipe.id));
        myRef.setValue(recipe);
    }
    // Thêm danh sách các bước hướng dẫn nấu ăn
    public void addInstructions(int id, ArrayList<InstructionsReponse> reponse){
        DatabaseReference myRef = mDatabase.child(String.valueOf(id)).child("instructionsReponses");
        myRef.setValue(reponse);
    }
}
