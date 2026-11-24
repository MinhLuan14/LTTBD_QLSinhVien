package vhu.edu.edusync.ui.admin.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.main.MainActivity;
import vhu.edu.edusync.ui.auth.LoginActivity;
import vhu.edu.edusync.ui.student.StudentMainActivity;

/**
 * Activity Khởi động (Splash Screen):
 * - Giới thiệu ứng dụng EduSync
 * - Kiểm tra phiên đăng nhập tự động
 * - Điều hướng theo vai trò (ADMIN -> MainActivity, SINHVIEN -> StudentMainActivity, Chưa đăng nhập -> LoginActivity)
 */
public class SplashActivity extends AppCompatActivity {

    private Button btnBatDau;
    private View splashContainer;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        // Kiểm tra xem đã có phiên đăng nhập hợp lệ chưa
        if (sessionManager.isLoggedIn()) {
            chuyenTrangTheoVaiTro();
            return;
        }

        btnBatDau = findViewById(R.id.btnBatDau);
        splashContainer = findViewById(R.id.splashContainer);

        View.OnClickListener startLoginListener = v -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        };

        if (btnBatDau != null) {
            btnBatDau.setOnClickListener(startLoginListener);
        }

        if (splashContainer != null) {
            splashContainer.setOnClickListener(startLoginListener);
        }
    }

    private void chuyenTrangTheoVaiTro() {
        Intent intent;
        if (sessionManager.isAdmin()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, StudentMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
