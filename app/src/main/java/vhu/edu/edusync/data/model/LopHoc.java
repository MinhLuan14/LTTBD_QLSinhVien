package vhu.edu.edusync.data.model;

import java.io.Serializable;

/**
 * Model đại diện cho thực thể LopHoc
 * Khớp với bảng LopHoc trong DatabaseHelper
 */
public class LopHoc implements Serializable {

    private String maLop;
    private String tenLop;
    private String khoaHoc;
    private int namNhapHoc;
    private int siSoToiDa;
    private String ghiChu;
    private String maChuyenNganh;

    public LopHoc() {
    }

    public LopHoc(String maLop, String tenLop, String khoaHoc, int namNhapHoc,
                  int siSoToiDa, String ghiChu, String maChuyenNganh) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoaHoc = khoaHoc;
        this.namNhapHoc = namNhapHoc;
        this.siSoToiDa = siSoToiDa;
        this.ghiChu = ghiChu;
        this.maChuyenNganh = maChuyenNganh;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(String khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public int getNamNhapHoc() {
        return namNhapHoc;
    }

    public void setNamNhapHoc(int namNhapHoc) {
        this.namNhapHoc = namNhapHoc;
    }

    public int getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(int siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getMaChuyenNganh() {
        return maChuyenNganh;
    }

    public void setMaChuyenNganh(String maChuyenNganh) {
        this.maChuyenNganh = maChuyenNganh;
    }

    /**
     * Dùng để hiển thị đẹp trên Spinner chọn lớp
     */
    @Override
    public String toString() {
        return tenLop != null ? tenLop : maLop;
    }
}
