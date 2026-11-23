
package vhu.edu.edusync.ui.admin.major;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.repository.ChuyenNganhRepository;
import vhu.edu.edusync.data.repository.KhoaRepository;
import vhu.edu.edusync.model.ChuyenNganh;
import vhu.edu.edusync.model.Khoa;

public class MajorFormActivity extends AppCompatActivity {

    private ImageView btnBackMajorForm;
    private TextView tvMajorFormTitle;

    private EditText edtMajorCode;
    private EditText edtMajorName;
    private EditText edtMajorDescription;

    private MaterialAutoCompleteTextView actvMajorDepartment;
    private MaterialButton btnSaveMajor;

    private ChuyenNganhRepository chuyenNganhRepository;
    private KhoaRepository khoaRepository;

    private List<Khoa> danhSachKhoa = new ArrayList<>();
    private Khoa khoaDuocChon;

    private String maChuyenNganhCu;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major_form);

        chuyenNganhRepository = new ChuyenNganhRepository(this);
        khoaRepository = new KhoaRepository(this);

        anhXa();
        taiDanhSachKhoa();
        kiemTraCheDo();
        xuLySuKien();
    }

    private void anhXa() {
        btnBackMajorForm = findViewById(R.id.btnBackMajorForm);
        tvMajorFormTitle = findViewById(R.id.tvMajorFormTitle);

        edtMajorCode = findViewById(R.id.edtMajorCode);
        edtMajorName = findViewById(R.id.edtMajorName);
        edtMajorDescription = findViewById(R.id.edtMajorDescription);

        actvMajorDepartment = findViewById(R.id.actvMajorDepartment);
        btnSaveMajor = findViewById(R.id.btnSaveMajor);
    }

    // Tải danh sách khoa vào dropdown
    private void taiDanhSachKhoa() {
        danhSachKhoa = khoaRepository.getAll();

        List<String> tenCacKhoa = new ArrayList<>();

        for (Khoa khoa : danhSachKhoa) {
            tenCacKhoa.add(
                    khoa.getTenKhoa() + " (" + khoa.getMaKhoa() + ")"
            );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                tenCacKhoa
        );

        actvMajorDepartment.setAdapter(adapter);

        actvMajorDepartment.setOnItemClickListener(
                (parent, view, position, id) -> {
                    khoaDuocChon = danhSachKhoa.get(position);
                }
        );
    }

    // Xác định thêm mới hay chỉnh sửa
    private void kiemTraCheDo() {
        maChuyenNganhCu = getIntent().getStringExtra("maChuyenNganh");

        if (maChuyenNganhCu != null) {
            isEdit = true;

            tvMajorFormTitle.setText("Sửa chuyên ngành");
            btnSaveMajor.setText("Cập nhật chuyên ngành");

            ChuyenNganh cn = chuyenNganhRepository.getById(
                    maChuyenNganhCu
            );

            if (cn == null) {
                Toast.makeText(
                        this,
                        "Không tìm thấy chuyên ngành",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
                return;
            }

            edtMajorCode.setText(cn.getMaChuyenNganh());
            edtMajorName.setText(cn.getTenChuyenNganh());
            edtMajorDescription.setText(cn.getMoTa());

            // Không cho sửa mã chuyên ngành
            edtMajorCode.setEnabled(false);

            // Chọn lại khoa cũ
            chonKhoaTheoMa(cn.getMaKhoa());

        } else {
            isEdit = false;

            tvMajorFormTitle.setText("Thêm chuyên ngành");
            btnSaveMajor.setText("Lưu thông tin chuyên ngành");
        }
    }

    private void chonKhoaTheoMa(String maKhoa) {
        for (int i = 0; i < danhSachKhoa.size(); i++) {
            Khoa khoa = danhSachKhoa.get(i);

            if (khoa.getMaKhoa().equals(maKhoa)) {
                khoaDuocChon = khoa;

                actvMajorDepartment.setText(
                        khoa.getTenKhoa() + " (" + khoa.getMaKhoa() + ")",
                        false
                );

                break;
            }
        }
    }

    private void xuLySuKien() {
        btnBackMajorForm.setOnClickListener(v -> finish());

        btnSaveMajor.setOnClickListener(v -> luuChuyenNganh());
    }

    private void luuChuyenNganh() {
        String ma = edtMajorCode.getText().toString().trim();
        String ten = edtMajorName.getText().toString().trim();
        String moTa = edtMajorDescription.getText().toString().trim();

        if (ma.isEmpty()) {
            edtMajorCode.setError("Vui lòng nhập mã chuyên ngành");
            edtMajorCode.requestFocus();
            return;
        }

        if (ten.isEmpty()) {
            edtMajorName.setError("Vui lòng nhập tên chuyên ngành");
            edtMajorName.requestFocus();
            return;
        }

        if (khoaDuocChon == null) {
            actvMajorDepartment.setError("Vui lòng chọn khoa trực thuộc");
            return;
        }

        ChuyenNganh cn = new ChuyenNganh(
                ma,
                ten,
                khoaDuocChon.getMaKhoa(),
                moTa.isEmpty() ? null : moTa
        );

        try {
            if (isEdit) {
                int ketQua = chuyenNganhRepository.suaChuyenNganh(cn);

                if (ketQua > 0) {
                    Toast.makeText(
                            this,
                            "Cập nhật chuyên ngành thành công",
                            Toast.LENGTH_SHORT
                    ).show();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(
                            this,
                            "Không tìm thấy chuyên ngành cần cập nhật",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } else {
                long ketQua = chuyenNganhRepository.themChuyenNganh(cn);

                if (ketQua != -1) {
                    Toast.makeText(
                            this,
                            "Thêm chuyên ngành thành công",
                            Toast.LENGTH_SHORT
                    ).show();

                    setResult(RESULT_OK);
                    finish();
                }
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
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
