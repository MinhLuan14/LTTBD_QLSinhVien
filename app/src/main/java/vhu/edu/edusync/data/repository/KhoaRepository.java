
package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.model.Khoa;

public class KhoaRepository {

    private final DatabaseHelper dbHelper;

    public KhoaRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Lấy danh sách khoa
    public List<Khoa> getAll() {

        List<Khoa> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Khoa ORDER BY tenKhoa ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                Khoa khoa = new Khoa(
                        cursor.getString(cursor.getColumnIndexOrThrow("maKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
                );

                danhSach.add(khoa);
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // 2. Tìm khoa theo mã
    public Khoa getById(String maKhoa) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "Khoa",
                null,
                "maKhoa = ?",
                new String[]{maKhoa},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {

                return new Khoa(
                        cursor.getString(cursor.getColumnIndexOrThrow("maKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
                );
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    // 3. Thêm khoa
    public long themKhoa(Khoa khoa) {

        if (khoa == null) {
            throw new IllegalArgumentException("Thông tin khoa không được để trống");
        }
        if (khoa.getMaKhoa() == null || khoa.getTenKhoa() == null) {
            throw new IllegalArgumentException("Mã khoa và tên khoa không được để trống");
        }

        String maKhoa = khoa.getMaKhoa().trim();
        String tenKhoa = khoa.getTenKhoa().trim();
        if (maKhoa.isEmpty()) {
            throw new IllegalArgumentException("Mã khoa không được để trống");
        }

        if (tenKhoa.isEmpty()) {
            throw new IllegalArgumentException("Tên khoa không được để trống");
        }

        ContentValues values = new ContentValues();

        values.put("maKhoa", maKhoa);
        values.put("tenKhoa", tenKhoa);
        values.put("moTa", khoa.getMoTa());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.insertOrThrow("Khoa", null, values);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException(
                    "Mã khoa đã tồn tại hoặc dữ liệu không hợp lệ"
            );
        }
    }

    // 4. Sửa khoa
    public int suaKhoa(Khoa khoa) {

        if (khoa == null) {
            throw new IllegalArgumentException("Thông tin khoa không được để trống");
        }

        String maKhoa = khoa.getMaKhoa().trim();
        String tenKhoa = khoa.getTenKhoa().trim();

        if (maKhoa.isEmpty() || tenKhoa.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã khoa và tên khoa không được để trống"
            );
        }

        ContentValues values = new ContentValues();

        values.put("tenKhoa", tenKhoa);
        values.put("moTa", khoa.getMoTa());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rows = db.update(
                "Khoa",
                values,
                "maKhoa = ?",
                new String[]{maKhoa}
        );

        if (rows == 0) {
            throw new IllegalArgumentException("Không tìm thấy khoa cần sửa");
        }

        return rows;
    }

    // 5. Kiểm tra khoa có chuyên ngành hay chưa
    public boolean kiemTraChuyenNganh(String maKhoa) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ChuyenNganh WHERE maKhoa = ?",
                new String[]{maKhoa}
        );

        try {
            cursor.moveToFirst();
            return cursor.getInt(0) > 0;
        } finally {
            cursor.close();
        }
    }

    // 6. Xóa khoa
    public boolean xoaKhoa(String maKhoa) {

        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khoa không hợp lệ");
        }

        if (kiemTraChuyenNganh(maKhoa)) {
            throw new IllegalStateException(
                    "Không thể xóa khoa vì vẫn còn chuyên ngành trực thuộc"
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                "Khoa",
                "maKhoa = ?",
                new String[]{maKhoa}
        ) > 0;
    }

    // Đóng kết nối
    public void close() {
        dbHelper.close();
    }
}
