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
    // Một số nguyên lưu trữ ID của công thức nấu ăn được chọn để xem chi tiết
    int id;
    // Các thành phần công thức nấu ăn
    TextView txtView_detail_title, txtView_detail_source, txtView_detail_summary;
    ImageView imgView_detail_image;
    // Hiện nguyên liệu, công thức nấu ăn tương tự, và hướng dẫn nấu ăn
    RecyclerView recyler_detail_ingredient, recyler_detail_similar, recyler_detail_instruction;
    // Sử dụng lớp RequestManager đã được định nghĩa để gọi các phương thức xử lý đối với API
    RequestManager manager;
    // Hiển thị hộp thoại
    AlertDialog.Builder builderDialog;
    AlertDialog myDialog;
    // Các lớp adapter tùy chỉnh để xử lý việc hiển thị danh sách các thành phần,
    // Công thức nấu ăn tương tự, và hướng dẫn nấu ăn cho công thức được chọn
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipesAdapter similarRecipesAdapter;
    InstructionsAdapter instructionsAdapter;
    // Các ImageButton để cho phép người dùng thêm, sửa, xóa
    ImageButton favoriteButton, delButton;
    // Sử dụng FirebaseDatabaseHelper đã được định nghĩa,
    // để thêm và xóa công thức nấu ăn khỏi danh sách yêu thích
    FirebaseDatabaseHelper myData;
    // một giao diện (interface) để lắng nghe sự kiện khi người dùng
    // thêm hoặc xóa công thức nấu ăn khỏi danh sách yêu thích
    private RecipeFavoriteDetailsListener recipeFavoriteDetailsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Gọi phương thức findViews
        // Tìm và ánh xạ views
        findViews();
        // Một số nguyên lưu trữ ID của công thức nấu ăn được chọn để xem chi tiết,
        // được lấy từ Intent
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        // Biến boolean xác định xem công thức nấu ăn được chọn để xem chi tiết
        // có từ màn hình chính MainActivity hay từ màn hình yêu thích FavoritesActivity
        boolean fromMain = getIntent().getBooleanExtra("fromMain", false);

        if(fromMain){
            // Nếu công thức nấu ăn được chọn để xem chi tiết từ màn hình chính
            Toast.makeText(RecipeDetailActivity.this, "Công thức mới!"
                    , Toast.LENGTH_SHORT).show();
            delButton.setVisibility(View.GONE); //Ẩn nút deleteButton
            //Tạo một đối tượng RequestManager để quản lý các yêu cầu API
            // và xử lý dữ liệu trả về
            manager = new RequestManager(this);
            // Tải thông tin chi tiết công thức nấu ăn được chọn
            manager.getRecipeDetails(detailsListener, id);
            // Tải danh sách các công thức nấu ăn tương tự
            manager.getSimilar(similarsListener, id);
            // Tải hướng dẫn nấu ăn cho công thức được chọn
            manager.getInstructions(instructionsListener, id);
        } else {
            // Tải thông tin chi tiết của công thức nấu ăn từ Firebase Realtime Database
            loadRecipeDetails();
            // Lắng nghe sự kiện khi người dùng thêm hoặc xóa công thức nấu ăn
            // khỏi danh sách yêu thích
            setRecipeFavoriteDetailsListener(recipeFavoriteDetailsListener);
            // Hiển thị thông tin chi tiết của công thức nấu ăn
            showDetails();
        }
        // Hiển thị một hộp thoại thông báo khi đang tải thông tin chi tiết của công thức nấu ăn
        builderDialog = new AlertDialog.Builder(this);
        builderDialog.setMessage("Loading Details...");
        builderDialog.setCancelable(false);
        myDialog = builderDialog.create();
        myDialog.show();
    }
    private void loadRecipeDetails() {
        // truy cập vào nút "recipes"
        // và lấy công thức nấu ăn có ID tương ứng với id
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(String.valueOf(id));
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            // Lắng nghe sự kiện khi dữ liệu được lấy từ Firebase Realtime Database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tạo đối tượng RecipeDetailsResponse lấy từ Firebase Realtime Database
                RecipeDetailsResponse recipeDetailsResponse = snapshot.getValue(RecipeDetailsResponse.class);
                if (recipeDetailsResponse != null) {
                    // Nếu có đối tượng trả về, thì sẽ truyền đối tượng cho bộ lắng nghe
                    recipeFavoriteDetailsListener.fetch(recipeDetailsResponse);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // err
            }
        });
    }
    private void showDetails() {
        setRecipeFavoriteDetailsListener(response -> {
            myDialog.dismiss(); // Ẩn hộp thoại
            delButton.setVisibility(View.VISIBLE); //Hiện nút deleteButton
            // Hiển thị nút Edit cho phép người dùng chỉnh sửa
            favoriteButton.setImageResource(R.drawable.ic_edit_button);
            // Hiển thị tên món ăn
            txtView_detail_title.setText(response.title);
            // Hiển thị nội dung giới thiệu món ăn
            Document doc = Jsoup.parse(response.summary);
            String rpsummary = doc.text();
            txtView_detail_summary.setText(rpsummary);
            // Hiển thị nguồn
            txtView_detail_source.setText(response.sourceName);
            // Hiển thị ảnh món ăn
            Picasso.get().load(response.image).into(imgView_detail_image);

            //Hiển thị dữ liệu nguyên lịệu ingredients lên Recycler View
            recyler_detail_ingredient.setHasFixedSize(true);
            recyler_detail_ingredient.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recyler_detail_ingredient.setAdapter(ingredientsAdapter);

            //Hiển thị dữ liệu lên Recycler View -- đang bị lỗi
//            recyler_detail_instruction.setHasFixedSize(true);
//            recyler_detail_instruction.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.VERTICAL, false));
//            instructionsAdapter = new InstructionsAdapter(RecipeDetailActivity.this, response.instructionsReponses);
//            recyler_detail_instruction.setAdapter(instructionsAdapter);

            // Khi người dùng nhấn nút Edit
            favoriteButton.setOnClickListener(v -> {
                // Tạo chuyển hướng người dùng đến EditDetailsRecipeActivity
                Intent myIntent = new Intent(RecipeDetailActivity.this
                        , EditDetailsRecipeActivity.class);
                // Các thông tin được truyền qua Intent
                // để hỗ trợ xử lý trên màn hình EditDetailsRecipeActivity
                myIntent.putExtra("fromDetailFav", true);
                myIntent.putExtra("recipeId", String.valueOf(id));
                startActivity(myIntent);
            });
            // Khi người dùng nhấn nút Delete
            delButton.setOnClickListener(v -> {
                // Hiển thị hộp thoại nhắc nhở, xác nhận hành động xóa
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(RecipeDetailActivity.this);
                myBuilder.setMessage("Muốn xóa công thức này thật chứ?");
                myBuilder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa đối tượng RecipeDetailsResponse
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("recipes").child(String.valueOf(id));
                        myRef.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(RecipeDetailActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish(); // Trở lại màn hình trước
                    }
                });
                myBuilder.setNegativeButton("Hủy", null);
                AlertDialog alertDialog = myBuilder.create();
                alertDialog.show();
            });
        });
    }
    //
    public void setRecipeFavoriteDetailsListener(RecipeFavoriteDetailsListener listener) {
        this.recipeFavoriteDetailsListener = listener;
    }
    // Tìm và ánh xạ
    private void findViews() {
        txtView_detail_title = findViewById(R.id.txtView_detail_title);
        txtView_detail_source = findViewById(R.id.txtView_detail_source);
        txtView_detail_summary = findViewById(R.id.txtView_detail_summary);
        imgView_detail_image = findViewById(R.id.imgView_detail_image);
        recyler_detail_ingredient = findViewById(R.id.recyler_detail_ingredient);
        recyler_detail_similar = findViewById(R.id.recyler_detail_similar);
        recyler_detail_instruction = findViewById(R.id.recyler_detail_instruction);
        favoriteButton = findViewById(R.id.favorite_button);
        delButton = findViewById(R.id.favorite_delButton);

        myData = new FirebaseDatabaseHelper();
    }

    // Các bộ lắng nghe dữ liệu API
    private final RecipeDetailsListener detailsListener = new RecipeDetailsListener() {
        @Override
        public void fetch(RecipeDetailsResponse response, String message) {
            myDialog.dismiss(); // Ẩn hộp thoại loading
            // Thiết lập ảnh cho nút favoriteButton
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            // Lấy dữ liệu từ API
            // tên
            txtView_detail_title.setText(response.title);
            // Hiển thị nội dung giới thiệu
            Document doc = Jsoup.parse(response.summary);
            String rpsummary = doc.text();
            txtView_detail_summary.setText(rpsummary);
            // Hiển thị nguồn
            txtView_detail_source.setText(response.sourceName);
            // Hiển thị ảnh món ăn
            Picasso.get().load(response.image).into(imgView_detail_image);
            // Hiển thị danh sách các nguyên liệu
            recyler_detail_ingredient.setHasFixedSize(true);
            recyler_detail_ingredient.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailActivity.this, response.extendedIngredients);
            recyler_detail_ingredient.setAdapter(ingredientsAdapter);
            // Thiết lập khi người dùng nhất nút favoriteButton
            favoriteButton.setOnClickListener(v -> {
                // Thiết lập ảnh favoriteButton đổi sang trạng thái "đã thích"
                favoriteButton.setImageResource(R.drawable.ic_favorite);
                // Thêm món ăn vào danh sách yêu thích
                RecipeDetailsResponse favR = new RecipeDetailsResponse(response.id, response.title, response.image, response.sourceName, response.extendedIngredients, new ArrayList<>(),response.summary, "");
                // Sử dụng phương thức addRecipe trong lớp FirebaseDatabaseHelper
                // để thêm món ăn vào danh sách yêu thích trên Firebase Realtime Database
                myData.addRecipe(favR);
                //Hiển thị thông báo
                Toast.makeText(RecipeDetailActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void err(String message) {
            // Khi dữ liệu API không trả về được

        }
    };
    // Đối tượng similarRecipesListener lắng nghe sự kiện
    // khi danh sách các công thức nấu ăn tương tự được tải từ API
    private final SimilarRecipesListener similarsListener = new SimilarRecipesListener() {
        @Override
        public void fetch(List<SimilarRecipesResponse> response, String message) {
            // Tải thành công từ API
            // Thiết lập để hiển thị danh sách các công thức nấu ăn tương tự
            recyler_detail_similar.setHasFixedSize(true);
            recyler_detail_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipesAdapter = new SimilarRecipesAdapter(RecipeDetailActivity.this, response, recipeClickedListener);
            recyler_detail_similar.setAdapter(similarRecipesAdapter);
        }

        @Override
        public void err(String message) {
            //Khi err
        }
    };
    // Đối tượng instructionsListener lắng nghe sự kiện
    // khi hướng dẫn nấu ăn của công thức được tải từ API
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void fetch(ArrayList<InstructionsReponse> reponse, String message) {
            // Tải thành công từ API
            myData.addInstructions(id, reponse);
            //Hiển thị dữ liệu lên Recycler View
            recyler_detail_instruction.setHasFixedSize(true);
            recyler_detail_instruction.setLayoutManager(new LinearLayoutManager(RecipeDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailActivity.this, reponse);
            recyler_detail_instruction.setAdapter(instructionsAdapter);
        }
        @Override
        public void err(String message) {
            // Khi err
        }
    };
    // Đối tượng recipeClickedListener lắng nghe các công thức nấu ăn tương tự
    //  trên màn hình xem chi tiết công thức nấu ăn
    private final RecipeClickedListener recipeClickedListener = id -> startActivity(new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class)
            .putExtra("id", id));
}