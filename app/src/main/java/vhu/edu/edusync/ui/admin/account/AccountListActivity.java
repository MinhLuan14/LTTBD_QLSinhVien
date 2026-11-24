package vhu.edu.edusync.ui.admin.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.AccountAdapter;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.model.TaiKhoan;
import vhu.edu.edusync.data.repository.TaiKhoanRepository;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.main.MainActivity;

/**
 * Activity quản lý danh sách tài khoản, phân quyền, cấp tài khoản cho sinh viên,
 * khóa/mở khóa và đặt lại mật khẩu.
 */
public class AccountListActivity extends AppCompatActivity implements AccountAdapter.OnAccountActionListener {

    private ImageView imgBack;
    private ImageView imgQuickAdd;
    private TextView tvStatTotal;
    private TextView tvStatActive;
    private TextView tvStatLocked;
    private EditText edtSearchAccount;
    private ImageView imgClearSearch;

    private TextView tvFilterAll;
    private TextView tvFilterAdmin;
    private TextView tvFilterSinhVien;
    private TextView tvFilterLocked;

    private RecyclerView rvAccountList;
    private LinearLayout llEmptyStateAccount;
    private ExtendedFloatingActionButton fabGrantAccount;

    private AccountAdapter accountAdapter;
    private List<TaiKhoan> listTaiKhoan = new ArrayList<>();

    private TaiKhoanRepository taiKhoanRepository;
    private SessionManager sessionManager;

    private String currentKeyword = "";
    private String currentFilterRole = null; // null: all, "ADMIN", "SINHVIEN"
    private Integer currentFilterStatus = null; // null: all, 1: active, 0: locked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

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
        // Kiểm tra phân quyền: Chỉ ADMIN mới được truy cập màn hình này
        if (!sessionManager.isLoggedIn() || !sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền truy cập chức năng này!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        taiKhoanRepository = new TaiKhoanRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadData();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgQuickAdd = findViewById(R.id.imgQuickAdd);
        tvStatTotal = findViewById(R.id.tvStatTotal);
        tvStatActive = findViewById(R.id.tvStatActive);
        tvStatLocked = findViewById(R.id.tvStatLocked);
        edtSearchAccount = findViewById(R.id.edtSearchAccount);
        imgClearSearch = findViewById(R.id.imgClearSearch);

        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterAdmin = findViewById(R.id.tvFilterAdmin);
        tvFilterSinhVien = findViewById(R.id.tvFilterSinhVien);
        tvFilterLocked = findViewById(R.id.tvFilterLocked);

        rvAccountList = findViewById(R.id.rvAccountList);
        llEmptyStateAccount = findViewById(R.id.llEmptyStateAccount);
        fabGrantAccount = findViewById(R.id.fabGrantAccount);
    }

