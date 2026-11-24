package vhu.edu.edusync.ui.admin.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.StudentAdapter;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.repository.SinhVienRepository;
import vhu.edu.edusync.ui.admin.main.MainActivity;

/**
 * Activity hiển thị danh sách sinh viên, hỗ trợ tìm kiếm, lọc giới tính, sửa và xóa
 */
public class StudentListActivity extends AppCompatActivity {

    // Views
    private ImageView imgBack;
    private ImageView imgSearchAction;
    private EditText edtSearch;
    private TextView tvFilterAll, tvFilterMale, tvFilterFemale;
    private RecyclerView rvStudentList;
    private LinearLayout llEmptyState;
    private TextView tvEmptyTitle, tvEmptySubtitle;
    private FloatingActionButton btnAddStudent;

    // Data & Adapter
    private SinhVienRepository sinhVienRepository;
    private StudentAdapter adapter;
    private List<SinhVien> danhSachGoc = new ArrayList<>();
    private List<SinhVien> danhSachHienThi = new ArrayList<>();

    // Bộ lọc giới tính hiện tại: 0: Tất cả, 1: Nam, 2: Nữ
    private int filterGender = 0;
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Xử lý status bar insets để header không bị chìm dưới thanh trạng thái
        View llHeader = findViewById(R.id.llHeader);
        if (llHeader != null) {
            ViewCompat.setOnApplyWindowInsetsListener(llHeader, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        sinhVienRepository = new SinhVienRepository(this);

        anhXa();
        cauHinhRecyclerView();
        xuLySuKien();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSachSinhVien();
    }

    private void anhXa() {
        imgBack = findViewById(R.id.imgBack);
        imgSearchAction = findViewById(R.id.imgSearchAction);
        edtSearch = findViewById(R.id.edtSearch);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterMale = findViewById(R.id.tvFilterMale);
        tvFilterFemale = findViewById(R.id.tvFilterFemale);
        rvStudentList = findViewById(R.id.rvStudentList);
        llEmptyState = findViewById(R.id.llEmptyState);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        btnAddStudent = findViewById(R.id.btnAddStudent);
    }

    private void cauHinhRecyclerView() {
        rvStudentList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(danhSachHienThi, new StudentAdapter.OnStudentActionListener() {
            @Override
            public void onEdit(SinhVien sv) {
                moFormSua(sv);
            }

            @Override
            public void onDelete(SinhVien sv) {
                xacNhanXoa(sv);
            }

            @Override
            public void onItemClick(SinhVien sv) {
                hienThiChiTiet(sv);
            }
        });
        rvStudentList.setAdapter(adapter);
    }

    private void xuLySuKien() {
        // Nút quay lại
        imgBack.setOnClickListener(v -> {
            if (isTaskRoot()) {
                Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        });

        // Bấm icon kính lúp trên header
        imgSearchAction.setOnClickListener(v -> {
            edtSearch.requestFocus();
        });

        // Tìm kiếm realtime theo tên hoặc mã sinh viên
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                currentKeyword = s.toString().trim();
                apDungBoLoc();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bộ lọc nhanh Tất cả / Nam / Nữ
        tvFilterAll.setOnClickListener(v -> {
            filterGender = 0;
            capNhatGiaoDienFilter();
            apDungBoLoc();
        });

        tvFilterMale.setOnClickListener(v -> {
            filterGender = 1;
            capNhatGiaoDienFilter();
            apDungBoLoc();
        });

        tvFilterFemale.setOnClickListener(v -> {
            filterGender = 2;
            capNhatGiaoDienFilter();
            apDungBoLoc();
        });

        // Nút thêm sinh viên mới
        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(StudentListActivity.this, StudentFormActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Tải lại danh sách sinh viên từ SQLite
     */
    private void taiDanhSachSinhVien() {
        danhSachGoc = sinhVienRepository.getAll();
        apDungBoLoc();
    }

    /**
     * Lọc danh sách theo từ khóa tìm kiếm và giới tính
     */
    private void apDungBoLoc() {
        danhSachHienThi.clear();

        for (SinhVien sv : danhSachGoc) {
            boolean matchKeyword = true;
            if (!currentKeyword.isEmpty()) {
                String kw = currentKeyword.toLowerCase();
                String hoTen = sv.getHoTen() != null ? sv.getHoTen().toLowerCase() : "";
                String mssv = sv.getMaSinhVien() != null ? sv.getMaSinhVien().toLowerCase() : "";
                matchKeyword = hoTen.contains(kw) || mssv.contains(kw);
            }

            boolean matchGender = true;
            if (filterGender == 1) { // Nam
                matchGender = "Nam".equalsIgnoreCase(sv.getGioiTinh());
            } else if (filterGender == 2) { // Nữ
                matchGender = "Nữ".equalsIgnoreCase(sv.getGioiTinh()) || "Nu".equalsIgnoreCase(sv.getGioiTinh());
            }

            if (matchKeyword && matchGender) {
                danhSachHienThi.add(sv);
            }
        }

        adapter.setData(danhSachHienThi);

        // Hiển thị trạng thái trống nếu không có kết quả
        if (danhSachHienThi.isEmpty()) {
            rvStudentList.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            if (!currentKeyword.isEmpty() || filterGender != 0) {
                tvEmptyTitle.setText("Không tìm thấy kết quả");
                tvEmptySubtitle.setText("Thử tìm kiếm với từ khóa hoặc bộ lọc khác");
            } else {
                tvEmptyTitle.setText("Chưa có sinh viên nào");
                tvEmptySubtitle.setText("Nhấn nút (+) bên dưới để thêm sinh viên mới");
            }
        } else {
            rvStudentList.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Đổi giao diện các Chip bộ lọc
     */
    private void capNhatGiaoDienFilter() {
        int bgSelected = R.drawable.bg_chip_selected;
        int bgUnselected = R.drawable.bg_chip_unselected;
        int colorWhite = getColor(R.color.card_white);
        int colorSec = getColor(R.color.text_secondary);

        tvFilterAll.setBackgroundResource(filterGender == 0 ? bgSelected : bgUnselected);
        tvFilterAll.setTextColor(filterGender == 0 ? colorWhite : colorSec);

        tvFilterMale.setBackgroundResource(filterGender == 1 ? bgSelected : bgUnselected);
        tvFilterMale.setTextColor(filterGender == 1 ? colorWhite : colorSec);

        tvFilterFemale.setBackgroundResource(filterGender == 2 ? bgSelected : bgUnselected);
        tvFilterFemale.setTextColor(filterGender == 2 ? colorWhite : colorSec);
    }

    /**
     * Mở form chỉnh sửa sinh viên
     */
    private void moFormSua(SinhVien sv) {
        Intent intent = new Intent(StudentListActivity.this, StudentFormActivity.class);
        intent.putExtra("STUDENT", sv);
        startActivity(intent);
    }

    /**
     * Xem thông tin chi tiết sinh viên
     */
    private void hienThiChiTiet(SinhVien sv) {
        String thongTin = "Mã SV: " + sv.getMaSinhVien() +
                "\nHọ tên: " + sv.getHoTen() +
                "\nNgày sinh: " + (sv.getNgaySinh() != null ? sv.getNgaySinh() : "Chưa có") +
                "\nGiới tính: " + (sv.getGioiTinh() != null ? sv.getGioiTinh() : "Chưa có") +
                "\nLớp: " + (sv.getTenLop() != null ? sv.getTenLop() : (sv.getMaLop() != null ? sv.getMaLop() : "Chưa phân lớp")) +
                "\nEmail: " + (sv.getEmail() != null ? sv.getEmail() : "Chưa có") +
                "\nSố điện thoại: " + (sv.getSoDienThoai() != null ? sv.getSoDienThoai() : "Chưa có") +
                "\nĐịa chỉ: " + (sv.getDiaChi() != null ? sv.getDiaChi() : "Chưa có");

        new AlertDialog.Builder(this)
                .setTitle("Thông tin sinh viên")
                .setMessage(thongTin)
                .setPositiveButton("Đóng", null)
                .setNegativeButton("Chỉnh sửa", (dialog, which) -> moFormSua(sv))
                .show();
    }

    /**
     * Hộp thoại xác nhận trước khi xóa
     */
    private void xacNhanXoa(SinhVien sv) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên \"" + sv.getHoTen() + "\" (" + sv.getMaSinhVien() + ") không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int ketQua = sinhVienRepository.xoaSinhVien(sv.getMaSinhVien());
                    if (ketQua == 1) {
                        Toast.makeText(this, "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                        taiDanhSachSinhVien();
                    } else if (ketQua == -1) {
                        new AlertDialog.Builder(this)
                                .setTitle("Không thể xóa")
                                .setMessage("Sinh viên này đã có điểm học phần hoặc đăng ký học phần trong hệ thống. Để bảo toàn dữ liệu môn học, bạn không thể xóa sinh viên này.")
                                .setPositiveButton("Đã hiểu", null)
                                .show();
                    } else {
                        Toast.makeText(this, "Không thể xóa sinh viên!", Toast.LENGTH_SHORT).show();
                    }
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
