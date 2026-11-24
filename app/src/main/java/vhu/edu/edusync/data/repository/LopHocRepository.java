package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.data.model.LopHoc;

/**
 * Repository quản lý truy vấn bảng LopHoc trong SQLite
 */
public class LopHocRepository {

    private final DatabaseHelper dbHelper;

    public LopHocRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Lấy toàn bộ danh sách lớp học
     */
    public List<LopHoc> getAll() {
        List<LopHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM LopHoc ORDER BY tenLop ASC", null);
        try {
            while (cursor.moveToNext()) {
                LopHoc lop = new LopHoc(
                        cursor.getString(cursor.getColumnIndexOrThrow("maLop")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenLop")),
                        cursor.getString(cursor.getColumnIndexOrThrow("khoaHoc")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("namNhapHoc")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("siSoToiDa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ghiChu")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maChuyenNganh"))
                );
                list.add(lop);
            }
        } finally {
            cursor.close();
        }

        return list;
    }

    /**
     * Lấy thông tin lớp học theo mã lớp
     */
    public LopHoc getById(String maLop) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "LopHoc",
                null,
                "maLop = ?",
                new String[]{maLop},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return new LopHoc(
                        cursor.getString(cursor.getColumnIndexOrThrow("maLop")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenLop")),
                        cursor.getString(cursor.getColumnIndexOrThrow("khoaHoc")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("namNhapHoc")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("siSoToiDa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ghiChu")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maChuyenNganh"))
                );
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * Lấy tên lớp theo mã lớp
     */
    public String getTenLopByMa(String maLop) {
        if (maLop == null || maLop.isEmpty()) return "";
        LopHoc lop = getById(maLop);
        return lop != null ? lop.getTenLop() : maLop;
    }

    /**
     * Kiểm tra và nạp vài lớp mẫu nếu cơ sở dữ liệu chưa có lớp nào
     * Giúp có sẵn dữ liệu lớp để chọn khi thêm sinh viên
     */
    public void ensureSampleClassesExist() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM LopHoc", null);
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        if (count == 0) {
            // Đảm bảo có chuyên ngành mẫu trước (khóa ngoại của LopHoc)
            Cursor cnCursor = db.rawQuery("SELECT maChuyenNganh FROM ChuyenNganh LIMIT 1", null);
            String maChuyenNganh = "CNTT";
            try {
                if (cnCursor.moveToFirst()) {
                    maChuyenNganh = cnCursor.getString(0);
                } else {
                    // Nếu chưa có Khoa & Chuyên ngành nào, thêm khoa và chuyên ngành mẫu
                    ContentValues kValues = new ContentValues();
                    kValues.put("maKhoa", "CNTT");
                    kValues.put("tenKhoa", "Công nghệ Thông tin");
                    kValues.put("moTa", "Khoa CNTT");
                    db.insertWithOnConflict("Khoa", null, kValues, SQLiteDatabase.CONFLICT_IGNORE);

                    ContentValues cnValues = new ContentValues();
                    cnValues.put("maChuyenNganh", "CNTT");
                    cnValues.put("tenChuyenNganh", "Công nghệ Phần mềm");
                    cnValues.put("maKhoa", "CNTT");
                    cnValues.put("moTa", "Chuyên ngành CNPM");
                    db.insertWithOnConflict("ChuyenNganh", null, cnValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
            } finally {
                cnCursor.close();
            }

            // Thêm các lớp mẫu
            ContentValues l1 = new ContentValues();
            l1.put("maLop", "22DTH01");
            l1.put("tenLop", "22DTH01 - Kỹ thuật phần mềm 1");
            l1.put("khoaHoc", "K22");
            l1.put("namNhapHoc", 2022);
            l1.put("siSoToiDa", 45);
            l1.put("ghiChu", "Lớp chính quy");
            l1.put("maChuyenNganh", maChuyenNganh);
            db.insertWithOnConflict("LopHoc", null, l1, SQLiteDatabase.CONFLICT_IGNORE);

            ContentValues l2 = new ContentValues();
            l2.put("maLop", "22DTH02");
            l2.put("tenLop", "22DTH02 - Kỹ thuật phần mềm 2");
            l2.put("khoaHoc", "K22");
            l2.put("namNhapHoc", 2022);
            l2.put("siSoToiDa", 45);
            l2.put("ghiChu", "Lớp chính quy");
            l2.put("maChuyenNganh", maChuyenNganh);
            db.insertWithOnConflict("LopHoc", null, l2, SQLiteDatabase.CONFLICT_IGNORE);

            ContentValues l3 = new ContentValues();
            l3.put("maLop", "23DTH01");
            l3.put("tenLop", "23DTH01 - Hệ thống thông tin");
            l3.put("khoaHoc", "K23");
            l3.put("namNhapHoc", 2023);
            l3.put("siSoToiDa", 50);
            l3.put("ghiChu", "Lớp chính quy");
            l3.put("maChuyenNganh", maChuyenNganh);
            db.insertWithOnConflict("LopHoc", null, l3, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
