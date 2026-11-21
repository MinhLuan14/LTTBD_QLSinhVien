
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
import vhu.edu.edusync.model.ChuyenNganh;
import vhu.edu.edusync.model.Khoa;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.MajorViewHolder> {

    private List<ChuyenNganh> danhSachChuyenNganh;
    private final List<Khoa> danhSachKhoa;
    private final OnMajorActionListener listener;

    public interface OnMajorActionListener {
        void onEdit(ChuyenNganh cn);
        void onDelete(ChuyenNganh cn);
    }

    public MajorAdapter(
            List<ChuyenNganh> danhSachChuyenNganh,
            List<Khoa> danhSachKhoa,
            OnMajorActionListener listener
    ) {
        this.danhSachChuyenNganh = danhSachChuyenNganh;
        this.danhSachKhoa = danhSachKhoa;
        this.listener = listener;
    }

    public static class MajorViewHolder extends RecyclerView.ViewHolder {

        TextView tvMajorName, tvMajorCode;
        TextView tvMajorDepartment, tvMajorDescription;
        MaterialButton btnEditMajor, btnDeleteMajor;

        public MajorViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMajorName = itemView.findViewById(R.id.tvMajorName);
            tvMajorCode = itemView.findViewById(R.id.tvMajorCode);
            tvMajorDepartment = itemView.findViewById(R.id.tvMajorDepartment);
            tvMajorDescription = itemView.findViewById(R.id.tvMajorDescription);
            btnEditMajor = itemView.findViewById(R.id.btnEditMajor);
            btnDeleteMajor = itemView.findViewById(R.id.btnDeleteMajor);
        }
    }

    @NonNull
    @Override
    public MajorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_major, parent, false);

        return new MajorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MajorViewHolder holder, int position
    ) {
        ChuyenNganh cn = danhSachChuyenNganh.get(position);

        holder.tvMajorName.setText(cn.getTenChuyenNganh());
        holder.tvMajorCode.setText("Mã chuyên ngành: " + cn.getMaChuyenNganh());

        String tenKhoa = "Không xác định";

        for (Khoa khoa : danhSachKhoa) {
            if (khoa.getMaKhoa().equals(cn.getMaKhoa())) {
                tenKhoa = khoa.getTenKhoa();
                break;
            }
        }

        holder.tvMajorDepartment.setText("Khoa: " + tenKhoa);

        String moTa = cn.getMoTa();

        if (moTa == null || moTa.trim().isEmpty()) {
            holder.tvMajorDescription.setText("Chưa có mô tả");
        } else {
            holder.tvMajorDescription.setText(moTa);
        }

        holder.btnEditMajor.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(cn);
            }
        });

        holder.btnDeleteMajor.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(cn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachChuyenNganh == null
                ? 0 : danhSachChuyenNganh.size();
    }

    public void setData(List<ChuyenNganh> danhSachMoi) {
        danhSachChuyenNganh = danhSachMoi;
        notifyDataSetChanged();
    }
}
