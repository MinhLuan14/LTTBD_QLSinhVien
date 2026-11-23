package vhu.edu.edusync.ui.admin.subject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.repository.MonHocRepository;
import vhu.edu.edusync.model.MonHoc;

public class SubjectFormActivity extends AppCompatActivity {

    public static final String EXTRA_MA_MON_HOC = "maMonHoc";

    private ImageView btnBack;
    private TextView tvFormTitle;
    private EditText edtSubjectCode, edtSubjectName, edtSubjectCredits;
    private EditText edtTheoryHours, edtPracticeHours, edtSubjectDescription;
    private RadioButton rbCompulsory, rbElective;
    private MaterialButton btnSaveSubject;

    private MonHocRepository monHocRepository;

    private MonHoc monHocCu;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_form);

        monHocRepository = new MonHocRepository(this);

        anhXa();
        kiemTraCheDo();

        btnBack.setOnClickListener(v -> finish());
        btnSaveSubject.setOnClickListener(v -> luuMonHoc());
    }

    private void anhXa() {
        btnBack = findViewById(R.id.btnBack);
        tvFormTitle = findViewById(R.id.tvFormTitle);
        edtSubjectCode = findViewById(R.id.edtSubjectCode);
        edtSubjectName = findViewById(R.id.edtSubjectName);
        edtSubjectCredits = findViewById(R.id.edtSubjectCredits);
        edtTheoryHours = findViewById(R.id.edtTheoryHours);
        edtPracticeHours = findViewById(R.id.edtPracticeHours);
        edtSubjectDescription = findViewById(R.id.edtSubjectDescription);
        rbCompulsory = findViewById(R.id.rbCompulsory);
        rbElective = findViewById(R.id.rbElective);
        btnSaveSubject = findViewById(R.id.btnSaveSubject);
    }

    // Xác định thêm mới hay chỉnh sửa
    private void kiemTraCheDo() {
        String ma = getIntent().getStringExtra(EXTRA_MA_MON_HOC);

        if (ma == null) {
            isEdit = false;
            tvFormTitle.setText(R.string.title_add_subject);
            return;
        }

        monHocCu = monHocRepository.getById(ma);

        if (monHocCu == null) {
            Toast.makeText(this, "Không tìm thấy môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        isEdit = true;
        tvFormTitle.setText(R.string.title_edit_subject);

        edtSubjectCode.setText(monHocCu.getMaMonHoc());
        edtSubjectCode.setEnabled(false); // Không cho sửa mã
        edtSubjectName.setText(monHocCu.getTenMonHoc());
        edtSubjectCredits.setText(String.valueOf(monHocCu.getSoTinChi()));
        edtTheoryHours.setText(String.valueOf(monHocCu.getSoTietLyThuyet()));
        edtPracticeHours.setText(String.valueOf(monHocCu.getSoTietThucHanh()));
        edtSubjectDescription.setText(monHocCu.getMoTa());

        if (MonHoc.LOAI_TU_CHON.equals(monHocCu.getLoaiMonHoc())) {
            rbElective.setChecked(true);
        } else {
            rbCompulsory.setChecked(true);
        }
    }

    private void luuMonHoc() {
        String ma = edtSubjectCode.getText().toString().trim();
        String ten = edtSubjectName.getText().toString().trim();
        String tinChiStr = edtSubjectCredits.getText().toString().trim();
        String moTa = edtSubjectDescription.getText().toString().trim();

        if (ma.isEmpty()) {
            edtSubjectCode.setError("Vui lòng nhập mã môn học");
            edtSubjectCode.requestFocus();
            return;
        }

        if (ten.isEmpty()) {
            edtSubjectName.setError("Vui lòng nhập tên môn học");
            edtSubjectName.requestFocus();
            return;
        }

        Integer soTinChi = docSoNguyen(tinChiStr);

        if (soTinChi == null
                || soTinChi < MonHocRepository.TIN_CHI_TOI_THIEU
                || soTinChi > MonHocRepository.TIN_CHI_TOI_DA) {
            edtSubjectCredits.setError("Số tín chỉ phải từ "
                    + MonHocRepository.TIN_CHI_TOI_THIEU + " đến "
                    + MonHocRepository.TIN_CHI_TOI_DA);
            edtSubjectCredits.requestFocus();
            return;
        }

        Integer tietLT = docSoTiet(edtTheoryHours);
        if (tietLT == null) {
            return;
        }

        Integer tietTH = docSoTiet(edtPracticeHours);
        if (tietTH == null) {
            return;
        }

        MonHoc mh = new MonHoc(
                ma,
                ten,
                soTinChi,
                rbElective.isChecked() ? MonHoc.LOAI_TU_CHON : MonHoc.LOAI_BAT_BUOC,
                tietLT,
                tietTH,
                moTa.isEmpty() ? null : moTa
        );

        // Cảnh báo khi đổi số tín chỉ của môn đã có điểm (ảnh hưởng GPA)
        if (isEdit && monHocCu.getSoTinChi() != soTinChi
                && monHocRepository.kiemTraCoDiem(monHocCu.getMaMonHoc())) {
            new AlertDialog.Builder(this)
                    .setTitle("Cảnh báo")
                    .setMessage("Môn học đã có điểm của sinh viên. Thay đổi số tín chỉ "
                            + "sẽ ảnh hưởng đến GPA. Bạn vẫn muốn tiếp tục?")
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Tiếp tục", (d, w) -> thucHienLuu(mh))
                    .show();
            return;
        }

        thucHienLuu(mh);
    }

    private void thucHienLuu(MonHoc mh) {
        try {
            if (isEdit) {
                if (monHocRepository.suaMonHoc(mh) > 0) {
                    Toast.makeText(this, "Cập nhật môn học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Không tìm thấy môn học cần cập nhật", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (monHocRepository.themMonHoc(mh) != -1) {
                    Toast.makeText(this, "Thêm môn học thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Số tiết: để trống = 0, không được âm
    private Integer docSoTiet(EditText edt) {
        String s = edt.getText().toString().trim();

        if (s.isEmpty()) {
            return 0;
        }

        Integer giaTri = docSoNguyen(s);

        if (giaTri == null || giaTri < 0) {
            edt.setError("Số tiết không hợp lệ");
            edt.requestFocus();
            return null;
        }

        return giaTri;
    }

    private Integer docSoNguyen(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monHocRepository != null) {
            monHocRepository.close();
        }
    }
}
