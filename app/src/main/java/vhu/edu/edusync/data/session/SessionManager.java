package vhu.edu.edusync.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import vhu.edu.edusync.data.model.TaiKhoan;

/**
 * Quản lý phiên đăng nhập của người dùng qua SharedPreferences:
 * - Lưu trạng thái đăng nhập, vai trò, thông tin người dùng
 * - Hỗ trợ "Ghi nhớ đăng nhập"
 * - Kiểm tra phân quyền truy cập
 * - Xóa phiên khi đăng xuất
 */
public class SessionManager {

    private static final String PREF_NAME = "EduSyncSession";
    private static final int PRIVATE_MODE = 0;

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_MA_TAI_KHOAN = "maTaiKhoan";
    private static final String KEY_TEN_DANG_NHAP = "tenDangNhap";
    private static final String KEY_VAI_TRO = "vaiTro";
    private static final String KEY_MA_SINH_VIEN = "maSinhVien";
    private static final String KEY_HO_TEN = "hoTen";
    private static final String KEY_REMEMBER_LOGIN = "rememberLogin";
    private static final String KEY_SAVED_USERNAME = "savedUsername";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Lưu phiên đăng nhập người dùng
     */
    public void saveSession(TaiKhoan account, boolean rememberMe) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_MA_TAI_KHOAN, account.getMaTaiKhoan());
        editor.putString(KEY_TEN_DANG_NHAP, account.getTenDangNhap());
        editor.putString(KEY_VAI_TRO, account.getVaiTro());
        editor.putString(KEY_MA_SINH_VIEN, account.getMaSinhVien());
        editor.putString(KEY_HO_TEN, account.getHoTenSinhVien() != null ? account.getHoTenSinhVien() : account.getTenDangNhap());
        editor.putBoolean(KEY_REMEMBER_LOGIN, rememberMe);

        if (rememberMe) {
            editor.putString(KEY_SAVED_USERNAME, account.getTenDangNhap());
        } else {
            editor.remove(KEY_SAVED_USERNAME);
        }

        editor.apply();
    }

    /**
     * Cập nhật thông tin họ tên hiển thị trong session
     */
    public void updateDisplayName(String newName) {
        editor.putString(KEY_HO_TEN, newName);
        editor.apply();
    }

    /**
     * Kiểm tra người dùng đã đăng nhập hay chưa
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Kiểm tra có ghi nhớ đăng nhập không
     */
    public boolean isRememberLogin() {
        return pref.getBoolean(KEY_REMEMBER_LOGIN, false);
    }

    public String getSavedUsername() {
        return pref.getString(KEY_SAVED_USERNAME, "");
    }

    public String getMaTaiKhoan() {
        return pref.getString(KEY_MA_TAI_KHOAN, "");
    }

    public String getTenDangNhap() {
        return pref.getString(KEY_TEN_DANG_NHAP, "");
    }

    public String getUsername() {
        return getTenDangNhap();
    }

    public String getVaiTro() {
        return pref.getString(KEY_VAI_TRO, "");
    }

    public String getMaSinhVien() {
        return pref.getString(KEY_MA_SINH_VIEN, null);
    }

    public String getDisplayName() {
        return pref.getString(KEY_HO_TEN, getTenDangNhap());
    }

    public boolean isAdmin() {
        return TaiKhoan.ROLE_ADMIN.equalsIgnoreCase(getVaiTro());
    }

    public boolean isSinhVien() {
        return TaiKhoan.ROLE_SINHVIEN.equalsIgnoreCase(getVaiTro());
    }

    /**
     * Đăng xuất - xóa sạch phiên đăng nhập hiện tại
     */
    public void logout() {
        boolean remember = isRememberLogin();
        String savedUser = getSavedUsername();

        editor.clear();

        // Giữ lại tên đăng nhập đã lưu nếu người dùng từng chọn ghi nhớ
        if (remember) {
            editor.putBoolean(KEY_REMEMBER_LOGIN, true);
            editor.putString(KEY_SAVED_USERNAME, savedUser);
        }

        editor.apply();
    }
}
