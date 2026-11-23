
package vhu.edu.edusync.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.model.Khoa;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private List<Khoa> danhSachKhoa;
    private final OnDepartmentActionListener listener;

    // Interface xử lý sự kiện Sửa và Xóa
    public interface OnDepartmentActionListener {
        void onEdit(Khoa khoa);
        void onDelete(Khoa khoa);
    }

    // Constructor
    public DepartmentAdapter(
            List<Khoa> danhSachKhoa,
            OnDepartmentActionListener listener) {

        this.danhSachKhoa = danhSachKhoa;
        this.listener = listener;
    }

    // ViewHolder: quản lý giao diện của từng thẻ Khoa
    public static class DepartmentViewHolder extends RecyclerView.ViewHolder {

        TextView tvDepartmentName;
        TextView tvDepartmentCode;
        TextView tvDepartmentDescription;
        TextView tvDepartmentMajorCount;

        MaterialButton btnEditDepartment;
        MaterialButton btnDeleteDepartment;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDepartmentName = itemView.findViewById(R.id.tvDepartmentName);
            tvDepartmentCode = itemView.findViewById(R.id.tvDepartmentCode);
            tvDepartmentDescription = itemView.findViewById(R.id.tvDepartmentDescription);
            tvDepartmentMajorCount = itemView.findViewById(R.id.tvDepartmentMajorCount);

            btnEditDepartment = itemView.findViewById(R.id.btnEditDepartment);
            btnDeleteDepartment = itemView.findViewById(R.id.btnDeleteDepartment);
        }
    }

    // Tạo giao diện cho từng item
    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department, parent, false);

        return new DepartmentViewHolder(view);
    }

    // Gán dữ liệu Khoa vào giao diện
    @Override
    public void onBindViewHolder(
            @NonNull DepartmentViewHolder holder,
            int position) {

        Khoa khoa = danhSachKhoa.get(position);

        holder.tvDepartmentName.setText(khoa.getTenKhoa());
        holder.tvDepartmentCode.setText("Mã khoa: " + khoa.getMaKhoa());

        String moTa = khoa.getMoTa();

        if (moTa == null || moTa.trim().isEmpty()) {
            holder.tvDepartmentDescription.setText("Chưa có mô tả");
        } else {
            holder.tvDepartmentDescription.setText(moTa);
        }

        // Hiện số chuyên ngành (sẽ cập nhật khi tích hợp dữ liệu đếm)
        holder.tvDepartmentMajorCount.setText("Số chuyên ngành: --");

        // Xử lý nút Sửa
        holder.btnEditDepartment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(khoa);
            }
        });

        // Xử lý nút Xóa
        holder.btnDeleteDepartment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(khoa);
            }
        });
    }

    // Trả về số lượng Khoa
    @Override
    public int getItemCount() {
        return danhSachKhoa == null ? 0 : danhSachKhoa.size();
    }

    // Cập nhật danh sách Khoa
    public void setData(List<Khoa> danhSachMoi) {
        this.danhSachKhoa = danhSachMoi;
        notifyDataSetChanged();
    }
}
