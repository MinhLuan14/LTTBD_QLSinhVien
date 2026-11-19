package vhu.edu.edusync;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class GradeEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_grade_entry);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootGradeEntry), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.appBarLayout).setPadding(0, systemBars.top, 0, 0);
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nút Lưu
        findViewById(R.id.btnSave).setOnClickListener(v ->
                Toast.makeText(this, "Lưu điểm thành công!", Toast.LENGTH_SHORT).show()
        );
    }
}
