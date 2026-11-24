package vhu.edu.edusync.ui.admin.main;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.data.session.SessionManager;
import vhu.edu.edusync.ui.admin.account.AccountListActivity;
import vhu.edu.edusync.ui.admin.classroom.ClassListActivity;
import vhu.edu.edusync.ui.admin.course.CourseListActivity;
import vhu.edu.edusync.ui.admin.department.DepartmentListActivity;
import vhu.edu.edusync.ui.admin.grade.GradeEntryActivity;
import vhu.edu.edusync.ui.admin.grade.GradeLookupActivity;
import vhu.edu.edusync.ui.admin.major.MajorListActivity;
import vhu.edu.edusync.ui.admin.student.StudentListActivity;
import vhu.edu.edusync.ui.admin.subject.SubjectListActivity;
import vhu.edu.edusync.ui.auth.LoginActivity;
import vhu.edu.edusync.ui.profile.ProfileActivity;

/**
 * Trang chủ Quản trị viên (Admin Dashboard)
 * - Hiển thị dữ liệu thống kê trực tiếp từ SQLite (Sinh viên, Lớp học, Môn học, Học phần)
 * - Phân quyền người dùng (chỉ ADMIN)
 * - Điều hướng chức năng quản lý danh mục và tài khoản hệ thống
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private TextView tvUserName;
    private ImageView btnMenu;
    private ImageView imgNotificationBell;

    // Các thẻ thống kê
    private MaterialCardView cardStatStudents;
    private MaterialCardView cardStatClasses;
    private MaterialCardView cardStatSubjects;
    private MaterialCardView cardStatCourses;

    private TextView tvStatCountStudents;
    private TextView tvStatCountClasses;
    private TextView tvStatCountSubjects;
    private TextView tvStatCountCourses;

    // Các nút menu truy cập nhanh
    private MaterialCardView btnQLSinhVien;
    private MaterialCardView btnQLLopHoc;
    private MaterialCardView btnQLMonHoc;
    private MaterialCardView btnQLHocPhan;
    private MaterialCardView btnQLKhoa;
    private MaterialCardView btnChuyenNganh;
    private MaterialCardView btnQLTaiKhoan;

    private ImageView btnQuickAccessArrow;
    private View rlQuickAccessHeader;
    private NestedScrollView homeScrollView;

    // Hoạt động gần đây
    private MaterialCardView cardRecentActivity1;
    private TextView btnSeeAllActivities;

    private BottomNavigationView bottomNavMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        // Kiểm tra phân quyền: Bắt buộc đăng nhập và phải là ADMIN
        if (!sessionManager.isLoggedIn() || !sessionManager.isAdmin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupListeners();
        loadDashboardStats();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        btnMenu = findViewById(R.id.btnMenu);
        imgNotificationBell = findViewById(R.id.imgNotificationBell);

        // Thống kê
        cardStatStudents = findViewById(R.id.cardStatStudents);
        cardStatClasses = findViewById(R.id.cardStatClasses);
        cardStatSubjects = findViewById(R.id.cardStatSubjects);
        cardStatCourses = findViewById(R.id.cardStatCourses);

        tvStatCountStudents = findViewById(R.id.tvStatCountStudents);
        tvStatCountClasses = findViewById(R.id.tvStatCountClasses);
        tvStatCountSubjects = findViewById(R.id.tvStatCountSubjects);
        tvStatCountCourses = findViewById(R.id.tvStatCountCourses);

        // Các nút truy cập nhanh
        btnQLSinhVien = findViewById(R.id.btnQLSinhVien);
        btnQLLopHoc = findViewById(R.id.btnQLLopHoc);
        btnQLMonHoc = findViewById(R.id.btnQLMonHoc);
        btnQLHocPhan = findViewById(R.id.btnQLHocPhan);
        btnQLKhoa = findViewById(R.id.btnQLKhoa);
        btnChuyenNganh = findViewById(R.id.btnQLChuyenNganh);
        btnQLTaiKhoan = findViewById(R.id.btnQLTaiKhoan);

        btnQuickAccessArrow = findViewById(R.id.btnQuickAccessArrow);
        rlQuickAccessHeader = findViewById(R.id.rlQuickAccessHeader);
        homeScrollView = findViewById(R.id.homeScrollView);

        cardRecentActivity1 = findViewById(R.id.cardRecentActivity1);
        btnSeeAllActivities = findViewById(R.id.btnSeeAllActivities);

        bottomNavMain = findViewById(R.id.bottomNavMain);

        // Hiển thị tên người dùng
        if (tvUserName != null) {
            String name = sessionManager.getDisplayName();
            tvUserName.setText(name + " 👋");
        }
    }

    private void setupListeners() {
        // Menu & Thông báo -> Chuyển đến Trang cá nhân
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }
        if (imgNotificationBell != null) {
            imgNotificationBell.setOnClickListener(v -> Toast.makeText(this, "Không có thông báo mới", Toast.LENGTH_SHORT).show());
        }

        // Chuyển trang quản lý sinh viên
        if (btnQLSinhVien != null) {
            btnQLSinhVien.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StudentListActivity.class)));
        }
        if (cardStatStudents != null) {
            cardStatStudents.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StudentListActivity.class)));
        }

        // Chuyển trang quản lý lớp học
        if (btnQLLopHoc != null) {
            btnQLLopHoc.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ClassListActivity.class)));
        }
        if (cardStatClasses != null) {
            cardStatClasses.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ClassListActivity.class)));
        }

        // Chuyển trang quản lý môn học
        if (btnQLMonHoc != null) {
            btnQLMonHoc.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SubjectListActivity.class)));
        }
        if (cardStatSubjects != null) {
            cardStatSubjects.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SubjectListActivity.class)));
        }

        // Chuyển trang quản lý học phần
        if (btnQLHocPhan != null) {
            btnQLHocPhan.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CourseListActivity.class)));
        }
        if (cardStatCourses != null) {
            cardStatCourses.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CourseListActivity.class)));
        }

        // Chuyển trang quản lý khoa
        if (btnQLKhoa != null) {
            btnQLKhoa.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DepartmentListActivity.class)));
        }

        // Chuyển trang quản lý chuyên ngành
        if (btnChuyenNganh != null) {
            btnChuyenNganh.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MajorListActivity.class)));
        }

        // Chuyển trang quản lý tài khoản (Nguyên phụ trách)
        if (btnQLTaiKhoan != null) {
            btnQLTaiKhoan.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AccountListActivity.class)));
        }

        // Mũi tên và tiêu đề "Truy cập nhanh"
        View.OnClickListener quickAccessHeaderListener = v -> {
            if (homeScrollView != null && rlQuickAccessHeader != null) {
                homeScrollView.smoothScrollTo(0, rlQuickAccessHeader.getTop());
            }
            Toast.makeText(MainActivity.this, "Đang hiển thị các lối tắt truy cập nhanh", Toast.LENGTH_SHORT).show();
        };
        if (btnQuickAccessArrow != null) {
            btnQuickAccessArrow.setOnClickListener(quickAccessHeaderListener);
        }
        if (rlQuickAccessHeader != null) {
            rlQuickAccessHeader.setOnClickListener(quickAccessHeaderListener);
        }

        // Hoạt động gần đây: Nhập điểm học phần
        if (cardRecentActivity1 != null) {
            cardRecentActivity1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GradeEntryActivity.class)));
        }
        if (btnSeeAllActivities != null) {
            btnSeeAllActivities.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GradeLookupActivity.class)));
        }

        // Điều hướng Bottom Navigation
        if (bottomNavMain != null) {
            bottomNavMain.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_students) {
                    startActivity(new Intent(MainActivity.this, StudentListActivity.class));
                    return true;
                } else if (itemId == R.id.nav_statistics) {
                    startActivity(new Intent(MainActivity.this, AccountListActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Truy vấn số liệu thực tế từ SQLite và đưa lên Dashboard
     */
    private void loadDashboardStats() {
        if (databaseHelper == null) return;

        try {
            int countStudents = countTableRows(DatabaseHelper.TABLE_SINH_VIEN);
            int countClasses = countTableRows(DatabaseHelper.TABLE_LOP_HOC);
            int countSubjects = countTableRows(DatabaseHelper.TABLE_MON_HOC);
            int countCourses = countTableRows(DatabaseHelper.TABLE_HOC_PHAN);

            if (tvStatCountStudents != null) tvStatCountStudents.setText(String.valueOf(countStudents));
            if (tvStatCountClasses != null) tvStatCountClasses.setText(String.valueOf(countClasses));
            if (tvStatCountSubjects != null) tvStatCountSubjects.setText(String.valueOf(countSubjects));
            if (tvStatCountCourses != null) tvStatCountCourses.setText(String.valueOf(countCourses));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countTableRows(String tableName) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavMain != null) {
            bottomNavMain.setSelectedItemId(R.id.nav_home);
        }
        loadDashboardStats();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
