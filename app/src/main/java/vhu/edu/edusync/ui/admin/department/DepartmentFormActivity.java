
package vhu.edu.edusync.ui.admin.department;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.repository.KhoaRepository;
import vhu.edu.edusync.model.Khoa;

public class DepartmentFormActivity extends AppCompatActivity {

    private ImageView btnBackDepartmentForm;
    private TextView tvDepartmentFormTitle;
    private EditText edtDepartmentCode;
    private EditText edtDepartmentName;
    private EditText edtDepartmentDescription;
    private MaterialButton btnSaveDepartment;

    private KhoaRepository khoaRepository;

    private String maKhoaCu;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_form);

        khoaRepository = new KhoaRepository(this);

        anhXa();
        kiemTraCheDo();
        xuLySuKien();
    }

    private void anhXa() {
        btnBackDepartmentForm = findViewById(R.id.btnBackDepartmentForm);
        tvDepartmentFormTitle = findViewById(R.id.tvDepartmentFormTitle);
        edtDepartmentCode = findViewById(R.id.edtDepartmentCode);
        edtDepartmentName = findViewById(R.id.edtDepartmentName);
        edtDepartmentDescription = findViewById(R.id.edtDepartmentDescription);
        btnSaveDepartment = findViewById(R.id.btnSaveDepartment);
    }

    private void kiemTraCheDo() {
        maKhoaCu = getIntent().getStringExtra("maKhoa");

        if (maKhoaCu != null) {
            isEdit = true;
            tvDepartmentFormTitle.setText("Sửa khoa");

            Khoa khoa = khoaRepository.getById(maKhoaCu);

            if (khoa == null) {
                Toast.makeText(this, "Không tìm thấy khoa", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            edtDepartmentCode.setText(khoa.getMaKhoa());
            edtDepartmentName.setText(khoa.getTenKhoa());
            edtDepartmentDescription.setText(khoa.getMoTa());

            // Không cho sửa mã khoa
            edtDepartmentCode.setEnabled(false);

            btnSaveDepartment.setText("Cập nhật");
        } else {
            isEdit = false;
            tvDepartmentFormTitle.setText("Thêm khoa");
            btnSaveDepartment.setText("Lưu khoa");
        }
    }

    private void xuLySuKien() {
        btnBackDepartmentForm.setOnClickListener(v -> {
            if (isTaskRoot()) {
                Intent intent = new Intent(DepartmentFormActivity.this, DepartmentListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        });

        btnSaveDepartment.setOnClickListener(v -> luuKhoa());
    }

    private void luuKhoa() {
        String maKhoa = edtDepartmentCode.getText().toString().trim();
        String tenKhoa = edtDepartmentName.getText().toString().trim();
        String moTa = edtDepartmentDescription.getText().toString().trim();

        if (maKhoa.isEmpty()) {
            edtDepartmentCode.setError("Vui lòng nhập mã khoa");
            edtDepartmentCode.requestFocus();
            return;
        }

        if (tenKhoa.isEmpty()) {
            edtDepartmentName.setError("Vui lòng nhập tên khoa");
            edtDepartmentName.requestFocus();
            return;
        }

        try {
            Khoa khoa = new Khoa(
                    maKhoa,
                    tenKhoa,
                    moTa.isEmpty() ? null : moTa
            );

            if (isEdit) {
                int ketQua = khoaRepository.suaKhoa(khoa);
                if (ketQua == 0) {
                    Toast.makeText(this, "Không thể cập nhật khoa", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Cập nhật khoa thành công", Toast.LENGTH_SHORT).show();

            } else {
                khoaRepository.themKhoa(khoa);

                Toast.makeText(this, "Thêm khoa thành công", Toast.LENGTH_SHORT).show();
            }

            setResult(RESULT_OK);
            finish();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    e.getMessage() == null ? "Có lỗi xảy ra" : e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
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
