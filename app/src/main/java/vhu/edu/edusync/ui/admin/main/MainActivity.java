
package vhu.edu.edusync.ui.admin.main;

import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.ui.admin.classroom.ClassListActivity;
import vhu.edu.edusync.ui.admin.department.DepartmentListActivity;
import vhu.edu.edusync.ui.admin.major.MajorListActivity;
import vhu.edu.edusync.ui.admin.student.StudentListActivity;
import vhu.edu.edusync.ui.admin.subject.SubjectListActivity;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private MaterialCardView btnQLSinhVien;
    private MaterialCardView btnQLLopHoc;
    private MaterialCardView btnQLMonHoc;
    private MaterialCardView btnQLHocPhan;
    private MaterialCardView btnQLKhoa;
    private MaterialCardView btnChuyenNganh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        // Khởi tạo database
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Ánh xạ các nút menu
        btnQLSinhVien = findViewById(R.id.btnQLSinhVien);
        btnQLLopHoc = findViewById(R.id.btnQLLopHoc);
        btnQLMonHoc = findViewById(R.id.btnQLMonHoc);
        btnQLHocPhan = findViewById(R.id.btnQLHocPhan);
        btnQLKhoa = findViewById(R.id.btnQLKhoa);
        btnChuyenNganh = findViewById(R.id.btnQLChuyenNganh);

        // Chuyển trang quản lý sinh viên
        btnQLSinhVien.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    StudentListActivity.class
            );
            startActivity(intent);
        });
        // Chuyển trang quản lý lớp học
        btnQLLopHoc.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
            startActivity(intent);
        });

// Chuyển trang quản lý môn học
        btnQLMonHoc.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
            startActivity(intent);
        });

// Chuyển trang quản lý học phần
        btnQLHocPhan.setOnClickListener(v -> {
           // Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
           // startActivity(intent);
        });

// Chuyển trang quản lý khoa
        btnQLKhoa.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DepartmentListActivity.class);
            startActivity(intent);
        });

// Chuyển trang quản lý chuyên ngành
        btnChuyenNganh.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MajorListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
