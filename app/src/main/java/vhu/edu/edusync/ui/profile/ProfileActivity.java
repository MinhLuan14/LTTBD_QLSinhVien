package vhu.edu.edusync.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.model.TaiKhoan;
import vhu.edu.edusync.data.repository.SinhVienRepository;
import vhu.edu.edusync.data.repository.TaiKhoanRepository;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.main.MainActivity;
import vhu.edu.edusync.ui.auth.ChangePasswordActivity;
import vhu.edu.edusync.ui.auth.LoginActivity;

/**
 * Trang cá nhân dùng chung cho cả vai trò ADMIN và SINHVIEN
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView imgBack;
    private ImageView imgProfileAvatar;
    private TextView tvProfileDisplayName;
    private TextView tvProfileRoleBadge;
    private TextView tvProfileStatusBadge;

    private TextView tvProfileUsername;
    private TextView tvProfileAccountId;

    private MaterialCardView cardStudentDetails;
    private TextView tvProfileStudentId;
    private TextView tvProfileStudentClass;
    private TextView tvProfileStudentDob;
    private TextView tvProfileStudentGender;
    private TextView tvProfileStudentEmail;
    private TextView tvProfileStudentPhone;
    private TextView tvProfileStudentAddress;

    private MaterialCardView cardAdminDetails;

    private MaterialButton btnChangePassword;
    private MaterialButton btnLogout;

    private SessionManager sessionManager;
    private TaiKhoanRepository taiKhoanRepository;
    private SinhVienRepository sinhVienRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            chuyenVeDangNhap();
            return;
        }

        taiKhoanRepository = new TaiKhoanRepository(this);
        sinhVienRepository = new SinhVienRepository(this);

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        tvProfileDisplayName = findViewById(R.id.tvProfileDisplayName);
        tvProfileRoleBadge = findViewById(R.id.tvProfileRoleBadge);
        tvProfileStatusBadge = findViewById(R.id.tvProfileStatusBadge);

        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileAccountId = findViewById(R.id.tvProfileAccountId);

        cardStudentDetails = findViewById(R.id.cardStudentDetails);
        tvProfileStudentId = findViewById(R.id.tvProfileStudentId);
        tvProfileStudentClass = findViewById(R.id.tvProfileStudentClass);
        tvProfileStudentDob = findViewById(R.id.tvProfileStudentDob);
        tvProfileStudentGender = findViewById(R.id.tvProfileStudentGender);
        tvProfileStudentEmail = findViewById(R.id.tvProfileStudentEmail);
        tvProfileStudentPhone = findViewById(R.id.tvProfileStudentPhone);
        tvProfileStudentAddress = findViewById(R.id.tvProfileStudentAddress);

        cardAdminDetails = findViewById(R.id.cardAdminDetails);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> {
            if (isTaskRoot()) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> hienThiDialogDangXuat());
    }

    private void loadProfileData() {
        String username = sessionManager.getUsername();
        TaiKhoan taiKhoan = taiKhoanRepository.getByUsername(username);

        if (taiKhoan == null) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
            return;
        }

        tvProfileUsername.setText(taiKhoan.getTenDangNhap());
        tvProfileAccountId.setText(taiKhoan.getMaTaiKhoan());

        if (taiKhoan.isActive()) {
            tvProfileStatusBadge.setText("Hoạt động");
            tvProfileStatusBadge.setBackgroundResource(R.drawable.bg_badge_active);
            tvProfileStatusBadge.setTextColor(getResources().getColor(R.color.success));
        } else {
            tvProfileStatusBadge.setText("Đã khóa");
            tvProfileStatusBadge.setBackgroundResource(R.drawable.bg_badge_locked);
            tvProfileStatusBadge.setTextColor(getResources().getColor(R.color.error));
        }

        if (taiKhoan.isAdmin()) {
            // Hiển thị giao diện dành riêng cho Admin
            tvProfileDisplayName.setText("Quản trị viên");
            tvProfileRoleBadge.setText("ADMIN");
            tvProfileRoleBadge.setBackgroundResource(R.drawable.bg_chip_selected);
            imgProfileAvatar.setImageResource(R.drawable.ic_person);

            cardStudentDetails.setVisibility(View.GONE);
            cardAdminDetails.setVisibility(View.VISIBLE);
        } else {
            // Hiển thị giao diện dành riêng cho Sinh viên
            tvProfileRoleBadge.setText("SINH VIÊN");
            tvProfileRoleBadge.setBackgroundResource(R.drawable.bg_circle_blue);
            imgProfileAvatar.setImageResource(R.drawable.ic_student);

            cardAdminDetails.setVisibility(View.GONE);
            cardStudentDetails.setVisibility(View.VISIBLE);

            String maSV = taiKhoan.getMaSinhVien();
            if (maSV != null && !maSV.isEmpty()) {
                SinhVien sv = sinhVienRepository.getById(maSV);
                if (sv != null) {
                    tvProfileDisplayName.setText(sv.getHoTen());
                    tvProfileStudentId.setText(sv.getMaSinhVien());
                    tvProfileStudentClass.setText(sv.getTenLop() != null ? sv.getTenLop() : (sv.getMaLop() != null ? sv.getMaLop() : "--"));
                    tvProfileStudentDob.setText(sv.getNgaySinh() != null ? sv.getNgaySinh() : "--");
                    tvProfileStudentGender.setText(sv.getGioiTinh() != null ? sv.getGioiTinh() : "--");
                    tvProfileStudentEmail.setText(sv.getEmail() != null ? sv.getEmail() : "--");
                    tvProfileStudentPhone.setText(sv.getSoDienThoai() != null ? sv.getSoDienThoai() : "--");
                    tvProfileStudentAddress.setText(sv.getDiaChi() != null ? sv.getDiaChi() : "--");
                } else {
                    tvProfileDisplayName.setText(taiKhoan.getTenDangNhap());
                    tvProfileStudentId.setText(maSV);
                }
            } else {
                tvProfileDisplayName.setText(taiKhoan.getTenDangNhap());
                tvProfileStudentId.setText("--");
            }
        }
    }

    private void hienThiDialogDangXuat() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                    chuyenVeDangNhap();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void chuyenVeDangNhap() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taiKhoanRepository != null) {
            taiKhoanRepository.close();
        }
    }
}
