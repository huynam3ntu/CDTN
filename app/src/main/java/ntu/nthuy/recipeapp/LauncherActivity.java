package ntu.nthuy.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIMEOUT = 3000; // 3 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Thực hiện tác vụ sau khi delay
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish(); // Kết thúc để ngăn không cho người dùng quay lại
                // màn hình này bằng cách sử dụng nút back.
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}