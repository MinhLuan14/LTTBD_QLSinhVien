package vhu.edu.edusync.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.TaiKhoan;
import vhu.edu.edusync.data.repository.TaiKhoanRepository;
import vhu.edu.edusync.data.session.SessionManager;

/**
 * Activity đổi mật khẩu dành cho người dùng hiện tại (cả ADMIN và SINHVIEN)
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextInputEditText edtCurrentPassword;
    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmNewPassword;
    private MaterialButton btnSavePassword;

    private SessionManager sessionManager;
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Xử lý status bar insets để header không bị chìm dưới thanh trạng thái
        View llHeader = findViewById(R.id.llHeader);
        if (llHeader != null) {
            ViewCompat.setOnApplyWindowInsetsListener(llHeader, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        taiKhoanRepository = new TaiKhoanRepository(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> finish());
        btnSavePassword.setOnClickListener(v -> xuLyDoiMatKhau());
    }

    private void xuLyDoiMatKhau() {
        String currentPass = edtCurrentPassword.getText() != null ? edtCurrentPassword.getText().toString().trim() : "";
        String newPass = edtNewPassword.getText() != null ? edtNewPassword.getText().toString().trim() : "";
        String confirmPass = edtConfirmNewPassword.getText() != null ? edtConfirmNewPassword.getText().toString().trim() : "";

        if (currentPass.isEmpty()) {
            edtCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại!");
            edtCurrentPassword.requestFocus();
            return;
        }

        if (newPass.length() < 6) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự!");
            edtNewPassword.requestFocus();
            return;
        }

        if (newPass.equals(currentPass)) {
            edtNewPassword.setError("Mật khẩu mới không được trùng với mật khẩu hiện tại!");
            edtNewPassword.requestFocus();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            edtConfirmNewPassword.setError("Mật khẩu xác nhận không khớp!");
            edtConfirmNewPassword.requestFocus();
            return;
        }

        String username = sessionManager.getUsername();

        // 1. Kiểm tra mật khẩu hiện tại có đúng không
        TaiKhoan currentAccount = taiKhoanRepository.kiemTraDangNhap(username, currentPass);
        if (currentAccount == null) {
            edtCurrentPassword.setError("Mật khẩu hiện tại không chính xác!");
            edtCurrentPassword.requestFocus();
            return;
        }

        // 2. Cập nhật mật khẩu mới
        boolean success = taiKhoanRepository.doiMatKhau(username, currentPass, newPass);
        if (success) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taiKhoanRepository != null) {
            taiKhoanRepository.close();
        }
    }
}
