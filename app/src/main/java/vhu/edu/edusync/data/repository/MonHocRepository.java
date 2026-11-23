package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.model.MonHoc;

public class MonHocRepository {

    public static final int TIN_CHI_TOI_THIEU = 1;
    public static final int TIN_CHI_TOI_DA = 10;

    private final DatabaseHelper dbHelper;

    public MonHocRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // ================= ĐỌC DỮ LIỆU =================

    // 1. Lấy toàn bộ môn học
    public List<MonHoc> getAll() {
        List<MonHoc> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM MonHoc ORDER BY tenMonHoc ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {
                danhSach.add(docMonHoc(cursor));
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // 2. Lấy môn học theo mã
    public MonHoc getById(String maMonHoc) {
        if (maMonHoc == null) {
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "MonHoc",
                null,
                "maMonHoc = ?",
                new String[]{maMonHoc},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return docMonHoc(cursor);
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    // 3. Tìm kiếm theo mã hoặc tên
    public List<MonHoc> timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return getAll();
        }

        List<MonHoc> danhSach = new ArrayList<>();
        String like = "%" + tuKhoa.trim() + "%";

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM MonHoc " +
                        "WHERE maMonHoc LIKE ? OR tenMonHoc LIKE ? " +
                        "ORDER BY tenMonHoc ASC",
                new String[]{like, like}
        );

        try {
            while (cursor.moveToNext()) {
                danhSach.add(docMonHoc(cursor));
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // ================= GHI DỮ LIỆU =================

    // 4. Thêm môn học
    public long themMonHoc(MonHoc mh) {
        kiemTraHopLe(mh);

        ContentValues values = taoContentValues(mh);
        values.put("maMonHoc", chuanHoaMa(mh.getMaMonHoc()));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.insertOrThrow("MonHoc", null, values);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException("Mã môn học đã tồn tại");
        }
    }

    // 5. Sửa môn học (không cho sửa mã)
    public int suaMonHoc(MonHoc mh) {
        kiemTraHopLe(mh);

        ContentValues values = taoContentValues(mh);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.update(
                    "MonHoc",
                    values,
                    "maMonHoc = ?",
                    new String[]{chuanHoaMa(mh.getMaMonHoc())}
            );
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException("Dữ liệu môn học không hợp lệ");
        }
    }

    // 6. Kiểm tra môn học đã có học phần hay chưa
    public boolean kiemTraHocPhan(String maMonHoc) {
        return demSo(
                "SELECT COUNT(*) FROM HocPhan WHERE maMonHoc = ?",
                maMonHoc
        ) > 0;
    }

    // 7. Kiểm tra môn học đã có điểm (ảnh hưởng GPA khi đổi số tín chỉ)
    public boolean kiemTraCoDiem(String maMonHoc) {
        return demSo(
                "SELECT COUNT(*) FROM DiemHocPhan d " +
                        "INNER JOIN HocPhan hp ON d.maHocPhan = hp.maHocPhan " +
                        "WHERE hp.maMonHoc = ?",
                maMonHoc
        ) > 0;
    }

    // 8. Xóa môn học
    public boolean xoaMonHoc(String maMonHoc) {
        if (maMonHoc == null || maMonHoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã môn học không hợp lệ");
        }

        if (kiemTraHocPhan(maMonHoc)) {
            throw new IllegalStateException(
                    "Không thể xóa môn học vì đã có học phần được mở"
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                "MonHoc",
                "maMonHoc = ?",
                new String[]{maMonHoc}
        ) > 0;
    }

    // ================= HỖ TRỢ =================

    private void kiemTraHopLe(MonHoc mh) {
        if (mh == null) {
            throw new IllegalArgumentException("Thông tin môn học không được để trống");
        }

        if (mh.getMaMonHoc() == null || mh.getMaMonHoc().trim().isEmpty()
                || mh.getTenMonHoc() == null || mh.getTenMonHoc().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã và tên môn học không được để trống");
        }

        if (mh.getSoTinChi() < TIN_CHI_TOI_THIEU || mh.getSoTinChi() > TIN_CHI_TOI_DA) {
            throw new IllegalArgumentException(
                    "Số tín chỉ phải từ " + TIN_CHI_TOI_THIEU + " đến " + TIN_CHI_TOI_DA
            );
        }

        if (mh.getSoTietLyThuyet() < 0 || mh.getSoTietThucHanh() < 0) {
            throw new IllegalArgumentException("Số tiết không được âm");
        }
    }

    private ContentValues taoContentValues(MonHoc mh) {
        ContentValues values = new ContentValues();
        values.put("tenMonHoc", mh.getTenMonHoc().trim());
        values.put("soTinChi", mh.getSoTinChi());
        values.put("loaiMonHoc", mh.getLoaiMonHoc());
        values.put("soTietLyThuyet", mh.getSoTietLyThuyet());
        values.put("soTietThucHanh", mh.getSoTietThucHanh());
        values.put("moTa", mh.getMoTa());
        return values;
    }

    private String chuanHoaMa(String ma) {
        return ma.trim().toUpperCase(Locale.ROOT);
    }

    private int demSo(String sql, String thamSo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, new String[]{thamSo});

        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    private MonHoc docMonHoc(Cursor cursor) {
        return new MonHoc(
                cursor.getString(cursor.getColumnIndexOrThrow("maMonHoc")),
                cursor.getString(cursor.getColumnIndexOrThrow("tenMonHoc")),
                cursor.getInt(cursor.getColumnIndexOrThrow("soTinChi")),
                cursor.getString(cursor.getColumnIndexOrThrow("loaiMonHoc")),
                cursor.getInt(cursor.getColumnIndexOrThrow("soTietLyThuyet")),
                cursor.getInt(cursor.getColumnIndexOrThrow("soTietThucHanh")),
                cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
        );
    }

    // Đóng kết nối
    public void close() {
        dbHelper.close();
    }
}
