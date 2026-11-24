package vhu.edu.edusync.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.TaiKhoan;
import vhu.edu.edusync.data.repository.TaiKhoanRepository;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.main.MainActivity;
import vhu.edu.edusync.ui.student.StudentMainActivity;

/**
 * Activity Đăng nhập hệ thống EduSync:
 * - Kiểm tra thông tin đăng nhập và trạng thái tài khoản
 * - Phân quyền điều hướng theo vai trò: ADMIN -> MainActivity, SINHVIEN -> StudentMainActivity
 * - Chặn tài khoản bị khóa đăng nhập
 * - Hỗ trợ ghi nhớ đăng nhập và ẩn/hiện mật khẩu
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ImageView btnTogglePassword;
    private CheckBox cbRememberLogin;
    private TextView tvForgotPassword;
    private TextView tvLoginNotification;
    private MaterialButton btnLogin;

    private TaiKhoanRepository taiKhoanRepository;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        taiKhoanRepository = new TaiKhoanRepository(this);
        sessionManager = new SessionManager(this);

        anhXa();
        caiDatGiaoDienBanDau();
        xuLySuKien();
    }

    private void anhXa() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        cbRememberLogin = findViewById(R.id.cbRememberLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvLoginNotification = findViewById(R.id.tvLoginNotification);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void caiDatGiaoDienBanDau() {
        // Tự động điền tên đăng nhập nếu đã ghi nhớ trước đó
        if (sessionManager.isRememberLogin()) {
            cbRememberLogin.setChecked(true);
            String savedUser = sessionManager.getSavedUsername();
            if (savedUser != null && !savedUser.isEmpty()) {
                edtUsername.setText(savedUser);
                edtPassword.requestFocus();
            }
        }
    }

    private void xuLySuKien() {
        // 1. Nút Ẩn/Hiện mật khẩu
        if (btnTogglePassword != null) {
            btnTogglePassword.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility);
                } else {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility);
                }
                edtPassword.setSelection(edtPassword.getText().length());
            });
        }

        // 2. Nút Đăng nhập
        btnLogin.setOnClickListener(v -> thucHienDangNhap());

        // 3. Quên mật khẩu
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> hienThiQuenMatKhau());
        }
    }

    private void thucHienDangNhap() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Ẩn thông báo lỗi cũ
        anThongBaoLoi();

        // Kiểm tra để trống
        if (username.isEmpty()) {
            hienThiThongBaoLoi("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            hienThiThongBaoLoi("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        // 1. Kiểm tra tài khoản có tồn tại không
        TaiKhoan tkExist = taiKhoanRepository.getByUsername(username);
        if (tkExist == null) {
            hienThiThongBaoLoi("Tên đăng nhập không tồn tại trong hệ thống");
            edtUsername.requestFocus();
            return;
        }

        // 2. Kiểm tra mật khẩu
        TaiKhoan tkAuthenticated = taiKhoanRepository.kiemTraDangNhap(username, password);
        if (tkAuthenticated == null) {
            hienThiThongBaoLoi("Mật khẩu không chính xác. Vui lòng thử lại");
            edtPassword.requestFocus();
            return;
        }

        // 3. Kiểm tra trạng thái tài khoản (1: Hoạt động, 0: Bị khóa)
        if (tkAuthenticated.isLocked()) {
            hienThiThongBaoLoi("Tài khoản của bạn đã bị KHÓA. Vui lòng liên hệ quản trị viên!");
            return;
        }

        // 4. Lưu phiên đăng nhập
        boolean remember = cbRememberLogin.isChecked();
        sessionManager.saveSession(tkAuthenticated, remember);

        // 5. Phân quyền và điều hướng theo vai trò
        dieuHuongTheoVaiTro(tkAuthenticated);
    }

    /**
     * Điều hướng đến Activity phù hợp theo vai trò người dùng
     */
    private void dieuHuongTheoVaiTro(TaiKhoan tk) {
        Intent intent;
        String tenNguoiDung = (tk.getHoTenSinhVien() != null && !tk.getHoTenSinhVien().isEmpty())
                ? tk.getHoTenSinhVien() : tk.getTenDangNhap();

        if (tk.isAdmin()) {
            Toast.makeText(this, "Xin chào Quản trị viên: " + tenNguoiDung, Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else {
            Toast.makeText(this, "Xin chào Sinh viên: " + tenNguoiDung, Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, StudentMainActivity.class);
        }

        // Xóa sạch ngăn xếp Activity để người dùng không bấm Back quay lại Login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void hienThiThongBaoLoi(String message) {
        if (tvLoginNotification != null) {
            tvLoginNotification.setText(message);
            tvLoginNotification.setVisibility(View.VISIBLE);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void anThongBaoLoi() {
        if (tvLoginNotification != null) {
            tvLoginNotification.setVisibility(View.GONE);
        }
    }

    private void hienThiQuenMatKhau() {
        new AlertDialog.Builder(this)
                .setTitle("Quên mật khẩu")
                .setMessage("Nếu bạn là Sinh viên, vui lòng liên hệ Ban Quản trị hoặc Phòng Đào tạo để được cấp lại mật khẩu.\n\nTài khoản quản trị mặc định: admin / admin123")
                .setPositiveButton("Đã hiểu", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taiKhoanRepository != null) {
            taiKhoanRepository.close();
        }
    }
}
