package vhu.edu.edusync.ui.admin.grade;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.adapter.GradeAdapter;
import vhu.edu.edusync.data.model.GradeItem;
import vhu.edu.edusync.R;

public class GradeLookupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_grade_lookup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootGradeLookup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.appBarLayout).setPadding(0, systemBars.top, 0, 0);
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (isTaskRoot()) {
                android.content.Intent intent = new android.content.Intent(GradeLookupActivity.this, vhu.edu.edusync.ui.admin.main.MainActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            finish();
        });

        // Cấu hình danh sách điểm (RecyclerView)
        RecyclerView rvGrades = findViewById(R.id.rvGrades);
        rvGrades.setLayoutManager(new LinearLayoutManager(this));

        List<GradeItem> sampleGrades = new ArrayList<>();
        sampleGrades.add(new GradeItem("Lập trình Java", "8.5", "3"));
        sampleGrades.add(new GradeItem("CSDL", "8.0", "3"));
        sampleGrades.add(new GradeItem("Mạng máy tính", "8.7", "3"));

        GradeAdapter adapter = new GradeAdapter(sampleGrades);
        rvGrades.setAdapter(adapter);
    }
}