    private void setupRecyclerView() {
        rvAccountList.setLayoutManager(new LinearLayoutManager(this));
        accountAdapter = new AccountAdapter(listTaiKhoan, sessionManager.getUsername(), this);
        rvAccountList.setAdapter(accountAdapter);
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> {
            if (isTaskRoot()) {
                Intent intent = new Intent(AccountListActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        });

        imgQuickAdd.setOnClickListener(v -> hienThiDialogCapTaiKhoan());
        fabGrantAccount.setOnClickListener(v -> hienThiDialogCapTaiKhoan());

        // Lọc tìm kiếm thời gian thực
        edtSearchAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                imgClearSearch.setVisibility(currentKeyword.isEmpty() ? View.GONE : View.VISIBLE);
                locDuLieu();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        imgClearSearch.setOnClickListener(v -> edtSearchAccount.setText(""));

        // Bộ lọc vai trò và trạng thái
        tvFilterAll.setOnClickListener(v -> {
            datTrangThaiChip(tvFilterAll);
            currentFilterRole = null;
            currentFilterStatus = null;
            locDuLieu();
        });

        tvFilterAdmin.setOnClickListener(v -> {
            datTrangThaiChip(tvFilterAdmin);
            currentFilterRole = TaiKhoan.ROLE_ADMIN;
            currentFilterStatus = null;
            locDuLieu();
        });

        tvFilterSinhVien.setOnClickListener(v -> {
            datTrangThaiChip(tvFilterSinhVien);
            currentFilterRole = TaiKhoan.ROLE_SINHVIEN;
            currentFilterStatus = null;
            locDuLieu();
        });

        tvFilterLocked.setOnClickListener(v -> {
            datTrangThaiChip(tvFilterLocked);
            currentFilterRole = null;
            currentFilterStatus = TaiKhoan.STATUS_LOCKED;
            locDuLieu();
        });
    }

    private void datTrangThaiChip(TextView selectedView) {
        TextView[] chips = new TextView[]{tvFilterAll, tvFilterAdmin, tvFilterSinhVien, tvFilterLocked};
        for (TextView chip : chips) {
            if (chip == selectedView) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected);
                chip.setTextColor(getResources().getColor(R.color.card_white));
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected);
                chip.setTextColor(getResources().getColor(R.color.text_secondary));
            }
        }
    }

    private void loadData() {
        // Cập nhật số liệu thống kê
        int total = taiKhoanRepository.countTotalAccounts();
        int active = taiKhoanRepository.countActiveAccounts();
        int locked = taiKhoanRepository.countLockedAccounts();

        tvStatTotal.setText(String.valueOf(total));
        tvStatActive.setText(String.valueOf(active));
        tvStatLocked.setText(String.valueOf(locked));

        locDuLieu();
    }

    private void locDuLieu() {
        listTaiKhoan = taiKhoanRepository.timKiemVaLoc(currentKeyword, currentFilterRole, currentFilterStatus);
        accountAdapter.setData(listTaiKhoan);

        if (listTaiKhoan.isEmpty()) {
            llEmptyStateAccount.setVisibility(View.VISIBLE);
            rvAccountList.setVisibility(View.GONE);
        } else {
            llEmptyStateAccount.setVisibility(View.GONE);
            rvAccountList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onToggleLock(TaiKhoan account) {
        if (account == null) return;

        // Bảo vệ tài khoản admin hiện tại
        if (sessionManager.getUsername().equalsIgnoreCase(account.getTenDangNhap())) {
            Toast.makeText(this, "Không thể khóa tài khoản đang đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean willLock = account.isActive();
        String actionTitle = willLock ? "Khóa tài khoản" : "Mở khóa tài khoản";
        String actionMsg = willLock
                ? "Bạn có chắc chắn muốn KHÓA tài khoản '" + account.getTenDangNhap() + "'? Người dùng sẽ không thể đăng nhập vào ứng dụng."
                : "Bạn có chắc chắn muốn MỞ KHÓA tài khoản '" + account.getTenDangNhap() + "'?";

        new MaterialAlertDialogBuilder(this)
                .setTitle(actionTitle)
                .setMessage(actionMsg)
                .setPositiveButton(willLock ? "Khóa ngay" : "Mở khóa", (dialog, which) -> {
                    int newStatus = willLock ? TaiKhoan.STATUS_LOCKED : TaiKhoan.STATUS_ACTIVE;
                    boolean success = taiKhoanRepository.capNhatTrangThai(account.getMaTaiKhoan(), newStatus);
                    if (success) {
                        Toast.makeText(this, (willLock ? "Đã khóa" : "Đã mở khóa") + " tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Thao tác thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onResetPassword(TaiKhoan account) {
        if (account == null) return;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvResetDialogSubtitle);
        TextInputEditText edtNewPass = dialogView.findViewById(R.id.edtResetNewPassword);
        TextInputEditText edtConfirmPass = dialogView.findViewById(R.id.edtResetConfirmPassword);

        tvSubtitle.setText("Đặt lại mật khẩu cho tài khoản: " + account.getTenDangNhap());

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String newPass = edtNewPass.getText() != null ? edtNewPass.getText().toString().trim() : "";
                    String confirmPass = edtConfirmPass.getText() != null ? edtConfirmPass.getText().toString().trim() : "";

                    if (newPass.length() < 6) {
                        Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = taiKhoanRepository.datLaiMatKhau(account.getMaTaiKhoan(), newPass);
                    if (ok) {
                        Toast.makeText(this, "Đặt lại mật khẩu thành công cho '" + account.getTenDangNhap() + "'!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Đặt lại mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onItemClick(TaiKhoan account) {
        if (account == null) return;

        StringBuilder details = new StringBuilder();
        details.append("Tên đăng nhập: ").append(account.getTenDangNhap()).append("\n");
        details.append("Vai trò: ").append(account.getVaiTro()).append("\n");
        details.append("Trạng thái: ").append(account.isActive() ? "Đang hoạt động" : "Bị khóa").append("\n");

        if (account.isSinhVien()) {
            details.append("Mã sinh viên: ").append(account.getMaSinhVien() != null ? account.getMaSinhVien() : "--").append("\n");
            if (account.getHoTenSinhVien() != null) {
                details.append("Họ và tên: ").append(account.getHoTenSinhVien()).append("\n");
            }
            if (account.getTenLop() != null) {
                details.append("Lớp học: ").append(account.getTenLop()).append("\n");
            }
            if (account.getEmail() != null) {
                details.append("Email: ").append(account.getEmail()).append("\n");
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Chi tiết tài khoản")
                .setMessage(details.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    /**
     * Mở Dialog cấp tài khoản cho sinh viên chưa có tài khoản
     */
    private void hienThiDialogCapTaiKhoan() {
        List<SinhVien> sinhVienChuaCoTaiKhoan = taiKhoanRepository.getDanhSachSinhVienChuaCoTaiKhoan();

        if (sinhVienChuaCoTaiKhoan.isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Thông báo")
                    .setMessage("Tất cả sinh viên hiện đã được cấp tài khoản!")
                    .setPositiveButton("Đóng", null)
                    .show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_grant_student_account, null);
        AutoCompleteTextView actvSelectStudent = dialogView.findViewById(R.id.actvSelectStudent);
        TextInputEditText edtGrantUsername = dialogView.findViewById(R.id.edtGrantUsername);
        TextInputEditText edtGrantPassword = dialogView.findViewById(R.id.edtGrantPassword);

        List<String> studentDisplayList = new ArrayList<>();
        for (SinhVien sv : sinhVienChuaCoTaiKhoan) {
            String lop = (sv.getTenLop() != null && !sv.getTenLop().isEmpty()) ? " - " + sv.getTenLop() : "";
            studentDisplayList.add(sv.getMaSinhVien() + " • " + sv.getHoTen() + lop);
        }

        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, studentDisplayList);
        actvSelectStudent.setAdapter(studentAdapter);

        final SinhVien[] selectedStudent = {null};

        actvSelectStudent.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < sinhVienChuaCoTaiKhoan.size()) {
                selectedStudent[0] = sinhVienChuaCoTaiKhoan.get(position);
                // Gợi ý tên đăng nhập bằng mã SV
                edtGrantUsername.setText(selectedStudent[0].getMaSinhVien());
            }
        });

        // Chọn mặc định mục đầu tiên
        if (!sinhVienChuaCoTaiKhoan.isEmpty()) {
            actvSelectStudent.setText(studentDisplayList.get(0), false);
            selectedStudent[0] = sinhVienChuaCoTaiKhoan.get(0);
            edtGrantUsername.setText(selectedStudent[0].getMaSinhVien());
        }

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Cấp tài khoản", (dialog, which) -> {
                    if (selectedStudent[0] == null) {
                        Toast.makeText(this, "Vui lòng chọn sinh viên!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String user = edtGrantUsername.getText() != null ? edtGrantUsername.getText().toString().trim() : "";
                    String pass = edtGrantPassword.getText() != null ? edtGrantPassword.getText().toString().trim() : "";

                    if (user.isEmpty()) {
                        Toast.makeText(this, "Tên đăng nhập không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pass.length() < 6) {
                        Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (taiKhoanRepository.isUsernameExists(user)) {
                        Toast.makeText(this, "Tên đăng nhập '" + user + "' đã tồn tại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean result = taiKhoanRepository.taoTaiKhoanSinhVien(
                            selectedStudent[0].getMaSinhVien(),
                            user,
                            pass
                    );

                    if (result) {
                        Toast.makeText(this, "Cấp tài khoản cho " + selectedStudent[0].getHoTen() + " thành công!", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Cấp tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taiKhoanRepository != null) {
            taiKhoanRepository.close();
        }
    }
}
