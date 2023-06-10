package ntu.nthuy.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Adapters.RandomRecipeAdapter;
import ntu.nthuy.recipeapp.Listeners.RandomRecipeResponseListener;
import ntu.nthuy.recipeapp.Listeners.RecipeClickedListener;
import ntu.nthuy.recipeapp.Model.RandomRecipeApiResponse;

public class MainActivity extends AppCompatActivity {
    // Định nghĩa builderDialog và dialog để tạo các hộp thoại tương tác
    AlertDialog.Builder builderDialog;
    AlertDialog dialog;
    // Sử dụng lớp RequestManager đã được định nghĩa để gọi các phương thức xử lý đối với API
    RequestManager myManager;
    // Sử dụng RandomRecipeAdapter để xử lý việc hiển thị các công thức nấu ăn ngẫu nhiên trong RecyclerView
    RandomRecipeAdapter randomAdapter;
    // Thành phần các công thức nấu ăn ngẫu nhiên
    RecyclerView rclViewRandom;
    // Một nút favoritesButton hình ảnh trên màn hình để chuyển đến màn hình Favorites Acitvity
    ImageButton favoritesTouch;
    // Thả xuống những Tags
    Spinner dropTags;
    // Một danh sách chuỗi: lưu trữ các thẻ để lọc công thức nấu ăn
    List<String> listTags = new ArrayList<>();
    // Thanh tìm công thức nấu ăn
    SearchView searchViewHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Tạo một hộp thoại (AlertDialog) "Loading"
        // khi đang tải dữ liệu
        builderDialog = new AlertDialog.Builder(this);
        builderDialog.setMessage("Loading...");
        builderDialog.setCancelable(false);
        dialog = builderDialog.create();

        searchViewHome = findViewById(R.id.searchView_home);
        searchViewHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listTags.clear();
                listTags.add(query);
                myManager.getRandomRecipes(randomResponseListener, listTags);
                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Thiết lập favoritesButton để chuyển hướng người dùng đến màn hình chứa các danh sách
        // món ăn yêu thích của người dùng khi thực hiện thao tác nhấn
        favoritesTouch = findViewById(R.id.favorite_button);
        favoritesTouch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FavoritesActivity.class)));

        // Thiết lập Spinner để cho phép người dùng lọc công thức nấu ăn theo các tags được cung cấp
        dropTags = findViewById(R.id.drop_tags);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.txt_spinner
        );
        // Các tags được lưu trữ trong tài nguyên chuỗi (string resource)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);

        // Khi người dùng chọn một tag,
        // danh sách các công thức nấu ăn sẽ được lọc dựa trên tag và hiển thị trong RecyclerView.
        dropTags.setAdapter(arrayAdapter);
        dropTags.setOnItemSelectedListener(spinnerSelectedListenner);

        //Tạo một đối tượng RequestManager để quản lý các yêu cầu API và xử lý dữ liệu trả về
        myManager = new RequestManager(this);
    }

    // Đoạn mã này khai báo một đối tượng RandomRecipeResponseListener
    // để lắng nghe các phản hồi từ API khi yêu cầu danh sách công thức nấu ăn ngẫu nhiên.
    private final RandomRecipeResponseListener randomResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void fetch(RandomRecipeApiResponse response, String message) {
            // Hộp thoại thông báo "Loading..." được ẩn đi
            dialog.dismiss();

            // Thiết lập RecyclerView để hiển thị danh sách các công thức nấu ăn
            rclViewRandom = findViewById(R.id.recylerView_random);
            rclViewRandom.setHasFixedSize(true);
            rclViewRandom.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            // Tạo đối tượng để hiển thị các công thức nấu ăn trong RecyclerView
            randomAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickedListener);
            rclViewRandom.setAdapter(randomAdapter);
        }

        @Override
        public void err(String message) {
            // Err
        }
    };

    // Khai báo một đối tượng AdapterView.OnItemSelectedListener
    // để lắng nghe sự kiện khi người dùng chọn một tag từ Spinner
    private final AdapterView.OnItemSelectedListener spinnerSelectedListenner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // Xóa danh sách tags
            listTags.clear();
            // Thêm tag được chọn vào danh sách tags để lọc công thức nấu ăn
            listTags.add(adapterView.getSelectedItem().toString());
            // Manager sẽ gọi phương thức getRandomRecipes
            // để lấy danh sách các công thức nấu ăn dựa trên tags
            myManager.getRandomRecipes(randomResponseListener, listTags);
            // và hiển thị hộp thoại thông báo "Loading..."
            dialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Khi không có tag nào được chọn
            // no action
        }
    };

    // Khai báo một đối tượng RecipeClickedListener
    // để lắng nghe công thức nấu ăn trong RecyclerView được chọn
    private final RecipeClickedListener recipeClickedListener = id -> {
        // Một Intent được tạo để chuyển hướng người dùng đến màn hình chi tiết công thức nấu ăn
        Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        // Các thông tin về công thức nấu ăn được truyền qua Intent
        // để hỗ trợ xử lý trên màn hình chi tiết RecipeDetailActivity
        intent.putExtra("fromMain", true);
        intent.putExtra("id", id);
        startActivity(intent);
    };
}