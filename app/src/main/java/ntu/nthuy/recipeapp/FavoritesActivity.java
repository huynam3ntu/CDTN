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

import androidx.activity.result.ActivityResultLauncher;
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
import com.google.firebase.storage.FirebaseStorage;

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

public class FavoritesActivity extends AppCompatActivity{
    // Khai báo thành phần RecyclerView
    // để hiển thị công thức yêu thích
    RecyclerView favoritesRecyclerView;
    // Hiển thị một TextView thông báo trống nếu không có công thức nào được yêu thích
    TextView emptyFavoritesTextView;
    // Sử dụng FavoritesAdapter để xử lý việc hiển thị các công thức nấu ăn yêu thích trong RecyclerView
    private FavoritesAdapter favoritesAdapter;
    // Danh sách các đối tượng RecipeDetailsResponse
    private List<RecipeDetailsResponse> listFav;
    // Thành phần Toolbar
    Toolbar toolbar;

    FirebaseStorage storage = FirebaseStorage.getInstance();
//    ImageView imageViewMeal;
    private ActivityResultLauncher<String> galleryLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Sử dụng fromEdit nhận từ Intent
        // để kiểm tra xem màn hình trước đó có phải là màn hình chỉnh sửa công thức nấu ăn hay không?
        boolean fromEdit = getIntent().getBooleanExtra("fromEdit", false);
        if(fromEdit)
            // Nếu đúng, một thông báo "New Updated!!" sẽ được hiển thị
            Toast.makeText(FavoritesActivity.this, "Dữ liệu được cập nhật!", Toast.LENGTH_SHORT).show();

        // Gọi phương thức init
        // và thiết lập RecyclerView cho danh sách công thức nấu ăn yêu thích
        init();
        // Gọi phương thức onClickReadData để load dữ liệu lấy từ Firebase Realtime Database
        onClickReadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Thiết lập cho thanh công cụ trên màn hình
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int idItem = item.getItemId();

