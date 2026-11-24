package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.data.model.SinhVien;
import vhu.edu.edusync.data.model.TaiKhoan;

/**
 * Repository xử lý các nghiệp vụ liên quan đến bảng TaiKhoan trong SQLite:
 * - Đăng nhập, xác thực và phân quyền (ADMIN / SINHVIEN)
 * - Quản lý tài khoản (Lấy danh sách, tìm kiếm, lọc)
 * - Khóa và mở khóa tài khoản
 * - Đổi mật khẩu và đặt lại mật khẩu
 * - Tạo tài khoản sinh viên và liên kết mã sinh viên
 * - Đảm bảo tài khoản ADMIN mặc định khởi tạo
 */
public class TaiKhoanRepository {

    private final DatabaseHelper dbHelper;

    public TaiKhoanRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        damBaoTaiKhoanAdminMacDinh();
        damBaoDuLieuKhoaLopSinhVienMau(dbHelper.getWritableDatabase());
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * Băm mật khẩu SHA-256 an toàn
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback plain text nếu lỗi thuật toán
        }
    }

    /**
     * Khởi tạo tài khoản ADMIN mặc định và dữ liệu mẫu nếu bảng chưa có dữ liệu
     * Tài khoản mặc định: admin / admin123
     */
    public void damBaoTaiKhoanAdminMacDinh() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN, null);
        try {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                // 1. Tạo tài khoản ADMIN mặc định
                ContentValues values = new ContentValues();
                values.put("maTaiKhoan", "TK_ADMIN_01");
                values.put("tenDangNhap", "admin");
                values.put("matKhau", "admin123");
                values.put("vaiTro", TaiKhoan.ROLE_ADMIN);
                values.put("trangThai", TaiKhoan.STATUS_ACTIVE);
                values.putNull("maSinhVien");
                db.insert(DatabaseHelper.TABLE_TAI_KHOAN, null, values);

                // 2. Khởi tạo danh mục mẫu và sinh viên nếu chưa có
                damBaoDuLieuKhoaLopSinhVienMau(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private void damBaoDuLieuKhoaLopSinhVienMau(SQLiteDatabase db) {
        try {
            // Khoa
            ContentValues vKhoa = new ContentValues();
            vKhoa.put("maKhoa", "K_CNTT");
            vKhoa.put("tenKhoa", "Công nghệ thông tin");
            vKhoa.put("moTa", "Khoa Công nghệ thông tin");
            db.insertWithOnConflict("Khoa", null, vKhoa, SQLiteDatabase.CONFLICT_IGNORE);

            // Chuyên ngành
            ContentValues vCN = new ContentValues();
            vCN.put("maChuyenNganh", "CN_KTPM");
            vCN.put("tenChuyenNganh", "Kỹ thuật phần mềm");
            vCN.put("maKhoa", "K_CNTT");
            vCN.put("moTa", "Chuyên ngành Kỹ thuật phần mềm");
            db.insertWithOnConflict("ChuyenNganh", null, vCN, SQLiteDatabase.CONFLICT_IGNORE);

            // Lớp học
            ContentValues vLop = new ContentValues();
            vLop.put("maLop", "22DTH01");
            vLop.put("tenLop", "Kỹ thuật phần mềm 1");
            vLop.put("khoaHoc", "Khóa 2022");
            vLop.put("namNhapHoc", 2022);
            vLop.put("siSoToiDa", 50);
            vLop.put("maChuyenNganh", "CN_KTPM");
            db.insertWithOnConflict("LopHoc", null, vLop, SQLiteDatabase.CONFLICT_IGNORE);

            // Sinh viên SV001 (chưa có tài khoản - dùng test cấp tài khoản)
            ContentValues sv1 = new ContentValues();
            sv1.put("maSinhVien", "SV001");
            sv1.put("hoTen", "Nguyễn Văn An");
            sv1.put("ngaySinh", "15/08/2003");
            sv1.put("gioiTinh", "Nam");
            sv1.put("email", "an.nguyen@vhu.edu.vn");
            sv1.put("soDienThoai", "0901234567");
            sv1.put("diaChi", "TP. Hồ Chí Minh");
            sv1.put("maLop", "22DTH01");
            db.insertWithOnConflict(DatabaseHelper.TABLE_SINH_VIEN, null, sv1, SQLiteDatabase.CONFLICT_IGNORE);

            // Sinh viên SV002 (đã có tài khoản - dùng test đăng nhập sinh viên, phân quyền)
            ContentValues sv2 = new ContentValues();
            sv2.put("maSinhVien", "SV002");
            sv2.put("hoTen", "Trần Thị Bình");
            sv2.put("ngaySinh", "20/11/2003");
            sv2.put("gioiTinh", "Nữ");
            sv2.put("email", "binh.tran@vhu.edu.vn");
            sv2.put("soDienThoai", "0912345678");
            sv2.put("diaChi", "Hà Nội");
            sv2.put("maLop", "22DTH01");
            db.insertWithOnConflict(DatabaseHelper.TABLE_SINH_VIEN, null, sv2, SQLiteDatabase.CONFLICT_IGNORE);

            // Tài khoản cho SV002
            ContentValues tkSv2 = new ContentValues();
            tkSv2.put("maTaiKhoan", "TK_SV002");
            tkSv2.put("tenDangNhap", "SV002");
            tkSv2.put("matKhau", "123456");
            tkSv2.put("vaiTro", TaiKhoan.ROLE_SINHVIEN);
            tkSv2.put("trangThai", TaiKhoan.STATUS_ACTIVE);
            tkSv2.put("maSinhVien", "SV002");
            db.insertWithOnConflict(DatabaseHelper.TABLE_TAI_KHOAN, null, tkSv2, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Xác thực đăng nhập
     * Hỗ trợ kiểm tra mật khẩu dạng thường hoặc băm SHA-256
     */
    public TaiKhoan kiemTraDangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null) return null;

        String trimmedUser = tenDangNhap.trim();
        String trimmedPass = matKhau.trim();
        String hashedPass = hashPassword(trimmedPass);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, tk.maSinhVien, " +
                "sv.hoTen, lh.tenLop, sv.email " +
                "FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " tk " +
                "LEFT JOIN " + DatabaseHelper.TABLE_SINH_VIEN + " sv ON tk.maSinhVien = sv.maSinhVien " +
                "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                "WHERE tk.tenDangNhap = ? LIMIT 1";

        Cursor cursor = db.rawQuery(sql, new String[]{trimmedUser});
        try {
            if (cursor.moveToFirst()) {
                String dbPass = cursor.getString(cursor.getColumnIndexOrThrow("matKhau"));
                // Khớp mật khẩu dạng thô hoặc dạng băm
                if (trimmedPass.equals(dbPass) || hashedPass.equalsIgnoreCase(dbPass)) {
                    return cursorToTaiKhoan(cursor);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 2. Lấy thông tin tài khoản theo tên đăng nhập
     */
    public TaiKhoan getByUsername(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, tk.maSinhVien, " +
                "sv.hoTen, lh.tenLop, sv.email " +
                "FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " tk " +
                "LEFT JOIN " + DatabaseHelper.TABLE_SINH_VIEN + " sv ON tk.maSinhVien = sv.maSinhVien " +
                "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                "WHERE tk.tenDangNhap = ? LIMIT 1";

        Cursor cursor = db.rawQuery(sql, new String[]{tenDangNhap.trim()});
        try {
            if (cursor.moveToFirst()) {
                return cursorToTaiKhoan(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 3. Lấy thông tin tài khoản theo mã sinh viên
     */
    public TaiKhoan getByMaSinhVien(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, tk.maSinhVien, " +
                "sv.hoTen, lh.tenLop, sv.email " +
                "FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " tk " +
                "LEFT JOIN " + DatabaseHelper.TABLE_SINH_VIEN + " sv ON tk.maSinhVien = sv.maSinhVien " +
                "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                "WHERE tk.maSinhVien = ? LIMIT 1";

        Cursor cursor = db.rawQuery(sql, new String[]{maSinhVien.trim()});
        try {
            if (cursor.moveToFirst()) {
                return cursorToTaiKhoan(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 4. Lấy toàn bộ danh sách tài khoản kèm thông tin sinh viên liên kết
     */
    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, tk.maSinhVien, " +
                "sv.hoTen, lh.tenLop, sv.email " +
                "FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " tk " +
                "LEFT JOIN " + DatabaseHelper.TABLE_SINH_VIEN + " sv ON tk.maSinhVien = sv.maSinhVien " +
                "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                "ORDER BY tk.vaiTro ASC, tk.tenDangNhap ASC";

        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToTaiKhoan(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 5. Tìm kiếm và lọc danh sách tài khoản
     * @param keyword Từ khóa tìm theo username hoặc mã sinh viên hoặc họ tên
     * @param filterRole null hoặc "ADMIN" hoặc "SINHVIEN"
     * @param filterStatus null hoặc 1 (Hoạt động) hoặc 0 (Bị khóa)
     */
    public List<TaiKhoan> timKiemVaLoc(String keyword, String filterRole, Integer filterStatus) {
        List<TaiKhoan> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder(
                "SELECT tk.maTaiKhoan, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai, tk.maSinhVien, " +
                        "sv.hoTen, lh.tenLop, sv.email " +
                        "FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " tk " +
                        "LEFT JOIN " + DatabaseHelper.TABLE_SINH_VIEN + " sv ON tk.maSinhVien = sv.maSinhVien " +
                        "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                        "WHERE 1=1 "
        );

        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String pattern = "%" + keyword.trim() + "%";
            sql.append("AND (tk.tenDangNhap LIKE ? OR tk.maSinhVien LIKE ? OR sv.hoTen LIKE ?) ");
            args.add(pattern);
            args.add(pattern);
            args.add(pattern);
        }

        if (filterRole != null && !filterRole.trim().isEmpty() && !filterRole.equalsIgnoreCase("ALL")) {
            sql.append("AND tk.vaiTro = ? ");
            args.add(filterRole.trim().toUpperCase());
        }

        if (filterStatus != null && filterStatus >= 0) {
            sql.append("AND tk.trangThai = ? ");
            args.add(String.valueOf(filterStatus));
        }

        sql.append("ORDER BY tk.vaiTro ASC, tk.tenDangNhap ASC");

        Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
        try {
            while (cursor.moveToNext()) {
                list.add(cursorToTaiKhoan(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 6. Khóa hoặc Mở khóa tài khoản
     * @param maTaiKhoan Mã tài khoản cần thay đổi
     * @param trangThaiMoi 1: Hoạt động, 0: Bị khóa
     */
    public boolean capNhatTrangThai(String maTaiKhoan, int trangThaiMoi) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) return false;
        if (trangThaiMoi != TaiKhoan.STATUS_ACTIVE && trangThaiMoi != TaiKhoan.STATUS_LOCKED) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);

        int rows = db.update(DatabaseHelper.TABLE_TAI_KHOAN, values, "maTaiKhoan = ?", new String[]{maTaiKhoan.trim()});
        return rows > 0;
    }

    /**
     * 7. Đổi mật khẩu
     */
    public boolean doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        if (tenDangNhap == null || matKhauCu == null || matKhauMoi == null) return false;

        TaiKhoan tk = kiemTraDangNhap(tenDangNhap, matKhauCu);
        if (tk == null) {
            return false; // Mật khẩu cũ không chính xác
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("matKhau", matKhauMoi.trim());

        int rows = db.update(DatabaseHelper.TABLE_TAI_KHOAN, values, "tenDangNhap = ?", new String[]{tenDangNhap.trim()});
        return rows > 0;
    }

    /**
     * 8. Đặt lại mật khẩu (Dành cho Quản trị viên reset mật khẩu người dùng)
     */
    public boolean datLaiMatKhau(String maTaiKhoan, String matKhauMoi) {
        if (maTaiKhoan == null || matKhauMoi == null) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("matKhau", matKhauMoi.trim());

        int rows = db.update(DatabaseHelper.TABLE_TAI_KHOAN, values, "maTaiKhoan = ?", new String[]{maTaiKhoan.trim()});
        return rows > 0;
    }

    /**
     * 9. Tạo tài khoản cho sinh viên đã có trong bảng SinhVien
     */
    public boolean taoTaiKhoanSinhVien(String maSinhVien, String tenDangNhap, String matKhau) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return false;

        String finalUser = (tenDangNhap != null && !tenDangNhap.trim().isEmpty())
                ? tenDangNhap.trim() : maSinhVien.trim();
        String finalPass = (matKhau != null && !matKhau.trim().isEmpty())
                ? matKhau.trim() : "123456";

        // Kiểm tra xem sinh viên đã có tài khoản chưa
        if (isMaSinhVienDaCoTaiKhoan(maSinhVien.trim())) {
            return false;
        }

        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (isUsernameExists(finalUser)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maTaiKhoan", "TK_" + maSinhVien.trim());
        values.put("tenDangNhap", finalUser);
        values.put("matKhau", finalPass);
        values.put("vaiTro", TaiKhoan.ROLE_SINHVIEN);
        values.put("trangThai", TaiKhoan.STATUS_ACTIVE);
        values.put("maSinhVien", maSinhVien.trim());

        long result = db.insert(DatabaseHelper.TABLE_TAI_KHOAN, null, values);
        return result != -1;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại hay chưa
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN +
                " WHERE tenDangNhap = ?", new String[]{username.trim()});
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
     * Kiểm tra mã sinh viên đã có tài khoản chưa
     */
    public boolean isMaSinhVienDaCoTaiKhoan(String maSinhVien) {
        if (maSinhVien == null || maSinhVien.trim().isEmpty()) return false;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN +
                " WHERE maSinhVien = ?", new String[]{maSinhVien.trim()});
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
     * Lấy danh sách các sinh viên chưa được cấp tài khoản
     */
    public List<SinhVien> getDanhSachSinhVienChuaCoTaiKhoan() {
        List<SinhVien> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT sv.maSinhVien, sv.hoTen, sv.ngaySinh, sv.gioiTinh, sv.email, " +
                "sv.soDienThoai, sv.diaChi, sv.maLop, sv.anhDaiDien, lh.tenLop " +
                "FROM " + DatabaseHelper.TABLE_SINH_VIEN + " sv " +
                "LEFT JOIN " + DatabaseHelper.TABLE_LOP_HOC + " lh ON sv.maLop = lh.maLop " +
                "LEFT JOIN " + DatabaseHelper.TABLE_TAI_KHOAN + " tk ON sv.maSinhVien = tk.maSinhVien " +
                "WHERE tk.maTaiKhoan IS NULL " +
                "ORDER BY sv.hoTen ASC";

        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                SinhVien sv = new SinhVien();
                sv.setMaSinhVien(cursor.getString(cursor.getColumnIndexOrThrow("maSinhVien")));
                sv.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow("hoTen")));
                sv.setNgaySinh(cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")));
                sv.setGioiTinh(cursor.getString(cursor.getColumnIndexOrThrow("gioiTinh")));
                sv.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                sv.setSoDienThoai(cursor.getString(cursor.getColumnIndexOrThrow("soDienThoai")));
                sv.setDiaChi(cursor.getString(cursor.getColumnIndexOrThrow("diaChi")));
                sv.setMaLop(cursor.getString(cursor.getColumnIndexOrThrow("maLop")));
                sv.setAnhDaiDien(cursor.getString(cursor.getColumnIndexOrThrow("anhDaiDien")));
                int colTenLop = cursor.getColumnIndex("tenLop");
                if (colTenLop != -1 && !cursor.isNull(colTenLop)) {
                    sv.setTenLop(cursor.getString(colTenLop));
                }
                list.add(sv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * Thống kê số lượng tài khoản
     */
    public int countTotalAccounts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN, null);
        try {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return 0;
    }

    public int countActiveAccounts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " WHERE trangThai = 1", null);
        try {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return 0;
    }

    public int countLockedAccounts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TAI_KHOAN + " WHERE trangThai = 0", null);
        try {
            if (cursor.moveToFirst()) return cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return 0;
    }

    private TaiKhoan cursorToTaiKhoan(Cursor cursor) {
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(cursor.getString(cursor.getColumnIndexOrThrow("maTaiKhoan")));
        tk.setTenDangNhap(cursor.getString(cursor.getColumnIndexOrThrow("tenDangNhap")));
        tk.setMatKhau(cursor.getString(cursor.getColumnIndexOrThrow("matKhau")));
        tk.setVaiTro(cursor.getString(cursor.getColumnIndexOrThrow("vaiTro")));
        tk.setTrangThai(cursor.getInt(cursor.getColumnIndexOrThrow("trangThai")));

        int colMaSV = cursor.getColumnIndex("maSinhVien");
        if (colMaSV != -1 && !cursor.isNull(colMaSV)) {
            tk.setMaSinhVien(cursor.getString(colMaSV));
        }

        int colHoTen = cursor.getColumnIndex("hoTen");
        if (colHoTen != -1 && !cursor.isNull(colHoTen)) {
            tk.setHoTenSinhVien(cursor.getString(colHoTen));
        }

        int colTenLop = cursor.getColumnIndex("tenLop");
        if (colTenLop != -1 && !cursor.isNull(colTenLop)) {
            tk.setTenLop(cursor.getString(colTenLop));
        }

        int colEmail = cursor.getColumnIndex("email");
        if (colEmail != -1 && !cursor.isNull(colEmail)) {
            tk.setEmail(cursor.getString(colEmail));
        }

        return tk;
    }
}
