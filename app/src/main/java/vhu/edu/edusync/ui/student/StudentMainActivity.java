package vhu.edu.edusync.ui.student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.repository.SinhVienRepository;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.grade.GradeLookupActivity;
import vhu.edu.edusync.ui.auth.ChangePasswordActivity;
import vhu.edu.edusync.ui.auth.LoginActivity;
import vhu.edu.edusync.ui.profile.ProfileActivity;

/**
 * Giao diện Trang chủ dành riêng cho Sinh viên (Vai trò: SINHVIEN):
 * - Hiển thị thông tin cá nhân hóa (Họ tên, MSSV, Lớp)
 * - Tra cứu điểm & kết quả học tập
 * - Đăng ký học phần
 * - Xem hồ sơ cá nhân và đổi mật khẩu
 * - Bảo vệ phân quyền: Ngăn chặn truy cập trái phép
 */
public class StudentMainActivity extends AppCompatActivity {

    private ImageView imgStudentAvatar;
    private ImageView btnLogoutStudent;
    private TextView tvStudentName;
    private TextView tvStudentDetails;

    private MaterialCardView cardStudentViewGrades;
    private MaterialCardView cardStudentRegisterCourse;
    private MaterialCardView cardStudentProfile;
    private MaterialCardView cardStudentChangePassword;
    private MaterialButton btnStudentLogout;

    private SessionManager sessionManager;
    private SinhVienRepository sinhVienRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Kiểm tra phân quyền: Chỉ cho phép SINHVIEN truy cập
        if (!sessionManager.isLoggedIn() || !sessionManager.isSinhVien()) {
            Toast.makeText(this, "Bạn không có quyền truy cập trang sinh viên!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_student_main);
        sinhVienRepository = new SinhVienRepository(this);

        anhXa();
        hienThiThongTinSinhVien();
        xuLySuKien();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hienThiThongTinSinhVien();
    }

    private void anhXa() {
        imgStudentAvatar = findViewById(R.id.imgStudentAvatar);
        btnLogoutStudent = findViewById(R.id.btnLogoutStudent);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentDetails = findViewById(R.id.tvStudentDetails);

        cardStudentViewGrades = findViewById(R.id.cardStudentViewGrades);
        cardStudentRegisterCourse = findViewById(R.id.cardStudentRegisterCourse);
        cardStudentProfile = findViewById(R.id.cardStudentProfile);
        cardStudentChangePassword = findViewById(R.id.cardStudentChangePassword);
        btnStudentLogout = findViewById(R.id.btnStudentLogout);
    }

    private void hienThiThongTinSinhVien() {
        String maSV = sessionManager.getMaSinhVien();
        if (maSV != null && !maSV.isEmpty()) {
            SinhVien sv = sinhVienRepository.getById(maSV);
            if (sv != null) {
                tvStudentName.setText(sv.getHoTen());
                String tenLop = sv.getTenLop() != null ? sv.getTenLop() : (sv.getMaLop() != null ? sv.getMaLop() : "Chưa phân lớp");
                tvStudentDetails.setText("Mã SV: " + sv.getMaSinhVien() + " | Lớp: " + tenLop);

                // Cập nhật tên hiển thị trong session
                sessionManager.updateDisplayName(sv.getHoTen());

                // Hiển thị ảnh đại diện
                if (sv.getAnhDaiDien() != null && !sv.getAnhDaiDien().isEmpty()) {
                    File imgFile = new File(sv.getAnhDaiDien());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if (bitmap != null) {
                            imgStudentAvatar.setImageBitmap(bitmap);
                        }
                    }
                }
                return;
            }
        }

        // Trường hợp tài khoản sinh viên chưa liên kết hồ sơ chi tiết
        tvStudentName.setText(sessionManager.getDisplayName());
        tvStudentDetails.setText("Mã SV: " + (maSV != null ? maSV : "Chưa liên kết"));
    }

    private void xuLySuKien() {
        // 1. Tra cứu điểm học phần
        cardStudentViewGrades.setOnClickListener(v -> {
            Intent intent = new Intent(StudentMainActivity.this, GradeLookupActivity.class);
            startActivity(intent);
        });

        // 2. Đăng ký học phần
        cardStudentRegisterCourse.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đăng ký học phần (Chương phụ trách) đang được kết nối!", Toast.LENGTH_SHORT).show();
        });

        // 3. Hồ sơ cá nhân
        cardStudentProfile.setOnClickListener(v -> {
            Intent intent = new Intent(StudentMainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // 4. Đổi mật khẩu
        cardStudentChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(StudentMainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 5. Đăng xuất
        View.OnClickListener logoutListener = v -> xacNhanDangXuat();
        btnStudentLogout.setOnClickListener(logoutListener);
        btnLogoutStudent.setOnClickListener(logoutListener);
    }

    private void xacNhanDangXuat() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentMainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sinhVienRepository != null) {
            sinhVienRepository.close();
        }
    }
}
