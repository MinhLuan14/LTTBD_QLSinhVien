package vhu.edu.edusync.ui.admin.subject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.adapter.SubjectAdapter;
import vhu.edu.edusync.data.repository.MonHocRepository;
import vhu.edu.edusync.model.MonHoc;

public class SubjectListActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtSearchSubject;
    private RecyclerView rvSubjectList;
    private LinearLayout llEmptyState;
    private FloatingActionButton fabAddSubject;

    private SubjectAdapter adapter;
    private MonHocRepository monHocRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        monHocRepository = new MonHocRepository(this);

        anhXa();
        cauHinhRecyclerView();
        xuLySuKien();
    }

    private void anhXa() {
        btnBack = findViewById(R.id.btnBack);
        edtSearchSubject = findViewById(R.id.edtSearchSubject);
        rvSubjectList = findViewById(R.id.rvSubjectList);
        llEmptyState = findViewById(R.id.llEmptyState);
        fabAddSubject = findViewById(R.id.fabAddSubject);
    }

    private void cauHinhRecyclerView() {
        rvSubjectList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SubjectAdapter(new ArrayList<>(), new SubjectAdapter.OnSubjectActionListener() {
            @Override
            public void onEdit(MonHoc mh) {
                moFormSua(mh);
            }

            @Override
            public void onDelete(MonHoc mh) {
                xacNhanXoa(mh);
            }
        });

        rvSubjectList.setAdapter(adapter);
    }

    private void xuLySuKien() {
        btnBack.setOnClickListener(v -> finish());

        fabAddSubject.setOnClickListener(v ->
                startActivity(new Intent(this, SubjectFormActivity.class))
        );

        edtSearchSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taiDanhSach();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void taiDanhSach() {
        List<MonHoc> ketQua = monHocRepository.timKiem(
                edtSearchSubject.getText().toString()
        );

        adapter.setData(ketQua);

        boolean rong = ketQua.isEmpty();
        llEmptyState.setVisibility(rong ? View.VISIBLE : View.GONE);
        rvSubjectList.setVisibility(rong ? View.GONE : View.VISIBLE);
    }

    private void moFormSua(MonHoc mh) {
        Intent intent = new Intent(this, SubjectFormActivity.class);
        intent.putExtra(SubjectFormActivity.EXTRA_MA_MON_HOC, mh.getMaMonHoc());
        startActivity(intent);
    }

    private void xacNhanXoa(MonHoc mh) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa môn học \"" + mh.getTenMonHoc() + "\" không?")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xóa", (dialog, which) -> {
                    try {
                        if (monHocRepository.xoaMonHoc(mh.getMaMonHoc())) {
                            Toast.makeText(this, "Xóa môn học thành công", Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } else {
                            Toast.makeText(this, "Không tìm thấy môn học cần xóa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhSach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monHocRepository != null) {
            monHocRepository.close();
        }
    }
}
