package vhu.edu.edusync.ui.admin.course;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.ui.admin.main.MainActivity;

/**
 * Activity hiển thị danh sách học phần
 */
public class CourseListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTotalCourse;
    private MaterialButton btnAddCourse;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_course_list);

        databaseHelper = new DatabaseHelper(this);

        View root = findViewById(R.id.rootCourseList);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (isTaskRoot()) {
                    Intent intent = new Intent(CourseListActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
            });
        }

        tvTotalCourse = findViewById(R.id.tvTotalCourse);
        btnAddCourse = findViewById(R.id.btnAddCourse);

        if (btnAddCourse != null) {
            btnAddCourse.setOnClickListener(v ->
                    Toast.makeText(this, "Chức năng Thêm học phần đang được xử lý", Toast.LENGTH_SHORT).show()
            );
        }

        loadCourseStats();
    }

    private void loadCourseStats() {
        if (tvTotalCourse == null || databaseHelper == null) return;
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_HOC_PHAN, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    tvTotalCourse.setText(String.valueOf(cursor.getInt(0)));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
