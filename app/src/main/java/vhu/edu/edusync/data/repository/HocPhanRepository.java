package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.model.HocPhan;
import vhu.edu.edusync.model.TrangThaiHocPhan;

/**
 * Quản lý học phần (Hưng).
 *
 * API bàn giao:
 *  - Chương (Đăng ký học phần): getDanhSachHocPhanDangMo(), getDanhSachHocPhanDangMo(hocKy, namHoc),
 *    isDangMo(maHocPhan), getById(maHocPhan)
 *  - Luân (Quản lý điểm): getAll(), getByHocKy(hocKy, namHoc), getDanhSachNamHoc()
 */
public class HocPhanRepository {

    public static final int HOC_KY_TOI_THIEU = 1;
    public static final int HOC_KY_TOI_DA = 3;

    private static final Pattern NAM_HOC_PATTERN = Pattern.compile("^(\\d{4})-(\\d{4})$");

    private static final String SELECT_JOIN =
            "SELECT hp.maHocPhan, hp.maMonHoc, hp.hocKy, hp.namHoc, " +
                    "hp.nhomHocPhan, hp.trangThai, mh.tenMonHoc, mh.soTinChi " +
                    "FROM HocPhan hp " +
                    "INNER JOIN MonHoc mh ON hp.maMonHoc = mh.maMonHoc ";

    private static final String ORDER_BY =
            " ORDER BY hp.namHoc DESC, hp.hocKy DESC, mh.tenMonHoc ASC, hp.nhomHocPhan ASC";

    private final DatabaseHelper dbHelper;

    public HocPhanRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ================= ĐỌC DỮ LIỆU =================

    // 1. Lấy toàn bộ học phần
    public List<HocPhan> getAll() {
        return truyVanDanhSach(SELECT_JOIN + ORDER_BY, null);
    }

    // 2. Lấy học phần theo học kỳ và năm học
    public List<HocPhan> getByHocKy(int hocKy, String namHoc) {
        return truyVanDanhSach(
                SELECT_JOIN + "WHERE hp.hocKy = ? AND hp.namHoc = ?" + ORDER_BY,
                new String[]{String.valueOf(hocKy), namHoc}
        );
    }

