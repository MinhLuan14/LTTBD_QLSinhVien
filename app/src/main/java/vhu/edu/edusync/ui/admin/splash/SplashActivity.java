package vhu.edu.edusync.ui.admin.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vhu.edu.edusync.R;
import vhu.edu.edusync.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private Button btnBatDau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btnBatDau = findViewById(R.id.btnBatDau);

        btnBatDau.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SplashActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });
    }
}