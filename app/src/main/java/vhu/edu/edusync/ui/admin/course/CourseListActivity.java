package vhu.edu.edusync.ui.admin.course;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.CourseAdapter;
import vhu.edu.edusync.data.repository.HocPhanRepository;
import vhu.edu.edusync.data.repository.MonHocRepository;
import vhu.edu.edusync.model.HocPhan;
import vhu.edu.edusync.model.MonHoc;
import vhu.edu.edusync.model.TrangThaiHocPhan;

public class CourseListActivity extends AppCompatActivity {

    private TextView tvTotalCourse, tvCourseListTitle;
    private EditText edtSearchCourse;
    private MaterialButton btnAddCourse;
    private RecyclerView rvCourseList;

    private CourseAdapter adapter;

    private HocPhanRepository hocPhanRepository;
    private MonHocRepository monHocRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        hocPhanRepository = new HocPhanRepository(this);
        monHocRepository = new MonHocRepository(this);

        anhXa();
        cauHinhRecyclerView();
        xuLySuKien();
    }

    private void anhXa() {
        tvTotalCourse = findViewById(R.id.tvTotalCourse);
        tvCourseListTitle = findViewById(R.id.tvCourseListTitle);
        edtSearchCourse = findViewById(R.id.edtSearchCourse);
        btnAddCourse = findViewById(R.id.btnAddCourse);
        rvCourseList = findViewById(R.id.rvCourseList);
    }

    private void cauHinhRecyclerView() {
        rvCourseList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CourseAdapter(new ArrayList<>(), new CourseAdapter.OnCourseActionListener() {
            @Override
            public void onDetail(HocPhan hp) {
                hienThiChiTiet(hp);
            }

            @Override
            public void onEdit(HocPhan hp) {
                moDialogForm(hp);
            }

            @Override
            public void onDelete(HocPhan hp) {
                xacNhanXoa(hp);
            }

            @Override
            public void onChangeStatus(HocPhan hp) {
                chonTrangThai(hp);
            }
        });

        rvCourseList.setAdapter(adapter);
    }

    private void xuLySuKien() {
        btnAddCourse.setOnClickListener(v -> moDialogForm(null));

        edtSearchCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taiDanhSach();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ================= DANH SÁCH =================

    private void taiDanhSach() {
        List<HocPhan> ketQua = hocPhanRepository.timKiem(edtSearchCourse.getText().toString());

        adapter.setData(ketQua);

        tvTotalCourse.setText(String.valueOf(hocPhanRepository.getAll().size()));

        if (ketQua.isEmpty()) {
            tvCourseListTitle.setText("Danh sách học phần (trống)");
        } else {
            tvCourseListTitle.setText("Danh sách học phần (" + ketQua.size() + ")");
        }
    }

    // ================= THÊM / SỬA =================

    private void moDialogForm(HocPhan hpCu) {
        List<MonHoc> danhSachMonHoc = monHocRepository.getAll();

        if (danhSachMonHoc.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm môn học trước khi tạo học phần", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isEdit = hpCu != null;

        View view = getLayoutInflater().inflate(R.layout.dialog_course_form, null);

        TextView tvFormTitle = view.findViewById(R.id.tvFormTitle);
        TextInputEditText edtCourseCode = view.findViewById(R.id.edtCourseCode);
        MaterialAutoCompleteTextView spnCourseSubject = view.findViewById(R.id.spnCourseSubject);
        TextInputEditText edtCourseSemester = view.findViewById(R.id.edtCourseSemester);
        TextInputEditText edtCourseYear = view.findViewById(R.id.edtCourseYear);
        TextInputEditText edtCourseGroup = view.findViewById(R.id.edtCourseGroup);
        MaterialAutoCompleteTextView spnCourseStatus = view.findViewById(R.id.spnCourseStatus);
        MaterialButton btnCancelCourse = view.findViewById(R.id.btnCancelCourse);
        MaterialButton btnSaveCourse = view.findViewById(R.id.btnSaveCourse);

        // Dropdown môn học
        spnCourseSubject.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, danhSachMonHoc));

        final MonHoc[] monHocDuocChon = {null};
        spnCourseSubject.setOnItemClickListener((parent, v, position, id) ->
                monHocDuocChon[0] = (MonHoc) parent.getItemAtPosition(position));

        // Dropdown trạng thái
        spnCourseStatus.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, TrangThaiHocPhan.TAT_CA));

        if (isEdit) {
            tvFormTitle.setText("Sửa học phần");
            btnSaveCourse.setText("Cập nhật");

            edtCourseCode.setText(hpCu.getMaHocPhan());
            edtCourseCode.setEnabled(false);

            for (MonHoc mh : danhSachMonHoc) {
                if (mh.getMaMonHoc().equals(hpCu.getMaMonHoc())) {
                    monHocDuocChon[0] = mh;
                    spnCourseSubject.setText(mh.toString(), false);
                    break;
                }
            }

            edtCourseSemester.setText(String.valueOf(hpCu.getHocKy()));
            edtCourseYear.setText(hpCu.getNamHoc());
            edtCourseGroup.setText(hpCu.getNhomHocPhan());
            spnCourseStatus.setText(hpCu.getTrangThai(), false);

            // Đã có đăng ký/điểm: khóa môn học, học kỳ, năm học
            if (hocPhanRepository.coDuLieuLienQuan(hpCu.getMaHocPhan())) {
                spnCourseSubject.setEnabled(false);
                edtCourseSemester.setEnabled(false);
                edtCourseYear.setEnabled(false);
            }
        } else {
            tvFormTitle.setText("Thêm học phần");
            spnCourseStatus.setText(TrangThaiHocPhan.DANG_MO, false);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnCancelCourse.setOnClickListener(v -> dialog.dismiss());

        btnSaveCourse.setOnClickListener(v -> {
            String ma = layChuoi(edtCourseCode);
            String hocKyStr = layChuoi(edtCourseSemester);
            String namHoc = layChuoi(edtCourseYear);
            String nhom = layChuoi(edtCourseGroup);
            String trangThai = spnCourseStatus.getText().toString().trim();

            if (ma.isEmpty()) {
                edtCourseCode.setError("Vui lòng nhập mã học phần");
                edtCourseCode.requestFocus();
                return;
            }

            if (monHocDuocChon[0] == null) {
                spnCourseSubject.setError("Vui lòng chọn môn học");
                return;
            }

            int hocKy;
            try {
                hocKy = Integer.parseInt(hocKyStr);
            } catch (NumberFormatException e) {
                hocKy = -1;
            }

            if (hocKy < HocPhanRepository.HOC_KY_TOI_THIEU || hocKy > HocPhanRepository.HOC_KY_TOI_DA) {
                edtCourseSemester.setError("Học kỳ từ "
                        + HocPhanRepository.HOC_KY_TOI_THIEU + " đến "
                        + HocPhanRepository.HOC_KY_TOI_DA);
                edtCourseSemester.requestFocus();
                return;
            }

            if (!HocPhanRepository.isNamHocHopLe(namHoc)) {
                edtCourseYear.setError("Định dạng YYYY-YYYY (VD: 2026-2027)");
                edtCourseYear.requestFocus();
                return;
            }

            if (nhom.isEmpty()) {
                edtCourseGroup.setError("Vui lòng nhập nhóm học phần");
                edtCourseGroup.requestFocus();
                return;
            }

            if (!TrangThaiHocPhan.isHopLe(trangThai)) {
                spnCourseStatus.setError("Vui lòng chọn trạng thái");
                return;
            }

            HocPhan hp = new HocPhan(ma, monHocDuocChon[0].getMaMonHoc(),
                    hocKy, namHoc, nhom, trangThai);

            try {
                if (isEdit) {
                    if (hocPhanRepository.suaHocPhan(hp) > 0) {
                        Toast.makeText(this, "Cập nhật học phần thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Không tìm thấy học phần", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hocPhanRepository.themHocPhan(hp);
                    Toast.makeText(this, "Thêm học phần thành công", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                taiDanhSach();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    // ================= TRẠNG THÁI =================

    private void chonTrangThai(HocPhan hp) {
        if (TrangThaiHocPhan.DA_KET_THUC.equals(hp.getTrangThai())) {
            Toast.makeText(this, "Học phần đã kết thúc, không thể đổi trạng thái", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] trangThai = TrangThaiHocPhan.TAT_CA;
        int viTriHienTai = 0;

        for (int i = 0; i < trangThai.length; i++) {
            if (trangThai[i].equals(hp.getTrangThai())) {
                viTriHienTai = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Trạng thái học phần " + hp.getMaHocPhan())
                .setSingleChoiceItems(trangThai, viTriHienTai, (dialog, which) -> {
                    String moi = trangThai[which];
                    dialog.dismiss();

                    if (moi.equals(hp.getTrangThai())) {
                        return;
                    }

                    if (TrangThaiHocPhan.DA_KET_THUC.equals(moi)) {
                        new AlertDialog.Builder(this)
                                .setTitle("Kết thúc học phần")
                                .setMessage("Sau khi kết thúc sẽ không thể mở lại. Tiếp tục?")
                                .setNegativeButton("Hủy", null)
                                .setPositiveButton("Kết thúc", (d, w) -> capNhatTrangThai(hp, moi))
                                .show();
                    } else {
                        capNhatTrangThai(hp, moi);
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void capNhatTrangThai(HocPhan hp, String trangThaiMoi) {
        try {
            if (hocPhanRepository.capNhatTrangThai(hp.getMaHocPhan(), trangThaiMoi) > 0) {
                Toast.makeText(this, "Đã chuyển sang \"" + trangThaiMoi + "\"", Toast.LENGTH_SHORT).show();
                taiDanhSach();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ================= CHI TIẾT / XÓA =================

    private void hienThiChiTiet(HocPhan hp) {
        String noiDung =
                "Mã học phần: " + hp.getMaHocPhan() +
                        "\nMôn học: " + hp.getTenMonHoc() + " (" + hp.getMaMonHoc() + ")" +
                        "\nSố tín chỉ: " + hp.getSoTinChi() +
                        "\nHọc kỳ: " + hp.getHocKy() +
                        "\nNăm học: " + hp.getNamHoc() +
                        "\nNhóm: " + hp.getNhomHocPhan() +
                        "\nTrạng thái: " + hp.getTrangThai() +
                        "\nSố SV đã đăng ký: " + hocPhanRepository.demSinhVienDangKy(hp.getMaHocPhan());

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Chi tiết học phần")
                .setMessage(noiDung)
                .setPositiveButton("Đóng", null);

        if (!TrangThaiHocPhan.DA_KET_THUC.equals(hp.getTrangThai())) {
            builder.setNeutralButton("Đổi trạng thái", (d, w) -> chonTrangThai(hp));
        }

        builder.show();
    }

    private void xacNhanXoa(HocPhan hp) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa học phần " + hp.getMaHocPhan()
                        + " - " + hp.getTenMonHoc() + " (" + hp.getNhomHocPhan() + ") không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    try {
                        if (hocPhanRepository.xoaHocPhan(hp.getMaHocPhan())) {
                            Toast.makeText(this, "Xóa học phần thành công", Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } else {
                            Toast.makeText(this, "Không tìm thấy học phần cần xóa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private String layChuoi(TextInputEditText edt) {
        return edt.getText() == null ? "" : edt.getText().toString().trim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hocPhanRepository != null) {
            hocPhanRepository.close();
        }
        if (monHocRepository != null) {
            monHocRepository.close();
        }
    }
}
