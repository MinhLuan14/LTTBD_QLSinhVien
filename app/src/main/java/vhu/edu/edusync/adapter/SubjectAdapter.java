package vhu.edu.edusync.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.model.MonHoc;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<MonHoc> danhSachMonHoc;
    private final OnSubjectActionListener listener;

    public interface OnSubjectActionListener {
        void onEdit(MonHoc mh);       // Nhấn: sửa
        void onDelete(MonHoc mh);     // Nhấn giữ: xóa
    }

    public SubjectAdapter(List<MonHoc> danhSachMonHoc, OnSubjectActionListener listener) {
        this.danhSachMonHoc = danhSachMonHoc;
        this.listener = listener;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {

        TextView tvSubjectName, tvSubjectCredits;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCredits = itemView.findViewById(R.id.tvSubjectCredits);
        }
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        MonHoc mh = danhSachMonHoc.get(position);

        holder.tvSubjectName.setText(mh.getTenMonHoc());

        StringBuilder thongTin = new StringBuilder();
        thongTin.append(mh.getMaMonHoc())
                .append(" • ")
                .append(mh.getSoTinChi())
                .append(" tín chỉ");

        if (mh.getLoaiMonHoc() != null && !mh.getLoaiMonHoc().isEmpty()) {
            thongTin.append(" • ").append(mh.getLoaiMonHoc());
        }

        holder.tvSubjectCredits.setText(thongTin.toString());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(mh);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDelete(mh);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return danhSachMonHoc == null ? 0 : danhSachMonHoc.size();
    }

    public void setData(List<MonHoc> danhSachMoi) {
        danhSachMonHoc = danhSachMoi;
        notifyDataSetChanged();
    }
}
