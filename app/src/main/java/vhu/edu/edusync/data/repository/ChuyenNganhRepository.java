
package vhu.edu.edusync.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

import vhu.edu.edusync.data.database.DatabaseHelper;
import vhu.edu.edusync.model.ChuyenNganh;

public class ChuyenNganhRepository {

    private final DatabaseHelper dbHelper;

    public ChuyenNganhRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Lấy toàn bộ danh sách chuyên ngành
    public List<ChuyenNganh> getAll() {

        List<ChuyenNganh> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM ChuyenNganh ORDER BY tenChuyenNganh ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                ChuyenNganh cn = new ChuyenNganh(
                        cursor.getString(cursor.getColumnIndexOrThrow("maChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
                );

                danhSach.add(cn);
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // 2. Lấy chuyên ngành theo mã
    public ChuyenNganh getById(String maChuyenNganh) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "ChuyenNganh",
                null,
                "maChuyenNganh = ?",
                new String[]{maChuyenNganh},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {

                return new ChuyenNganh(
                        cursor.getString(cursor.getColumnIndexOrThrow("maChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
                );
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    // 3. Lấy danh sách chuyên ngành thuộc một khoa
    public List<ChuyenNganh> getByMaKhoa(String maKhoa) {

        List<ChuyenNganh> danhSach = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "ChuyenNganh",
                null,
                "maKhoa = ?",
                new String[]{maKhoa},
                null,
                null,
                "tenChuyenNganh ASC"
        );

        try {
            while (cursor.moveToNext()) {

                ChuyenNganh cn = new ChuyenNganh(
                        cursor.getString(cursor.getColumnIndexOrThrow("maChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tenChuyenNganh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("maKhoa")),
                        cursor.getString(cursor.getColumnIndexOrThrow("moTa"))
                );

                danhSach.add(cn);
            }
        } finally {
            cursor.close();
        }

        return danhSach;
    }

    // 4. Thêm chuyên ngành
    public long themChuyenNganh(ChuyenNganh cn) {

        if (cn == null) {
            throw new IllegalArgumentException("Thông tin chuyên ngành không được để trống");
        }

        String ma = cn.getMaChuyenNganh().trim();
        String ten = cn.getTenChuyenNganh().trim();
        String maKhoa = cn.getMaKhoa().trim();

        if (ma.isEmpty() || ten.isEmpty() || maKhoa.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã, tên chuyên ngành và khoa không được để trống"
            );
        }

        ContentValues values = new ContentValues();

        values.put("maChuyenNganh", ma);
        values.put("tenChuyenNganh", ten);
        values.put("maKhoa", maKhoa);
        values.put("moTa", cn.getMoTa());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            return db.insertOrThrow("ChuyenNganh", null, values);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException(
                    "Mã chuyên ngành đã tồn tại hoặc mã khoa không hợp lệ"
            );
        }
    }

    // 5. Sửa chuyên ngành
    public int suaChuyenNganh(ChuyenNganh cn) {

        if (cn == null) {
            throw new IllegalArgumentException("Thông tin chuyên ngành không được để trống");
        }

        String ma = cn.getMaChuyenNganh().trim();
        String ten = cn.getTenChuyenNganh().trim();
        String maKhoa = cn.getMaKhoa().trim();

        if (ma.isEmpty() || ten.isEmpty() || maKhoa.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã, tên chuyên ngành và khoa không được để trống"
            );
        }

        ContentValues values = new ContentValues();

        values.put("tenChuyenNganh", ten);
        values.put("maKhoa", maKhoa);
        values.put("moTa", cn.getMoTa());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.update(
                "ChuyenNganh",
                values,
                "maChuyenNganh = ?",
                new String[]{ma}
        );
    }

    // 6. Kiểm tra chuyên ngành có lớp học hay chưa
    public boolean kiemTraLopHoc(String maChuyenNganh) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM LopHoc WHERE maChuyenNganh = ?",
                new String[]{maChuyenNganh}
        );

        try {
            cursor.moveToFirst();
            return cursor.getInt(0) > 0;
        } finally {
            cursor.close();
        }
    }

    // 7. Xóa chuyên ngành
    public boolean xoaChuyenNganh(String maChuyenNganh) {

        if (maChuyenNganh == null || maChuyenNganh.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã chuyên ngành không hợp lệ"
            );
        }

        if (kiemTraLopHoc(maChuyenNganh)) {
            throw new IllegalStateException(
                    "Không thể xóa chuyên ngành vì vẫn còn lớp học trực thuộc"
            );
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(
                "ChuyenNganh",
                "maChuyenNganh = ?",
                new String[]{maChuyenNganh}
        ) > 0;
    }

    // Đóng kết nối
    public void close() {
        dbHelper.close();
    }
}
