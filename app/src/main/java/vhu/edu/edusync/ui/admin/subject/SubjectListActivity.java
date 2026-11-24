package vhu.edu.edusync.ui.admin.subject;

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
 * Activity hiển thị danh sách môn học
 */
public class SubjectListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private FloatingActionButton fabAddSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_subject_list);

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
                    Intent intent = new Intent(SubjectListActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                finish();
            });
        }

        fabAddSubject = findViewById(R.id.fabAddSubject);
        if (fabAddSubject != null) {
            fabAddSubject.setOnClickListener(v ->
                    Toast.makeText(this, "Chức năng Thêm môn học đang được kết nối", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
