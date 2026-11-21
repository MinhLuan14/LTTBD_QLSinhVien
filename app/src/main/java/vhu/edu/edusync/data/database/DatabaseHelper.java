
package vhu.edu.edusync.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ================= DATABASE CONFIGURATION =================

    private static final String DATABASE_NAME = "EduSync.db";
    private static final int DATABASE_VERSION = 3;

    // ================= TABLE NAMES =================

    public static final String TABLE_TAI_KHOAN = "TaiKhoan";
    public static final String TABLE_KHOA = "Khoa";
    public static final String TABLE_CHUYEN_NGANH = "ChuyenNganh";
    public static final String TABLE_LOP_HOC = "LopHoc";
    public static final String TABLE_SINH_VIEN = "SinhVien";
    public static final String TABLE_MON_HOC = "MonHoc";
    public static final String TABLE_HOC_PHAN = "HocPhan";
    public static final String TABLE_DIEM_HOC_PHAN = "DiemHocPhan";
    public static final String TABLE_DANGKY_HOC_PHAN = "DangKyHocPhan";

    // ================= CONSTRUCTOR =================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ================= CONFIGURE DATABASE =================

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ================= CREATE DATABASE =================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 1. TABLE KHOA
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Khoa (" +
                        "maKhoa TEXT PRIMARY KEY, " +
                        "tenKhoa TEXT NOT NULL, " +
                        "moTa TEXT" +
                        ")"
        );

        // 2. TABLE CHUYEN NGANH
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS ChuyenNganh (" +
                        "maChuyenNganh TEXT PRIMARY KEY, " +
                        "tenChuyenNganh TEXT NOT NULL, " +
                        "maKhoa TEXT NOT NULL, " +
                        "moTa TEXT, " +
                        "FOREIGN KEY (maKhoa) REFERENCES Khoa(maKhoa) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT" +
                        ")"
        );

        // 3. TABLE LOP HOC
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS LopHoc (" +
                        "maLop TEXT PRIMARY KEY, " +
                        "tenLop TEXT NOT NULL, " +
                        "khoaHoc TEXT, " +
                        "namNhapHoc INTEGER, " +
                        "siSoToiDa INTEGER DEFAULT 50, " +
                        "ghiChu TEXT, " +
                        "maChuyenNganh TEXT NOT NULL, " +
                        "FOREIGN KEY (maChuyenNganh) " +
                        "REFERENCES ChuyenNganh(maChuyenNganh) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT, " +
                        "CHECK (siSoToiDa > 0)" +
                        ")"
        );

        // 4. TABLE SINH VIEN
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS SinhVien (" +
                        "maSinhVien TEXT PRIMARY KEY, " +
                        "hoTen TEXT NOT NULL, " +
                        "ngaySinh TEXT, " +
                        "gioiTinh TEXT, " +
                        "email TEXT, " +
                        "soDienThoai TEXT, " +
                        "diaChi TEXT, " +
                        "maLop TEXT, " +
                        "anhDaiDien TEXT, " +
                        "FOREIGN KEY (maLop) REFERENCES LopHoc(maLop) " +
                        "ON UPDATE CASCADE ON DELETE SET NULL" +
                        ")"
        );

        // 5. TABLE TAI KHOAN
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS TaiKhoan (" +
                        "maTaiKhoan TEXT PRIMARY KEY, " +
                        "tenDangNhap TEXT NOT NULL UNIQUE, " +
                        "matKhau TEXT NOT NULL, " +
                        "vaiTro TEXT NOT NULL " +
                        "CHECK (vaiTro IN ('ADMIN', 'SINHVIEN')), " +
                        "trangThai INTEGER NOT NULL DEFAULT 1 " +
                        "CHECK (trangThai IN (0, 1)), " +
                        "maSinhVien TEXT UNIQUE, " +
                        "FOREIGN KEY (maSinhVien) REFERENCES SinhVien(maSinhVien) " +
                        "ON UPDATE CASCADE ON DELETE SET NULL" +
                        ")"
        );

        // 6. TABLE MON HOC
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS MonHoc (" +
                        "maMonHoc TEXT PRIMARY KEY, " +
                        "tenMonHoc TEXT NOT NULL, " +
                        "soTinChi INTEGER NOT NULL, " +
                        "loaiMonHoc TEXT, " +
                        "soTietLyThuyet INTEGER DEFAULT 0, " +
                        "soTietThucHanh INTEGER DEFAULT 0, " +
                        "moTa TEXT, " +
                        "CHECK (soTinChi > 0), " +
                        "CHECK (soTietLyThuyet >= 0), " +
                        "CHECK (soTietThucHanh >= 0)" +
                        ")"
        );

        // 7. TABLE HOC PHAN
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS HocPhan (" +
                        "maHocPhan TEXT PRIMARY KEY, " +
                        "maMonHoc TEXT NOT NULL, " +
                        "hocKy INTEGER NOT NULL, " +
                        "namHoc TEXT NOT NULL, " +
                        "nhomHocPhan TEXT, " +
                        "trangThai TEXT DEFAULT 'Đang mở' " +
                        "CHECK (trangThai IN ('Đang mở', 'Đã đóng', 'Đã kết thúc')), " +
                        "FOREIGN KEY (maMonHoc) REFERENCES MonHoc(maMonHoc) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT, " +
                        "CHECK (hocKy > 0)" +
                        ")"
        );

        // 8. TABLE DIEM HOC PHAN
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS DiemHocPhan (" +
                        "maDiem INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "maSinhVien TEXT NOT NULL, " +
                        "maHocPhan TEXT NOT NULL, " +
                        "diemQuaTrinh REAL, " +
                        "diemThi REAL, " +
                        "diemTongKet REAL, " +

                        "FOREIGN KEY (maSinhVien) REFERENCES SinhVien(maSinhVien) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE, " +

                        "FOREIGN KEY (maHocPhan) REFERENCES HocPhan(maHocPhan) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE, " +

                        "UNIQUE (maSinhVien, maHocPhan), " +

                        "CHECK (diemQuaTrinh IS NULL OR " +
                        "(diemQuaTrinh >= 0 AND diemQuaTrinh <= 10)), " +

                        "CHECK (diemThi IS NULL OR " +
                        "(diemThi >= 0 AND diemThi <= 10)), " +

                        "CHECK (diemTongKet IS NULL OR " +
                        "(diemTongKet >= 0 AND diemTongKet <= 10))" +
                        ")"
        );

        // 9. TABLE DANG KY HOC PHAN
        createDangKyHocPhanTable(db);

        // 10. CREATE INDEX
        createIndexes(db);
    }

    // ================= CREATE REGISTRATION TABLE =================

    private void createDangKyHocPhanTable(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS DangKyHocPhan (" +
                        "maDangKy INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "maSinhVien TEXT NOT NULL, " +
                        "maHocPhan TEXT NOT NULL, " +
                        "ngayDangKy TEXT DEFAULT CURRENT_TIMESTAMP, " +
                        "trangThai TEXT DEFAULT 'Đã đăng ký', " +

                        "FOREIGN KEY (maSinhVien) " +
                        "REFERENCES SinhVien(maSinhVien) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE, " +

                        "FOREIGN KEY (maHocPhan) " +
                        "REFERENCES HocPhan(maHocPhan) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE, " +

                        "UNIQUE (maSinhVien, maHocPhan), " +

                        "CHECK (trangThai IN ('Đã đăng ký', 'Đã hủy'))" +
                        ")"
        );
    }

    // ================= CREATE INDEX =================

    private void createIndexes(SQLiteDatabase db) {

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_chuyennganh_khoa " +
                        "ON ChuyenNganh(maKhoa)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_lophoc_chuyennganh " +
                        "ON LopHoc(maChuyenNganh)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_sinhvien_lop " +
                        "ON SinhVien(maLop)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_hocphan_monhoc " +
                        "ON HocPhan(maMonHoc)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_hocphan_hocky " +
                        "ON HocPhan(hocKy, namHoc, trangThai)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_diem_sinhvien " +
                        "ON DiemHocPhan(maSinhVien)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_diem_hocphan " +
                        "ON DiemHocPhan(maHocPhan)"
        );

        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_dangky_hocphan " +
                        "ON DangKyHocPhan(maHocPhan)"
        );
    }

    // ================= DATABASE UPGRADE =================

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Upgrade từ version 1 lên version 2
        if (oldVersion < 2) {
            createDangKyHocPhanTable(db);
        }

        // Upgrade từ version 2 lên version 3
        if (oldVersion < 3) {
            createIndexes(db);
        }
    }
}
