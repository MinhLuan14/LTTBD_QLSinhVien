package vhu.edu.edusync.model;

public class ChuyenNganh {

    private String maChuyenNganh;
    private String tenChuyenNganh;
    private String maKhoa;
    private String moTa;

    public ChuyenNganh() {
    }

    public ChuyenNganh(String maChuyenNganh,
                       String tenChuyenNganh,
                       String maKhoa,
                       String moTa) {
        this.maChuyenNganh = maChuyenNganh;
        this.tenChuyenNganh = tenChuyenNganh;
        this.maKhoa = maKhoa;
        this.moTa = moTa;
    }

    public String getMaChuyenNganh() {
        return maChuyenNganh;
    }

    public void setMaChuyenNganh(String maChuyenNganh) {
        this.maChuyenNganh = maChuyenNganh;
    }

    public String getTenChuyenNganh() {
        return tenChuyenNganh;
    }

    public void setTenChuyenNganh(String tenChuyenNganh) {
        this.tenChuyenNganh = tenChuyenNganh;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}