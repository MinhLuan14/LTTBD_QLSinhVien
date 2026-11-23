
package vhu.edu.edusync.ui.admin.department;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.DepartmentAdapter;
import vhu.edu.edusync.data.repository.KhoaRepository;
import vhu.edu.edusync.model.Khoa;

public class DepartmentListActivity extends AppCompatActivity {

    private ImageView btnBackDepartment;
    private EditText edtSearchDepartment;
    private TextView tvDepartmentCount;
    private TextView tvDepartmentEmpty;
    private RecyclerView rvDepartment;

    private DepartmentAdapter adapter;
    private KhoaRepository khoaRepository;

    private List<Khoa> danhSachKhoa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);

        // 1. Khởi tạo Repository
        khoaRepository = new KhoaRepository(this);

        // 2. Ánh xạ giao diện
        anhXa();

        // 3. Cấu hình RecyclerView
        cauHinhRecyclerView();

        // 4. Xử lý sự kiện
        xuLySuKien();

        // 5. Hiển thị dữ liệu
        taiDanhSachKhoa();
    }

    // Ánh xạ View
    private void anhXa() {
        btnBackDepartment = findViewById(R.id.btnBackDepartment);
        edtSearchDepartment = findViewById(R.id.edtSearchDepartment);
        tvDepartmentCount = findViewById(R.id.tvDepartmentCount);
        tvDepartmentEmpty = findViewById(R.id.tvDepartmentEmpty);
        rvDepartment = findViewById(R.id.rvDepartment);
    }

    // Cấu hình RecyclerView
    private void cauHinhRecyclerView() {

        rvDepartment.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DepartmentAdapter(
                danhSachKhoa,
                new DepartmentAdapter.OnDepartmentActionListener() {

                    @Override
                    public void onEdit(Khoa khoa) {
                        moFormSua(khoa);
                    }

                    @Override
                    public void onDelete(Khoa khoa) {
                        xacNhanXoa(khoa);
                    }
                }
        );

        rvDepartment.setAdapter(adapter);
    }

    // Lấy danh sách Khoa từ SQLite
    private void taiDanhSachKhoa() {

        danhSachKhoa = khoaRepository.getAll();

        adapter.setData(danhSachKhoa);

        tvDepartmentCount.setText(
                "Danh sách khoa (" + danhSachKhoa.size() + ")"
        );

        if (danhSachKhoa.isEmpty()) {
            tvDepartmentEmpty.setVisibility(TextView.VISIBLE);
            rvDepartment.setVisibility(RecyclerView.GONE);
        } else {
            tvDepartmentEmpty.setVisibility(TextView.GONE);
            rvDepartment.setVisibility(RecyclerView.VISIBLE);
        }
    }

    // Xử lý các sự kiện trên màn hình
    private void xuLySuKien() {

        // Quay lại
        btnBackDepartment.setOnClickListener(v -> finish());

        // Thêm Khoa
        findViewById(R.id.btnAddDepartment).setOnClickListener(v -> {
            Intent intent = new Intent(
                    DepartmentListActivity.this,
                    DepartmentFormActivity.class
            );

            startActivity(intent);
        });

        // Tìm kiếm
        edtSearchDepartment.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                timKiemKhoa(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Tìm kiếm Khoa theo mã hoặc tên
    private void timKiemKhoa(String tuKhoa) {

        List<Khoa> ketQua = new ArrayList<>();

        String tuKhoaLower = tuKhoa.trim().toLowerCase();

        for (Khoa khoa : danhSachKhoa) {

            String maKhoa = khoa.getMaKhoa().toLowerCase();
            String tenKhoa = khoa.getTenKhoa().toLowerCase();

            if (maKhoa.contains(tuKhoaLower)
                    || tenKhoa.contains(tuKhoaLower)) {

                ketQua.add(khoa);
            }
        }

        adapter.setData(ketQua);

        tvDepartmentCount.setText(
                "Danh sách khoa (" + ketQua.size() + ")"
        );

        if (ketQua.isEmpty()) {
            tvDepartmentEmpty.setText("Không tìm thấy khoa phù hợp");
            tvDepartmentEmpty.setVisibility(TextView.VISIBLE);
            rvDepartment.setVisibility(RecyclerView.GONE);
        } else {
            tvDepartmentEmpty.setText("Chưa có dữ liệu khoa");
            tvDepartmentEmpty.setVisibility(TextView.GONE);
            rvDepartment.setVisibility(RecyclerView.VISIBLE);
        }
    }

    // Mở form sửa Khoa
    private void moFormSua(Khoa khoa) {

        Intent intent = new Intent(
                DepartmentListActivity.this,
                DepartmentFormActivity.class
        );

        intent.putExtra("maKhoa", khoa.getMaKhoa());

        startActivity(intent);
    }

    // Xác nhận trước khi xóa
    private void xacNhanXoa(Khoa khoa) {

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage(
                        "Bạn có chắc chắn muốn xóa khoa "
                                + khoa.getTenKhoa() + " không?"
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {

                    try {
                        boolean ketQua = khoaRepository.xoaKhoa(
                                khoa.getMaKhoa()
                        );

                        if (ketQua) {
                            Toast.makeText(
                                    this,
                                    "Xóa khoa thành công",
                                    Toast.LENGTH_SHORT
                            ).show();

                            taiDanhSachKhoa();
                        }

                    } catch (IllegalStateException e) {

                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Không thể xóa khoa",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }

    // Tải lại danh sách khi quay về màn hình
    @Override
    protected void onResume() {
        super.onResume();

        if (khoaRepository != null && adapter != null) {
            taiDanhSachKhoa();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (khoaRepository != null) {
            khoaRepository.close();
        }
    }
}
