
package vhu.edu.edusync.ui.admin.major;

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
import java.util.Locale;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.MajorAdapter;
import vhu.edu.edusync.data.repository.ChuyenNganhRepository;
import vhu.edu.edusync.data.repository.KhoaRepository;
import vhu.edu.edusync.model.ChuyenNganh;
import vhu.edu.edusync.model.Khoa;

public class MajorListActivity extends AppCompatActivity {

    private ImageView btnBackMajor;
    private EditText edtSearchMajor;
    private TextView tvMajorCount, tvMajorEmpty;
    private RecyclerView rvMajor;

    private MajorAdapter adapter;

    private ChuyenNganhRepository chuyenNganhRepository;
    private KhoaRepository khoaRepository;

    private List<ChuyenNganh> danhSachChuyenNganh = new ArrayList<>();
    private List<Khoa> danhSachKhoa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_list);

        chuyenNganhRepository = new ChuyenNganhRepository(this);
        khoaRepository = new KhoaRepository(this);

        anhXa();
        cauHinhRecyclerView();
        xuLySuKien();
        taiDanhSach();
    }

    private void anhXa() {
        btnBackMajor = findViewById(R.id.btnBackMajor);
        edtSearchMajor = findViewById(R.id.edtSearchMajor);
        tvMajorCount = findViewById(R.id.tvMajorCount);
        tvMajorEmpty = findViewById(R.id.tvMajorEmpty);
        rvMajor = findViewById(R.id.rvMajor);
    }

    private void cauHinhRecyclerView() {
        rvMajor.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MajorAdapter(
                danhSachChuyenNganh,
                danhSachKhoa,
                new MajorAdapter.OnMajorActionListener() {
                    @Override
                    public void onEdit(ChuyenNganh cn) {
                        moFormSua(cn);
                    }

                    @Override
                    public void onDelete(ChuyenNganh cn) {
                        xacNhanXoa(cn);
                    }
                }
        );

        rvMajor.setAdapter(adapter);
    }

    private void xuLySuKien() {
        btnBackMajor.setOnClickListener(v -> finish());

        findViewById(R.id.btnAddMajor).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MajorListActivity.this,
                    MajorFormActivity.class
            );
            startActivity(intent);
        });

        edtSearchMajor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count
            ) {
                timKiemChuyenNganh(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void taiDanhSach() {
        danhSachKhoa = khoaRepository.getAll();
        danhSachChuyenNganh = chuyenNganhRepository.getAll();

        timKiemChuyenNganh(edtSearchMajor.getText().toString());
    }

    private void timKiemChuyenNganh(String tuKhoa) {
        List<ChuyenNganh> ketQua = new ArrayList<>();

        String tuKhoaLower = tuKhoa.trim().toLowerCase(Locale.ROOT);

        for (ChuyenNganh cn : danhSachChuyenNganh) {
            String ma = cn.getMaChuyenNganh().toLowerCase(Locale.ROOT);
            String ten = cn.getTenChuyenNganh().toLowerCase(Locale.ROOT);

            if (ma.contains(tuKhoaLower) || ten.contains(tuKhoaLower)) {
                ketQua.add(cn);
            }
        }

        adapter.setData(ketQua);

        tvMajorCount.setText(
                "Danh sách chuyên ngành (" + ketQua.size() + ")"
        );

        if (ketQua.isEmpty()) {
            tvMajorEmpty.setVisibility(TextView.VISIBLE);
            rvMajor.setVisibility(RecyclerView.GONE);
        } else {
            tvMajorEmpty.setVisibility(TextView.GONE);
            rvMajor.setVisibility(RecyclerView.VISIBLE);
        }
    }

    private void moFormSua(ChuyenNganh cn) {
        Intent intent = new Intent(
                MajorListActivity.this,
                MajorFormActivity.class
        );

        intent.putExtra("maChuyenNganh", cn.getMaChuyenNganh());
        startActivity(intent);
    }

    private void xacNhanXoa(ChuyenNganh cn) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage(
                        "Bạn có chắc muốn xóa chuyên ngành "
                                + cn.getTenChuyenNganh() + " không?"
                )
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xóa", (dialog, which) -> {
                    try {
                        boolean ketQua = chuyenNganhRepository.xoaChuyenNganh(
                                cn.getMaChuyenNganh()
                        );

                        if (ketQua) {
                            Toast.makeText(
                                    this,
                                    "Xóa chuyên ngành thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                            taiDanhSach();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Không tìm thấy chuyên ngành cần xóa",
                                    Toast.LENGTH_SHORT
                            ).show();
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
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (chuyenNganhRepository != null && adapter != null) {
            taiDanhSach();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chuyenNganhRepository != null) {
            chuyenNganhRepository.close();
        }

        if (khoaRepository != null) {
            khoaRepository.close();
        }
    }
}
