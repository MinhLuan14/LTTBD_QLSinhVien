package vhu.edu.edusync.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.SinhVien;

/**
 * Adapter hiển thị danh sách sinh viên lên RecyclerView
 * Khớp với layout item_student.xml
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<SinhVien> danhSachSinhVien;
    private final OnStudentActionListener listener;

    /**
     * Interface lắng nghe các sự kiện thao tác trên từng sinh viên
     */
    public interface OnStudentActionListener {
        void onEdit(SinhVien sv);
        void onDelete(SinhVien sv);
        void onItemClick(SinhVien sv);
    }

    public StudentAdapter(List<SinhVien> danhSachSinhVien, OnStudentActionListener listener) {
        this.danhSachSinhVien = danhSachSinhVien;
        this.listener = listener;
    }

    /**
     * Cập nhật danh sách mới và làm mới giao diện
     */
    public void setData(List<SinhVien> newList) {
        this.danhSachSinhVien = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        SinhVien sv = danhSachSinhVien.get(position);
        if (sv == null) return;

        // 1. Hiển thị thông tin cơ bản
        holder.tvStudentName.setText(sv.getHoTen());
        holder.tvStudentId.setText(sv.getMaSinhVien());

        String tenLop = sv.getTenLop();
        if (tenLop != null && !tenLop.isEmpty()) {
            holder.tvClassName.setText(tenLop);
        } else if (sv.getMaLop() != null && !sv.getMaLop().isEmpty()) {
            holder.tvClassName.setText(sv.getMaLop());
        } else {
            holder.tvClassName.setText("Chưa phân lớp");
        }

        // 2. Hiển thị ảnh đại diện (nếu có lưu đường dẫn hoặc URI)
        hienThiAnhDaiDien(holder.imgStudentAvatar, sv.getAnhDaiDien());

        // 3. Xử lý các sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sv);
            }
        });

        holder.btnEditStudent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(sv);
            }
        });

        holder.btnDeleteStudent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(sv);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachSinhVien != null ? danhSachSinhVien.size() : 0;
    }

    /**
     * Hàm phụ nạp ảnh đại diện từ đường dẫn máy hoặc dùng ảnh mặc định
     */
    private void hienThiAnhDaiDien(ImageView imgView, String duongDanAnh) {
        if (duongDanAnh != null && !duongDanAnh.trim().isEmpty()) {
            try {
                File file = new File(duongDanAnh);
                if (file.exists()) {
                    imgView.setImageURI(Uri.fromFile(file));
                    imgView.clearColorFilter();
                    return;
                } else {
                    Uri uri = Uri.parse(duongDanAnh);
                    imgView.setImageURI(uri);
                    imgView.clearColorFilter();
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // Ảnh đại diện mặc định
        imgView.setImageResource(R.drawable.ic_student);
        imgView.setColorFilter(ContextCompat.getColor(imgView.getContext(), R.color.primary_blue));
    }

    /**
     * ViewHolder quản lý các View trong file item_student.xml
     */
    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgStudentAvatar;
        TextView tvStudentName;
        TextView tvStudentId;
        TextView tvClassName;
        ImageButton btnEditStudent;
        ImageButton btnDeleteStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStudentAvatar = itemView.findViewById(R.id.imgStudentAvatar);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            btnEditStudent = itemView.findViewById(R.id.btnEditStudent);
            btnDeleteStudent = itemView.findViewById(R.id.btnDeleteStudent);
        }
    }
}
