package ntu.nthuy.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIMEOUT = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Thực hiện tác vụ sau khi delay
                Intent intent = new Intent(LauncherActivity.this, FavoritesActivity.class);
                startActivity(intent);
                finish(); // Kết thúc `LauncherActivity` để ngăn không cho người dùng quay lại
                // màn hình này bằng cách sử dụng nút back.
            }

        }, SPLASH_SCREEN_TIMEOUT);
    }
}