        if(idItem == R.id.add_recipe){
            // Nhấn nút "Add Recipe"
            // Hộp thoại sẽ hiển thị thêm mới vào danh sách yêu thích
            showAddRecipeDialog();
            //Cập nhật lại danh sách công thức nấu ăn yêu thích trong RecyclerView
            favoritesAdapter.notifyDataSetChanged();
            return true;
        }
        else
            if(idItem == R.id.home_click){
                // Nếu người dùng nhấp vào nút "Home"
                // Chuyển sang màn hình MainActivity
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                finish();
                return true;
            }
        // Nếu không có mục nào được chọn, thì xử lý sự kiện mặc định
        return super.onOptionsItemSelected(item);
    }

    // Tìm và ánh xạ view
    private void init(){
        favoritesRecyclerView = findViewById(R.id.recyler_favorite_recipes);
        emptyFavoritesTextView = findViewById(R.id.empty_favorites_text_view);

        favoritesRecyclerView.setHasFixedSize(true);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        listFav = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(listFav, FavoritesActivity.this, recipeClickedListener);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        toolbar = findViewById(R.id.toolbarFav);
        // Thiết lập toolbar cho màn hình
        setSupportActionBar(toolbar);
        // Hiển thị nút "Back" trên thanh công cụ
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
    }

    private void showAddRecipeDialog() {
        // Hiển thị dialog để người dùng nhập liệu và thêm mới món ăn
        // Tạo dialog builder
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setTitle("Thêm mới");

        // Tạo layout cho dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_recipe, null);
        EditText titleEditText = view.findViewById(R.id.edt_dialogFav_title);
        EditText imageEditText = view.findViewById(R.id.edt_dialogFav_image);
        EditText sourceNameEditText = view.findViewById(R.id.edt_dialogFav_source);
        EditText summaryEditText = view.findViewById(R.id.edt_dialogFav_summary);
        RecyclerView ingredientsRecyclerView = view.findViewById(R.id.recyler_ingredients_fav);
        RecyclerView instructionsRecyclerView = view.findViewById(R.id.recyler_instructions_fav);

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(this, new ArrayList<>());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Thêm listener cho nút "Add Ingredient"
        Button addIngredientButton = view.findViewById(R.id.button_add_ingredient);
        addIngredientButton.setOnClickListener(v -> showAddIngredientDialog(ingredientsAdapter));

//        instructionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        InstructionsAdapter instructionsAdapter = new InstructionsAdapter(this, new ArrayList<>());
//        instructionsRecyclerView.setAdapter(instructionsAdapter);

        // Thiết lập layout cho dialog
        myBuilder.setView(view);
        // Thêm các nút "Cancel" và "Add"

        myBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        myBuilder.setPositiveButton("Add", (dialog, which) -> {
            // Lấy giá trị từ các EditText và tạo đối tượng RecipeDetailsResponse mới
            int id = generateId();

            String title = titleEditText.getText().toString();
            String image= imageEditText.getText().toString();
            String sourceName = sourceNameEditText.getText().toString();
            String summary = summaryEditText.getText().toString()
                    ;
            ArrayList<ExtendedIngredient> ingredients = ingredientsAdapter.getIngredient();

            RecipeDetailsResponse recipe = new RecipeDetailsResponse(id, title, image, sourceName, ingredients, null, summary, "");

            // Thêm mới món ăn vào Firebase Realtime Database
            FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();
            databaseHelper.addRecipe(recipe);

            dialog.dismiss();
        });
        // Hiển thị dialog
        AlertDialog myDialog = myBuilder.create();
        myDialog.show();
    }
    private void showAddIngredientDialog(IngredientsAdapter ingredientsAdapter)  {
        // Tạo dialog builder
        int id = generateId();

        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setTitle("Thêm nguyên liệu");
        // Tạo cho dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ingredient, null);
        EditText nameEditText = view.findViewById(R.id.edt_dialogIngredient_name);
        EditText imageEditText = view.findViewById(R.id.edt_dialogIngredient_image);
        EditText originalEditText = view.findViewById(R.id.edt_dialogIngredient_original);
        // Thiết lập layout cho dialog
        myBuilder.setView(view);
        // Thêm các nút "Cancel" và "Add"
        myBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        myBuilder.setPositiveButton("Add", (dialog, which) -> {
            // Lấy giá trị từ các EditText và tạo đối tượng ExtendedIngredient mới
            String name = nameEditText.getText().toString();
            String original = originalEditText.getText().toString();
            String image = imageEditText.getText().toString();
            ExtendedIngredient ingredient = new ExtendedIngredient(id, image, name, original);
            // Thêm mới nguyên liệu vào adapter
            ingredientsAdapter.addIngredient(ingredient);
            dialog.dismiss();
        });
        // Hiển thị myDialog
        AlertDialog myDialog = myBuilder.create();
        myDialog.show();
    }

    private final RecipeClickedListener recipeClickedListener = id ->{
        // Một Intent được tạo để chuyển hướng người dùng đến màn hình chi tiết công thức nấu ăn
        Intent intent = new Intent(FavoritesActivity.this, RecipeDetailActivity.class);
        // Các thông tin về công thức nấu ăn được truyền qua Intent
        // để hỗ trợ xử lý trên màn hình chi tiết RecipeDetailActivity
        intent.putExtra("fromFav", true);
        intent.putExtra("id", id);
        startActivity(intent);
    };

    //Tạo phương thức generateId để tạo một ID ngẫu nhiên
    private int generateId() {
        // Tạo một đối tượng UUID
        UUID uuid = UUID.randomUUID();
        // Mã băm của nó được trả về dưới dạng một số nguyên
        return uuid.hashCode();
    }
    private void onClickReadData(){
        //Lấy dữ liệu từ Firebase RealtimeDatabase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //  Tham chiếu đến nút "recipes" trong cơ sở dữ liệu
        databaseReference.child("recipes").addValueEventListener(new ValueEventListener() {
            // Lắng nghe sự kiện khi dữ liệu thay đổi
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Danh sách công thức nấu ăn yêu thích được làm mới
                listFav.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    // Công thức nấu ăn mới được thêm vào danh sách
                    // từ dữ liệu được lấy từ Firebase Realtime Database
                    RecipeDetailsResponse fav = dataSnapshot.getValue(RecipeDetailsResponse.class);
                    listFav.add(fav);
                }
                if(listFav.size()==0)
                    // Danh sách rỗng
                    emptyFavoritesTextView.setVisibility(View.VISIBLE);
                else
                    // Nếu danh sách công thức nấu ăn yêu thích không rỗng
                    emptyFavoritesTextView.setVisibility(View.GONE);
                // Cập nhật lại danh sách công thức nấu ăn yêu thích trong RecyclerView
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // err
            }
        });
    }
}