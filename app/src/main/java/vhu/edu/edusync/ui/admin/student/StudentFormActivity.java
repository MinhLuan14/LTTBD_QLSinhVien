package vhu.edu.edusync.ui.admin.student;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.LopHoc;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.repository.LopHocRepository;
import vhu.edu.edusync.data.repository.SinhVienRepository;

/**
 * Activity xử lý thêm mới hoặc cập nhật thông tin sinh viên
 * Hỗ trợ: Chọn ảnh từ máy, chọn ngày sinh, chọn lớp học và cấp tài khoản sinh viên
 */
public class StudentFormActivity extends AppCompatActivity {

    // Views Header
    private ImageView imgBack;
    private TextView tvTitle;

    // Views Avatar
    private ImageView imgStudentAvatar;
    private ImageView imgPickAvatar;
    private TextView tvChangeAvatar;

    // Views Form
    private EditText edtStudentName;
    private EditText edtStudentId;
    private EditText edtBirthDate;
    private ImageView imgCalendar;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spClass;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtAddress;

    // Views Cấp tài khoản
    private MaterialCardView cardStudentAccount;
    private Switch switchCreateAccount;
    private LinearLayout layoutAccountInfo;
    private EditText edtUsername;
    private EditText edtInitialPassword;

    // Views Buttons
    private MaterialButton btnCancelStudent;
    private MaterialButton btnSaveStudent;

    // Repositories
    private SinhVienRepository sinhVienRepository;
    private LopHocRepository lopHocRepository;

    // Data
    private List<LopHoc> danhSachLopHoc;
    private SinhVien studentToEdit = null;
    private boolean isEditMode = false;
    private String selectedAvatarPath = null;
    private Uri tempSelectedImageUri = null;

    // Launcher chọn ảnh từ bộ nhớ máy (Android Jetpack hiện đại, không cần thư viện ngoài)
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    tempSelectedImageUri = uri;
                    hienThiAnhTamThoi(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_form);

        sinhVienRepository = new SinhVienRepository(this);
        lopHocRepository = new LopHocRepository(this);

        // Đảm bảo có sẵn lớp học mẫu nếu database chưa có
        lopHocRepository.ensureSampleClassesExist();