    // 3. Tìm kiếm theo mã học phần, mã/tên môn, năm học, nhóm, trạng thái
    public List<HocPhan> timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getAll();
        }

        String like = "%" + tuKhoa.trim() + "%";

        return truyVanDanhSach(
                SELECT_JOIN +
                        "WHERE hp.maHocPhan LIKE ? OR hp.maMonHoc LIKE ? " +
                        "OR mh.tenMonHoc LIKE ? OR hp.namHoc LIKE ? " +
                        "OR hp.nhomHocPhan LIKE ? OR hp.trangThai LIKE ?" +
                        ORDER_BY,
                new String[]{like, like, like, like, like, like}
        );
    }

    // 4. Lấy học phần theo mã
    public HocPhan getById(String maHocPhan) {
        if (maHocPhan == null) {
            return null;
        }

        List<HocPhan> ketQua = truyVanDanhSach(
                SELECT_JOIN + "WHERE hp.maHocPhan = ?",
                new String[]{maHocPhan}
        );

        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    // 5. [Cho Chương] Danh sách học phần đang mở
    public List<HocPhan> getDanhSachHocPhanDangMo() {
        return truyVanDanhSach(
                SELECT_JOIN + "WHERE hp.trangThai = ?" + ORDER_BY,
                new String[]{TrangThaiHocPhan.DANG_MO}
        );
    }

    // 6. [Cho Chương] Danh sách học phần đang mở theo học kỳ, năm học
    public List<HocPhan> getDanhSachHocPhanDangMo(int hocKy, String namHoc) {
        return truyVanDanhSach(
                SELECT_JOIN +
                        "WHERE hp.trangThai = ? AND hp.hocKy = ? AND hp.namHoc = ?" +
                        ORDER_BY,
                new String[]{TrangThaiHocPhan.DANG_MO, String.valueOf(hocKy), namHoc}
        );
    }

    // 7. [Cho Chương] Kiểm tra học phần còn mở trước khi đăng ký
    public boolean isDangMo(String maHocPhan) {
        return demSo(
                "SELECT COUNT(*) FROM HocPhan WHERE maHocPhan = ? AND trangThai = ?",
                new String[]{maHocPhan, TrangThaiHocPhan.DANG_MO}
        ) > 0;
    }

    // 8. Danh sách năm học đang có (phục vụ bộ lọc)
    public List<String> getDanhSachNamHoc() {
        List<String> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT namHoc FROM HocPhan ORDER BY namHoc DESC",
                null
        );

        try {
            while (cursor.moveToNext()) {
                danhSach.add(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // 9. Số sinh viên đang đăng ký học phần
    public int demSinhVienDangKy(String maHocPhan) {
        return demSo(
                "SELECT COUNT(*) FROM DangKyHocPhan WHERE maHocPhan = ? AND trangThai = ?",
                new String[]{maHocPhan, "Đã đăng ký"}
        );
    }

    // 10. Học phần đã có dữ liệu đăng ký hoặc điểm
    public boolean coDuLieuLienQuan(String maHocPhan) {
        return demSo(
                "SELECT (SELECT COUNT(*) FROM DangKyHocPhan WHERE maHocPhan = ?) + " +
                        "(SELECT COUNT(*) FROM DiemHocPhan WHERE maHocPhan = ?)",
                new String[]{maHocPhan, maHocPhan}
        ) > 0;
    }

    // 11. Kiểm tra trùng nhóm (cùng môn, học kỳ, năm học, nhóm)
    public boolean kiemTraTrungNhom(String maMonHoc, int hocKy, String namHoc,
                                    String nhomHocPhan, String maHocPhanBoQua) {
        String boQua = maHocPhanBoQua == null ? "" : maHocPhanBoQua;

        return demSo(
                "SELECT COUNT(*) FROM HocPhan " +
                        "WHERE maMonHoc = ? AND hocKy = ? AND namHoc = ? " +
                        "AND LOWER(TRIM(nhomHocPhan)) = LOWER(?) " +
                        "AND maHocPhan <> ?",
                new String[]{
                        maMonHoc,
                        String.valueOf(hocKy),
                        namHoc,
                        nhomHocPhan.trim(),
                        boQua
                }
        ) > 0;
    }

    // ================= GHI DỮ LIỆU =================

    // 12. Thêm học phần
    public long themHocPhan(HocPhan hp) {
        kiemTraHopLe(hp);

        String ma = chuanHoaMa(hp.getMaHocPhan());

        if (kiemTraTrungNhom(hp.getMaMonHoc(), hp.getHocKy(),
                hp.getNamHoc().trim(), hp.getNhomHocPhan(), null)) {
            throw new IllegalArgumentException(
                    "Môn học này đã có nhóm \"" + hp.getNhomHocPhan().trim()
                            + "\" trong học kỳ " + hp.getHocKy() + " - " + hp.getNamHoc().trim()
            );
        }

        ContentValues values = taoContentValues(hp);
        values.put("maHocPhan", ma);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.insertOrThrow("HocPhan", null, values);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException(
                    "Mã học phần đã tồn tại hoặc môn học không hợp lệ"
            );
        }
    }

    // 13. Sửa học phần (không cho sửa mã)
    public int suaHocPhan(HocPhan hp) {
        kiemTraHopLe(hp);

        String ma = chuanHoaMa(hp.getMaHocPhan());
        HocPhan cu = getById(ma);

        if (cu == null) {
            return 0;
        }

        if (!TrangThaiHocPhan.coTheChuyen(cu.getTrangThai(), hp.getTrangThai())) {
            throw new IllegalStateException(
                    "Học phần đã kết thúc, không thể chuyển sang \"" + hp.getTrangThai() + "\""
            );
        }

        boolean doiThongTinChinh = !cu.getMaMonHoc().equals(hp.getMaMonHoc())
                || cu.getHocKy() != hp.getHocKy()
                || !cu.getNamHoc().equals(hp.getNamHoc().trim());

        if (doiThongTinChinh && coDuLieuLienQuan(ma)) {
            throw new IllegalStateException(
                    "Học phần đã có sinh viên đăng ký hoặc có điểm, " +
                            "chỉ được sửa nhóm và trạng thái"
            );
        }

        if (kiemTraTrungNhom(hp.getMaMonHoc(), hp.getHocKy(),
                hp.getNamHoc().trim(), hp.getNhomHocPhan(), ma)) {
            throw new IllegalArgumentException(
                    "Môn học này đã có nhóm \"" + hp.getNhomHocPhan().trim()
                            + "\" trong học kỳ " + hp.getHocKy() + " - " + hp.getNamHoc().trim()
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.update(
                    "HocPhan",
                    taoContentValues(hp),
                    "maHocPhan = ?",
                    new String[]{ma}
            );
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException("Dữ liệu học phần không hợp lệ");
        }
    }

    // 14. Cập nhật trạng thái (mở / đóng / kết thúc)
    public int capNhatTrangThai(String maHocPhan, String trangThaiMoi) {
        HocPhan cu = getById(maHocPhan);

        if (cu == null) {
            return 0;
        }

        if (!TrangThaiHocPhan.isHopLe(trangThaiMoi)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }

        if (!TrangThaiHocPhan.coTheChuyen(cu.getTrangThai(), trangThaiMoi)) {
            throw new IllegalStateException(
                    "Học phần đã kết thúc, không thể chuyển sang \"" + trangThaiMoi + "\""
            );
        }

        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.update(
                "HocPhan",
                values,
                "maHocPhan = ?",
                new String[]{maHocPhan}
        );
    }

    // 15. Xóa học phần
    // Lưu ý: DangKyHocPhan và DiemHocPhan có ON DELETE CASCADE,
    // nên phải chặn xóa khi đã có dữ liệu liên quan.
    public boolean xoaHocPhan(String maHocPhan) {
        if (maHocPhan == null || maHocPhan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã học phần không hợp lệ");
        }

        if (coDuLieuLienQuan(maHocPhan)) {
            throw new IllegalStateException(
                    "Không thể xóa vì học phần đã có sinh viên đăng ký hoặc có điểm. " +
                            "Hãy chuyển sang trạng thái \"" + TrangThaiHocPhan.DA_KET_THUC + "\""
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                "HocPhan",
                "maHocPhan = ?",
                new String[]{maHocPhan}
        ) > 0;
    }

    // ================= KIỂM TRA DỮ LIỆU =================

    public static boolean isNamHocHopLe(String namHoc) {
        if (namHoc == null) {
            return false;
        }

        Matcher m = NAM_HOC_PATTERN.matcher(namHoc.trim());

        if (!m.matches()) {
            return false;
        }

        int namDau = Integer.parseInt(m.group(1));
        int namSau = Integer.parseInt(m.group(2));

        return namSau == namDau + 1;
    }

    private void kiemTraHopLe(HocPhan hp) {
        if (hp == null) {
            throw new IllegalArgumentException("Thông tin học phần không được để trống");
        }

        if (hp.getMaHocPhan() == null || hp.getMaHocPhan().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã học phần không được để trống");
        }

        if (hp.getMaMonHoc() == null || hp.getMaMonHoc().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn môn học");
        }

        if (hp.getHocKy() < HOC_KY_TOI_THIEU || hp.getHocKy() > HOC_KY_TOI_DA) {
            throw new IllegalArgumentException(
                    "Học kỳ phải từ " + HOC_KY_TOI_THIEU + " đến " + HOC_KY_TOI_DA
            );
        }

        if (!isNamHocHopLe(hp.getNamHoc())) {
            throw new IllegalArgumentException(
                    "Năm học phải có dạng YYYY-YYYY và liên tiếp (VD: 2026-2027)"
            );
        }

        if (hp.getNhomHocPhan() == null || hp.getNhomHocPhan().trim().isEmpty()) {
            throw new IllegalArgumentException("Nhóm học phần không được để trống");
        }

        if (!TrangThaiHocPhan.isHopLe(hp.getTrangThai())) {
            throw new IllegalArgumentException("Trạng thái học phần không hợp lệ");
        }
    }

    // ================= HỖ TRỢ =================

    private ContentValues taoContentValues(HocPhan hp) {
        ContentValues values = new ContentValues();
        values.put("maMonHoc", hp.getMaMonHoc().trim());
        values.put("hocKy", hp.getHocKy());
        values.put("namHoc", hp.getNamHoc().trim());
        values.put("nhomHocPhan", hp.getNhomHocPhan().trim());
        values.put("trangThai", hp.getTrangThai());
        return values;
    }

    private String chuanHoaMa(String ma) {
        return ma.trim().toUpperCase(Locale.ROOT);
    }

    private int demSo(String sql, String[] thamSo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, thamSo);

        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private List<HocPhan> truyVanDanhSach(String sql, String[] thamSo) {
        List<HocPhan> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, thamSo);

        try {
            while (cursor.moveToNext()) {
                HocPhan hp = new HocPhan(
                        cursor.getString(cursor.getColumnIndexOrThrow("maHocPhan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maMonHoc")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("hocKy")),
                        cursor.getString(cursor.getColumnIndexOrThrow("namHoc")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nhomHocPhan")),
                        cursor.getString(cursor.getColumnIndexOrThrow("trangThai"))
                );

                hp.setTenMonHoc(cursor.getString(cursor.getColumnIndexOrThrow("tenMonHoc")));
                hp.setSoTinChi(cursor.getInt(cursor.getColumnIndexOrThrow("soTinChi")));

                danhSach.add(hp);
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // Đóng kết nối
    public void close() {
        dbHelper.close();
    }
}
