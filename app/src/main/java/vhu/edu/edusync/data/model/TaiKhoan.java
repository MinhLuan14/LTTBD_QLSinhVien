package vhu.edu.edusync.data.model;

import java.io.Serializable;

/**
 * Model đại diện cho thực thể TaiKhoan
 * Khớp với cấu trúc bảng TaiKhoan trong DatabaseHelper:
 * maTaiKhoan TEXT PRIMARY KEY,
 * tenDangNhap TEXT NOT NULL UNIQUE,
 * matKhau TEXT NOT NULL,
 * vaiTro TEXT NOT NULL CHECK (vaiTro IN ('ADMIN', 'SINHVIEN')),
 * trangThai INTEGER NOT NULL DEFAULT 1 CHECK (trangThai IN (0, 1)),
 * maSinhVien TEXT UNIQUE REFERENCES SinhVien(maSinhVien)
 */
public class TaiKhoan implements Serializable {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SINHVIEN = "SINHVIEN";

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_LOCKED = 0;

    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // "ADMIN" hoặc "SINHVIEN"
    private int trangThai; // 1: Hoạt động, 0: Bị khóa
    private String maSinhVien; // null nếu là ADMIN

    // Trường phụ trợ hiển thị khi JOIN với bảng SinhVien & LopHoc
    private String hoTenSinhVien;
    private String tenLop;
    private String email;

    public TaiKhoan() {
        this.trangThai = STATUS_ACTIVE;
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, String vaiTro, int trangThai, String maSinhVien) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.maSinhVien = maSinhVien;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getHoTenSinhVien() {
        return hoTenSinhVien;
    }

    public void setHoTenSinhVien(String hoTenSinhVien) {
        this.hoTenSinhVien = hoTenSinhVien;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Helper methods
    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(this.vaiTro);
    }

    public boolean isSinhVien() {
        return ROLE_SINHVIEN.equalsIgnoreCase(this.vaiTro);
    }

    public boolean isActive() {
        return this.trangThai == STATUS_ACTIVE;
    }

    public boolean isLocked() {
        return this.trangThai == STATUS_LOCKED;
    }
}