        anhXa();
        napDanhSachLopHoc();
        kiemTraCheDoSua();
        xuLySuKien();
    }

    private void anhXa() {
        imgBack = findViewById(R.id.imgBack);
        tvTitle = findViewById(R.id.tvTitle);

        imgStudentAvatar = findViewById(R.id.imgStudentAvatar);
        imgPickAvatar = findViewById(R.id.imgPickAvatar);
        tvChangeAvatar = findViewById(R.id.tvChangeAvatar);

        edtStudentName = findViewById(R.id.edtStudentName);
        edtStudentId = findViewById(R.id.edtStudentId);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        imgCalendar = findViewById(R.id.imgCalendar);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        spClass = findViewById(R.id.spClass);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        cardStudentAccount = findViewById(R.id.cardStudentAccount);
        switchCreateAccount = findViewById(R.id.switchCreateAccount);
        layoutAccountInfo = findViewById(R.id.layoutAccountInfo);
        edtUsername = findViewById(R.id.edtUsername);
        edtInitialPassword = findViewById(R.id.edtInitialPassword);

        btnCancelStudent = findViewById(R.id.btnCancelStudent);
        btnSaveStudent = findViewById(R.id.btnSaveStudent);
    }

    /**
     * Tải danh sách lớp học lên Spinner
     */
    private void napDanhSachLopHoc() {
        danhSachLopHoc = lopHocRepository.getAll();
        ArrayAdapter<LopHoc> classAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                danhSachLopHoc
        );
        spClass.setAdapter(classAdapter);
    }

    /**
     * Kiểm tra xem màn hình được mở để Sửa hay để Thêm mới
     */
    private void kiemTraCheDoSua() {
        studentToEdit = (SinhVien) getIntent().getSerializableExtra("STUDENT");
        if (studentToEdit != null) {
            isEditMode = true;
            tvTitle.setText("Chỉnh sửa sinh viên");

            // Điền dữ liệu cũ lên form
            edtStudentName.setText(studentToEdit.getHoTen());
            edtStudentId.setText(studentToEdit.getMaSinhVien());
            edtStudentId.setEnabled(false); // Khóa mã SV (khóa chính) không cho sửa

            edtBirthDate.setText(studentToEdit.getNgaySinh());

            if ("Nữ".equalsIgnoreCase(studentToEdit.getGioiTinh()) || "Nu".equalsIgnoreCase(studentToEdit.getGioiTinh())) {
                rbFemale.setChecked(true);
            } else {
                rbMale.setChecked(true);
            }

            edtEmail.setText(studentToEdit.getEmail());
            edtPhone.setText(studentToEdit.getSoDienThoai());
            edtAddress.setText(studentToEdit.getDiaChi());

            // Chọn đúng lớp của sinh viên
            if (studentToEdit.getMaLop() != null && danhSachLopHoc != null) {
                for (int i = 0; i < danhSachLopHoc.size(); i++) {
                    if (studentToEdit.getMaLop().equals(danhSachLopHoc.get(i).getMaLop())) {
                        spClass.setSelection(i);
                        break;
                    }
                }
            }

            // Hiển thị ảnh cũ nếu có
            selectedAvatarPath = studentToEdit.getAnhDaiDien();
            if (selectedAvatarPath != null && !selectedAvatarPath.isEmpty()) {
                File file = new File(selectedAvatarPath);
                if (file.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bmp != null) {
                        imgStudentAvatar.setImageBitmap(bmp);
                        imgStudentAvatar.clearColorFilter();
                    }
                }
            }

            // Khi sửa thì ẩn phần cấp tài khoản
            if (cardStudentAccount != null) {
                cardStudentAccount.setVisibility(View.GONE);
            }
        } else {
            isEditMode = false;
            tvTitle.setText("Thêm sinh viên mới");
            if (cardStudentAccount != null) {
                cardStudentAccount.setVisibility(View.VISIBLE);
            }
        }
    }

    private void xuLySuKien() {
        // Nút quay lại & nút Hủy
        imgBack.setOnClickListener(v -> finish());
        btnCancelStudent.setOnClickListener(v -> finish());

        // Chọn ngày sinh
        edtBirthDate.setOnClickListener(v -> hienThiDatePicker());
        imgCalendar.setOnClickListener(v -> hienThiDatePicker());

        // Chọn ảnh đại diện
        View.OnClickListener pickAvatarListener = v -> pickImageLauncher.launch("image/*");
        imgPickAvatar.setOnClickListener(pickAvatarListener);
        tvChangeAvatar.setOnClickListener(pickAvatarListener);
        imgStudentAvatar.setOnClickListener(pickAvatarListener);

        // Bật / tắt tạo tài khoản
        if (switchCreateAccount != null) {
            switchCreateAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (layoutAccountInfo != null) {
                    layoutAccountInfo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            });
        }

        // Bấm nút Lưu
        btnSaveStudent.setOnClickListener(v -> xuLyLuuSinhVien());
    }

    /**
     * Mở hộp thoại chọn ngày tháng năm sinh
     */
    private void hienThiDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = 2004; // Năm sinh đại học mặc định
        int month = 0;
        int day = 1;

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String dateFormatted = String.format("%02d/%02d/%04d", d, m + 1, y);
            edtBirthDate.setText(dateFormatted);
        }, year, month, day);

        dialog.show();
    }

    /**
     * Hiển thị xem trước ảnh đại diện vừa chọn
     */
    private void hienThiAnhTamThoi(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (is != null) is.close();

            if (bitmap != null) {
                imgStudentAvatar.setImageBitmap(bitmap);
                imgStudentAvatar.clearColorFilter();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể đọc ảnh đã chọn", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lưu ảnh vào bộ nhớ trong của ứng dụng để đảm bảo vĩnh viễn không bị mất khi thoát app
     */
    private String luuAnhVaoBoNhoTrong(Uri uri, String studentId) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File avatarDir = new File(getFilesDir(), "avatars");
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            File destFile = new File(avatarDir, "avatar_" + studentId + "_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xử lý xác thực dữ liệu và Lưu sinh viên vào SQLite
     */
    private void xuLyLuuSinhVien() {
        String hoTen = edtStudentName.getText().toString().trim();
        String maSV = edtStudentId.getText().toString().trim();
        String ngaySinh = edtBirthDate.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String soDienThoai = edtPhone.getText().toString().trim();
        String diaChi = edtAddress.getText().toString().trim();
        String gioiTinh = rbFemale.isChecked() ? "Nữ" : "Nam";

        // 1. Kiểm tra các trường bắt buộc
        if (hoTen.isEmpty()) {
            edtStudentName.setError("Vui lòng nhập họ và tên");
            edtStudentName.requestFocus();
            return;
        }

        if (maSV.isEmpty()) {
            edtStudentId.setError("Vui lòng nhập mã sinh viên");
            edtStudentId.requestFocus();
            return;
        }

        if (ngaySinh.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            hienThiDatePicker();
            return;
        }

        // Lấy mã lớp từ Spinner
        String maLop = "";
        if (spClass.getSelectedItem() != null) {
            LopHoc lopChon = (LopHoc) spClass.getSelectedItem();
            maLop = lopChon.getMaLop();
        }

        // Lưu ảnh mới vào bộ nhớ trong (nếu có chọn)
        if (tempSelectedImageUri != null) {
            String savedPath = luuAnhVaoBoNhoTrong(tempSelectedImageUri, maSV);
            if (savedPath != null) {
                selectedAvatarPath = savedPath;
            }
        }

        if (isEditMode) {
            // === CHẾ ĐỘ CHỈNH SỬA ===
            studentToEdit.setHoTen(hoTen);
            studentToEdit.setNgaySinh(ngaySinh);
            studentToEdit.setGioiTinh(gioiTinh);
            studentToEdit.setMaLop(maLop);
            studentToEdit.setEmail(email);
            studentToEdit.setSoDienThoai(soDienThoai);
            studentToEdit.setDiaChi(diaChi);
            studentToEdit.setAnhDaiDien(selectedAvatarPath);

            boolean success = sinhVienRepository.capNhatSinhVien(studentToEdit);
            if (success) {
                Toast.makeText(this, "Cập nhật sinh viên thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }

        } else {
            // === CHẾ ĐỘ THÊM MỚI ===
            // Kiểm tra trùng mã sinh viên
            if (sinhVienRepository.isMaSinhVienExists(maSV)) {
                edtStudentId.setError("Mã sinh viên này đã tồn tại trong hệ thống!");
                edtStudentId.requestFocus();
                return;
            }

            SinhVien svMoi = new SinhVien(
                    maSV, hoTen, ngaySinh, gioiTinh,
                    email, soDienThoai, diaChi, maLop, selectedAvatarPath
            );

            boolean taoTaiKhoan = switchCreateAccount != null && switchCreateAccount.isChecked();
            String username = edtUsername != null ? edtUsername.getText().toString().trim() : "";
            String password = edtInitialPassword != null ? edtInitialPassword.getText().toString().trim() : "";

            boolean success = sinhVienRepository.themSinhVien(svMoi, taoTaiKhoan, username, password);
            if (success) {
                String thongBao = "Thêm sinh viên thành công!";
                if (taoTaiKhoan) {
                    thongBao += " Đã cấp tài khoản đăng nhập.";
                }
                Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Thêm sinh viên thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sinhVienRepository != null) sinhVienRepository.close();
        if (lopHocRepository != null) lopHocRepository.close();
    }
}
