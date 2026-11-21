
package vhu.edu.edusync.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vhu.edu.edusync.R;
import vhu.edu.edusync.ui.admin.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> dangNhap());
    }

    private void dangNhap() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ thông tin",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Tài khoản demo
        if (username.equals("admin123")
                && password.equals("admin123")) {

            Toast.makeText(this,
                    "Đăng nhập thành công!",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
