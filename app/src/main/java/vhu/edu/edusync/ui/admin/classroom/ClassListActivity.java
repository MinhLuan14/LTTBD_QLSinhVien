package vhu.edu.edusync.ui.admin.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vhu.edu.edusync.R;
import vhu.edu.edusync.ui.admin.main.MainActivity;

/**
 * Activity hiển thị danh sách lớp học
 */
public class ClassListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private FloatingActionButton fabAddClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_class_list);

        View header = findViewById(R.id.headerContainer);
        if (header != null) {
            ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(v.getPaddingLeft(), systemBars.top + 16, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
        }

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (isTaskRoot()) {
                    Intent intent = new Intent(ClassListActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
            });
        }

        fabAddClass = findViewById(R.id.fabAddClass);
        if (fabAddClass != null) {
            fabAddClass.setOnClickListener(v ->
                    Toast.makeText(this, "Chức năng Thêm lớp học đang được kết nối", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
