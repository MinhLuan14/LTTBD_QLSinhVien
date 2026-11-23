package vhu.edu.edusync.model;

public class MonHoc {

    public static final String LOAI_BAT_BUOC = "Bắt buộc";
    public static final String LOAI_TU_CHON = "Tự chọn";

    private String maMonHoc;
    private String tenMonHoc;
    private int soTinChi;
    private String loaiMonHoc;
    private int soTietLyThuyet;
    private int soTietThucHanh;
    private String moTa;

    public MonHoc() {
    }

    public MonHoc(String maMonHoc, String tenMonHoc, int soTinChi,
                  String loaiMonHoc, int soTietLyThuyet,
                  int soTietThucHanh, String moTa) {
        this.maMonHoc = maMonHoc;
        this.tenMonHoc = tenMonHoc;
        this.soTinChi = soTinChi;
        this.loaiMonHoc = loaiMonHoc;
        this.soTietLyThuyet = soTietLyThuyet;
        this.soTietThucHanh = soTietThucHanh;
        this.moTa = moTa;
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getLoaiMonHoc() {
        return loaiMonHoc;
    }

    public void setLoaiMonHoc(String loaiMonHoc) {
        this.loaiMonHoc = loaiMonHoc;
    }

    public int getSoTietLyThuyet() {
        return soTietLyThuyet;
    }

    public void setSoTietLyThuyet(int soTietLyThuyet) {
        this.soTietLyThuyet = soTietLyThuyet;
    }

    public int getSoTietThucHanh() {
        return soTietThucHanh;
    }

    public void setSoTietThucHanh(int soTietThucHanh) {
        this.soTietThucHanh = soTietThucHanh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Dùng cho dropdown chọn môn học
    @Override
    public String toString() {
        return tenMonHoc + " (" + maMonHoc + ") - " + soTinChi + " TC";
    }
}
