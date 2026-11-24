package vhu.edu.edusync.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import vhu.edu.edusync.R;
import vhu.edu.edusync.data.model.TaiKhoan;

/**
 * Adapter hiển thị danh sách tài khoản lên RecyclerView
 * Khớp với item_account.xml
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<TaiKhoan> danhSachTaiKhoan;
    private final String currentLoggedInUsername;
    private final OnAccountActionListener listener;

    public interface OnAccountActionListener {
        void onToggleLock(TaiKhoan account);
        void onResetPassword(TaiKhoan account);
        void onItemClick(TaiKhoan account);
    }

    public AccountAdapter(List<TaiKhoan> danhSachTaiKhoan, String currentLoggedInUsername, OnAccountActionListener listener) {
        this.danhSachTaiKhoan = danhSachTaiKhoan;
        this.currentLoggedInUsername = currentLoggedInUsername;
        this.listener = listener;
    }

    public void setData(List<TaiKhoan> newList) {
        this.danhSachTaiKhoan = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        TaiKhoan account = danhSachTaiKhoan.get(position);
        if (account == null) return;

        // 1. Tên đăng nhập
        holder.tvAccountUsername.setText(account.getTenDangNhap());

        // 2. Phân vai trò
        if (account.isAdmin()) {
            holder.tvAccountRoleBadge.setText("ADMIN");
            holder.tvAccountRoleBadge.setTextColor(Color.WHITE);
            holder.tvAccountRoleBadge.setBackgroundResource(R.drawable.bg_badge_admin);
            holder.tvAccountFullName.setText("Quản trị viên hệ thống");
            holder.tvAccountDetailInfo.setText("Mã TK: " + account.getMaTaiKhoan());
            holder.imgAccountAvatar.setImageResource(R.drawable.ic_person);
            holder.imgAccountAvatar.setBackgroundResource(R.drawable.bg_circle_purple);
        } else {
            holder.tvAccountRoleBadge.setText("SINH VIÊN");
            holder.tvAccountRoleBadge.setTextColor(Color.parseColor("#3730A3"));
            holder.tvAccountRoleBadge.setBackgroundResource(R.drawable.bg_badge_student);
            String fullName = account.getHoTenSinhVien();
            holder.tvAccountFullName.setText(fullName != null && !fullName.isEmpty() ? fullName : "Sinh viên");

            String mssv = account.getMaSinhVien() != null ? account.getMaSinhVien() : "--";
            String lop = account.getTenLop() != null ? " • Lớp: " + account.getTenLop() : "";
            holder.tvAccountDetailInfo.setText("MSSV: " + mssv + lop);
            holder.imgAccountAvatar.setImageResource(R.drawable.ic_student);
            holder.imgAccountAvatar.setBackgroundResource(R.drawable.bg_circle_blue);
        }

        // 3. Trạng thái (Hoạt động / Bị khóa)
        boolean isCurrentUser = currentLoggedInUsername != null &&
                currentLoggedInUsername.equalsIgnoreCase(account.getTenDangNhap());

        if (account.isActive()) {
            holder.tvAccountStatusBadge.setText("Hoạt động");
            holder.tvAccountStatusBadge.setTextColor(Color.parseColor("#059669"));
            holder.tvAccountStatusBadge.setBackgroundResource(R.drawable.bg_badge_active);

            holder.btnToggleLock.setText("Khóa");
            holder.btnToggleLock.setTextColor(Color.parseColor("#DC2626"));
            holder.btnToggleLock.setStrokeColorResource(R.color.error);
        } else {
            holder.tvAccountStatusBadge.setText("Đã khóa");
            holder.tvAccountStatusBadge.setTextColor(Color.parseColor("#DC2626"));
            holder.tvAccountStatusBadge.setBackgroundResource(R.drawable.bg_badge_locked);

            holder.btnToggleLock.setText("Mở khóa");
            holder.btnToggleLock.setTextColor(Color.parseColor("#059669"));
            holder.btnToggleLock.setStrokeColorResource(R.color.success);
        }

        // Không cho phép tự khóa chính tài khoản admin đang đăng nhập
        if (isCurrentUser) {
            holder.btnToggleLock.setVisibility(View.GONE);
        } else {
            holder.btnToggleLock.setVisibility(View.VISIBLE);
        }

        // 4. Sự kiện click
        holder.btnToggleLock.setOnClickListener(v -> {
            if (listener != null) listener.onToggleLock(account);
        });

        holder.btnResetPassword.setOnClickListener(v -> {
            if (listener != null) listener.onResetPassword(account);
        });

        holder.cardAccountItem.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(account);
        });
    }

    @Override
    public int getItemCount() {
        return danhSachTaiKhoan != null ? danhSachTaiKhoan.size() : 0;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAccountItem;
        ImageView imgAccountAvatar;
        TextView tvAccountUsername;
        TextView tvAccountRoleBadge;
        TextView tvAccountFullName;
        TextView tvAccountStatusBadge;
        TextView tvAccountDetailInfo;
        MaterialButton btnResetPassword;
        MaterialButton btnToggleLock;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAccountItem = itemView.findViewById(R.id.cardAccountItem);
            imgAccountAvatar = itemView.findViewById(R.id.imgAccountAvatar);
            tvAccountUsername = itemView.findViewById(R.id.tvAccountUsername);
            tvAccountRoleBadge = itemView.findViewById(R.id.tvAccountRoleBadge);
            tvAccountFullName = itemView.findViewById(R.id.tvAccountFullName);
            tvAccountStatusBadge = itemView.findViewById(R.id.tvAccountStatusBadge);
            tvAccountDetailInfo = itemView.findViewById(R.id.tvAccountDetailInfo);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
            btnToggleLock = itemView.findViewById(R.id.btnToggleLock);
        }
    }
}
