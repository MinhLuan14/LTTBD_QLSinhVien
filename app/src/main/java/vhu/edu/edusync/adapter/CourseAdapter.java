package vhu.edu.edusync.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.model.HocPhan;
import vhu.edu.edusync.model.TrangThaiHocPhan;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<HocPhan> danhSachHocPhan;
    private final OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onDetail(HocPhan hp);
        void onEdit(HocPhan hp);
        void onDelete(HocPhan hp);
        void onChangeStatus(HocPhan hp);
    }

    public CourseAdapter(List<HocPhan> danhSachHocPhan, OnCourseActionListener listener) {
        this.danhSachHocPhan = danhSachHocPhan;
        this.listener = listener;
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView tvCourseCode, tvCourseStatus, tvCourseName;
        TextView tvCourseSemester, tvCourseYear, tvCourseGroup;
        TextView tvCourseDetail, tvCourseEdit, tvCourseDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseStatus = itemView.findViewById(R.id.tvCourseStatus);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseSemester = itemView.findViewById(R.id.tvCourseSemester);
            tvCourseYear = itemView.findViewById(R.id.tvCourseYear);
            tvCourseGroup = itemView.findViewById(R.id.tvCourseGroup);
            tvCourseDetail = itemView.findViewById(R.id.tvCourseDetail);
            tvCourseEdit = itemView.findViewById(R.id.tvCourseEdit);
            tvCourseDelete = itemView.findViewById(R.id.tvCourseDelete);
        }
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        HocPhan hp = danhSachHocPhan.get(position);

        holder.tvCourseCode.setText(hp.getMaHocPhan() + " • " + hp.getMaMonHoc());
        holder.tvCourseName.setText(hp.getTenMonHoc() + " (" + hp.getSoTinChi() + " TC)");
        holder.tvCourseSemester.setText("Học kỳ " + hp.getHocKy());
        holder.tvCourseYear.setText(hp.getNamHoc());
        holder.tvCourseGroup.setText(hp.getNhomHocPhan() == null ? "-" : hp.getNhomHocPhan());

        hienThiTrangThai(holder.tvCourseStatus, hp.getTrangThai());

        holder.tvCourseStatus.setOnClickListener(v -> {
            if (listener != null) listener.onChangeStatus(hp);
        });
        holder.tvCourseDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetail(hp);
        });
        holder.tvCourseEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(hp);
        });
        holder.tvCourseDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(hp);
        });
    }

    // Màu badge theo trạng thái
    private void hienThiTrangThai(TextView tv, String trangThai) {
        int mauChu;
        int mauNen;

        if (TrangThaiHocPhan.DANG_MO.equals(trangThai)) {
            mauChu = R.color.success;
            mauNen = R.color.success_light;
        } else if (TrangThaiHocPhan.DA_DONG.equals(trangThai)) {
            mauChu = R.color.warning;
            mauNen = R.color.warning_light;
        } else {
            mauChu = R.color.text_muted;
            mauNen = R.color.divider;
        }

        tv.setText(trangThai);
        tv.setTextColor(ContextCompat.getColor(tv.getContext(), mauChu));
        ViewCompat.setBackgroundTintList(
                tv,
                ColorStateList.valueOf(ContextCompat.getColor(tv.getContext(), mauNen))
        );
    }

    @Override
    public int getItemCount() {
        return danhSachHocPhan == null ? 0 : danhSachHocPhan.size();
    }

    public void setData(List<HocPhan> danhSachMoi) {
        danhSachHocPhan = danhSachMoi;
        notifyDataSetChanged();
    }
}
