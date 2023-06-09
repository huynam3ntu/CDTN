package ntu.nthuy.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import ntu.nthuy.recipeapp.Adapters.IngredientsAdapter;
import ntu.nthuy.recipeapp.Model.ExtendedIngredient;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public class EditDetailsRecipeActivity extends AppCompatActivity {
    private DatabaseReference recipeRef;
    private RecipeDetailsResponse recipeDetails;
    EditText titleEditText, imageEditText, sourceNameEditText, summaryEditText, noteEditText;
    RecyclerView extendedIngredientsRecyclerView, instructionsRecyclerView;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details_recipe);
        findViews();
        // Lấy id của đối tượng RecipeDetailsResponse cần chỉnh sửa từ intent
        String recipeId = getIntent().getStringExtra("id");
        Toast.makeText(this, recipeId, Toast.LENGTH_SHORT).show();

        // Khởi tạo DatabaseReference để truy cập đến đối tượng RecipeDetailsResponse
        // tương ứng trên Firebase Realtime
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy thông tin về đối tượng RecipeDetailsResponse từ Firebase Realtime
                recipeDetails = snapshot.getValue(RecipeDetailsResponse.class);
                // Hiển thị thông tin lên layout
                titleEditText.setText(recipeDetails.getTitle());
                imageEditText.setText(recipeDetails.getImage());
                sourceNameEditText.setText(recipeDetails.getSourceName());

                extendedIngredientsRecyclerView.setHasFixedSize(true);
                extendedIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(EditDetailsRecipeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                IngredientsAdapter extendedIngredientsAdapter = new IngredientsAdapter(EditDetailsRecipeActivity.this, recipeDetails.getExtendedIngredients());
                extendedIngredientsRecyclerView.setAdapter(extendedIngredientsAdapter);

//                instructionsRecyclerView.setHasFixedSize(true);
//                instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(EditDetailsRecipeActivity.this, LinearLayoutManager.HORIZONTAL, false));
//                InstructionsAdapter instructionsAdapter = new InstructionsAdapter(EditDetailsRecipeActivity.this, recipeDetails.getInstructionsReponses());
//                instructionsRecyclerView.setAdapter(instructionsAdapter);

                summaryEditText.setText(recipeDetails.getSummary());
                noteEditText.setText(recipeDetails.getNote());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        // Thiết lập sự kiện click cho nút "Save"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị mới từ các view trên layout
                String title = ((EditText) findViewById(R.id.edt_edit_title)).getText().toString();
                String image = ((EditText) findViewById(R.id.edt_edit_image)).getText().toString();
                String sourceName = ((EditText) findViewById(R.id.edt_edit_source_name)).getText().toString();
                ArrayList<ExtendedIngredient> extendedIngredients = ((IngredientsAdapter) extendedIngredientsRecyclerView.getAdapter()).getIngredient();
//                ArrayList<InstructionsReponse> instructionsReponses = ((InstructionsAdapter) instructionsRecyclerView.getAdapter()).getInstructions();
                String summary = ((EditText) findViewById(R.id.summary_edit_text)).getText().toString();
                String note = ((EditText) findViewById(R.id.note_edit_text)).getText().toString();

                // Cập nhật đối tượng RecipeDetailsResponse trên Firebase Realtime
                recipeDetails.setTitle(title);
                recipeDetails.setImage(image);
                recipeDetails.setSourceName(sourceName);
                recipeDetails.setExtendedIngredients(extendedIngredients);
//                recipeDetails.setInstructionsReponses(instructionsReponses);
                recipeDetails.setSummary(summary);
                recipeDetails.setNote(note);
                recipeRef.setValue(recipeDetails);

                // Trở về danh sách favorite
                startActivity(new Intent(EditDetailsRecipeActivity.this, FavoritesActivity.class).putExtra("fromEdit", true));
            }
        });
    }

    private void findViews() {
        titleEditText = findViewById(R.id.edt_edit_title);
        imageEditText = findViewById(R.id.edt_edit_image);
        sourceNameEditText = findViewById(R.id.edt_edit_source_name);
        extendedIngredientsRecyclerView = findViewById(R.id.recycler_view_Edit_extended_ingredients);
        instructionsRecyclerView = findViewById(R.id.recycler_view_Edit_instructions);
        summaryEditText = findViewById(R.id.summary_edit_text);
        noteEditText = findViewById(R.id.note_edit_text);
        saveButton = findViewById(R.id.save_button);
    }
}