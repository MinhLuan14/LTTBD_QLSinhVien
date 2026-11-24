package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.data.model.SinhVien;

/**
 * Repository xử lý toàn bộ truy vấn bảng SinhVien và liên kết với TaiKhoan trong SQLite
 * Tuân thủ đúng quy ước phối hợp giữa Hoàng và Nguyên (Mục 1.2)
 */
public class SinhVienRepository {

    private final DatabaseHelper dbHelper;

    public SinhVienRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 1. Lấy toàn bộ danh sách sinh viên kèm tên lớp (dùng LEFT JOIN)
     */
    public List<SinhVien> getAll() {
        List<SinhVien> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT sv.maSinhVien, sv.hoTen, sv.ngaySinh, sv.gioiTinh, sv.email, " +
                "sv.soDienThoai, sv.diaChi, sv.maLop, sv.anhDaiDien, lh.tenLop " +
                "FROM SinhVien sv " +
                "LEFT JOIN LopHoc lh ON sv.maLop = lh.maLop " +
                "ORDER BY sv.hoTen ASC";

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToSinhVien(cursor));
            }
        } finally {
            cursor.close();
        }

        return list;
    }

    /**
     * 2. Tìm kiếm sinh viên theo từ khóa (Mã sinh viên hoặc Họ tên)
     */
    public List<SinhVien> search(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String pattern = "%" + keyword.trim() + "%";

        String query = "SELECT sv.maSinhVien, sv.hoTen, sv.ngaySinh, sv.gioiTinh, sv.email, " +
                "sv.soDienThoai, sv.diaChi, sv.maLop, sv.anhDaiDien, lh.tenLop " +
                "FROM SinhVien sv " +
                "LEFT JOIN LopHoc lh ON sv.maLop = lh.maLop " +
                "WHERE sv.maSinhVien LIKE ? OR sv.hoTen LIKE ? " +
                "ORDER BY sv.hoTen ASC";

        Cursor cursor = db.rawQuery(query, new String[]{pattern, pattern});
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToSinhVien(cursor));
            }
        } finally {
            cursor.close();
        }

        return list;
    }

    /**
     * 3. Lấy thông tin sinh viên theo mã sinh viên
     */
    public SinhVien getById(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT sv.maSinhVien, sv.hoTen, sv.ngaySinh, sv.gioiTinh, sv.email, " +
                "sv.soDienThoai, sv.diaChi, sv.maLop, sv.anhDaiDien, lh.tenLop " +
                "FROM SinhVien sv " +
                "LEFT JOIN LopHoc lh ON sv.maLop = lh.maLop " +
                "WHERE sv.maSinhVien = ? LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{maSinhVien.trim()});
        try {
            if (cursor.moveToFirst()) {
                return cursorToSinhVien(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 4. Kiểm tra mã sinh viên đã tồn tại hay chưa
     */
    public boolean isMaSinhVienExists(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return false;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM SinhVien WHERE maSinhVien = ?",
                new String[]{maSinhVien.trim()});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }

    /**
     * 5. Thêm mới sinh viên và tùy chọn tạo tài khoản liên kết (sử dụng SQLite Transaction)
     * Thỏa mãn yêu cầu Mục 1.2: Phối hợp Hoàng ↔ Nguyên qua SQLite transaction
     */
    public boolean themSinhVien(SinhVien sv, boolean taoTaiKhoan, String username, String password) {
        if (sv == null || sv.getMaSinhVien() == null || sv.getMaSinhVien().trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Bước 5.1: Thêm vào bảng SinhVien
            ContentValues svValues = new ContentValues();
            svValues.put("maSinhVien", sv.getMaSinhVien().trim());
            svValues.put("hoTen", sv.getHoTen().trim());
            svValues.put("ngaySinh", sv.getNgaySinh());
            svValues.put("gioiTinh", sv.getGioiTinh());
            svValues.put("email", sv.getEmail());
            svValues.put("soDienThoai", sv.getSoDienThoai());
            svValues.put("diaChi", sv.getDiaChi());
            svValues.put("maLop", sv.getMaLop());
            svValues.put("anhDaiDien", sv.getAnhDaiDien());

            long svResult = db.insertOrThrow("SinhVien", null, svValues);
            if (svResult == -1) {
                return false;
            }

            // Bước 5.2: Nếu người dùng chọn tạo tài khoản đăng nhập
            if (taoTaiKhoan) {
                String finalUser = (username != null && !username.trim().isEmpty())
                        ? username.trim() : sv.getMaSinhVien().trim();
                String finalPass = (password != null && !password.trim().isEmpty())
                        ? password.trim() : "123456";

                ContentValues tkValues = new ContentValues();
                tkValues.put("maTaiKhoan", "TK_" + sv.getMaSinhVien().trim());
                tkValues.put("tenDangNhap", finalUser);
                tkValues.put("matKhau", finalPass);
                tkValues.put("vaiTro", "SINHVIEN");
                tkValues.put("trangThai", 1);
                tkValues.put("maSinhVien", sv.getMaSinhVien().trim());

                long tkResult = db.insertOrThrow("TaiKhoan", null, tkValues);
                if (tkResult == -1) {
                    return false;
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 6. Cập nhật thông tin sinh viên
     */
    public boolean capNhatSinhVien(SinhVien sv) {
        if (sv == null || sv.getMaSinhVien() == null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoTen", sv.getHoTen());
        values.put("ngaySinh", sv.getNgaySinh());
        values.put("gioiTinh", sv.getGioiTinh());
        values.put("email", sv.getEmail());
        values.put("soDienThoai", sv.getSoDienThoai());
        values.put("diaChi", sv.getDiaChi());
        values.put("maLop", sv.getMaLop());
        values.put("anhDaiDien", sv.getAnhDaiDien());

        int rows = db.update("SinhVien", values, "maSinhVien = ?",
                new String[]{sv.getMaSinhVien()});
        return rows > 0;
    }

    /**
     * 7. Kiểm tra sinh viên có đang có dữ liệu điểm hoặc đăng ký học phần hay không
     * Tránh xóa dây chuyền gây mất dữ liệu của môn học/học phần/điểm
     */
    public boolean hasDiemOrDangKy(String maSinhVien) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Kiểm tra bảng DiemHocPhan
        Cursor cDiem = db.rawQuery("SELECT COUNT(*) FROM DiemHocPhan WHERE maSinhVien = ?",
                new String[]{maSinhVien});
        try {
            if (cDiem.moveToFirst() && cDiem.getInt(0) > 0) {
                return true;
            }
        } finally {
            cDiem.close();
        }

        // Kiểm tra bảng DangKyHocPhan
        Cursor cDangKy = db.rawQuery("SELECT COUNT(*) FROM DangKyHocPhan WHERE maSinhVien = ?",
                new String[]{maSinhVien});
        try {
            if (cDangKy.moveToFirst() && cDangKy.getInt(0) > 0) {
                return true;
            }
        } finally {
            cDangKy.close();
        }

        return false;
    }

    /**
     * 8. Xóa sinh viên an toàn
     * Trả về:
     *  1: Xóa thành công
     *  0: Không tìm thấy sinh viên để xóa
     * -1: Sinh viên đang có điểm hoặc đăng ký học phần, không được xóa
     */
    public int xoaSinhVien(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return 0;

        // Kiểm tra ràng buộc tham chiếu trước khi xóa
        if (hasDiemOrDangKy(maSinhVien)) {
            return -1;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa tài khoản liên kết (nếu có)
            db.delete("TaiKhoan", "maSinhVien = ?", new String[]{maSinhVien});

            // Xóa sinh viên
            int rows = db.delete("SinhVien", "maSinhVien = ?", new String[]{maSinhVien});

            db.setTransactionSuccessful();
            return rows > 0 ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Hàm phụ chuyển đổi từ Cursor sang đối tượng SinhVien
     */
    private SinhVien cursorToSinhVien(Cursor cursor) {
        SinhVien sv = new SinhVien(
                cursor.getString(cursor.getColumnIndexOrThrow("maSinhVien")),
                cursor.getString(cursor.getColumnIndexOrThrow("hoTen")),
                cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")),
                cursor.getString(cursor.getColumnIndexOrThrow("gioiTinh")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("soDienThoai")),
                cursor.getString(cursor.getColumnIndexOrThrow("diaChi")),
                cursor.getString(cursor.getColumnIndexOrThrow("maLop")),
                cursor.getString(cursor.getColumnIndexOrThrow("anhDaiDien"))
        );

        int colTenLop = cursor.getColumnIndex("tenLop");
        if (colTenLop != -1) {
            sv.setTenLop(cursor.getString(colTenLop));
        }

        return sv;
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
