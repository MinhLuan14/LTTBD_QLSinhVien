package vhu.edu.edusync.model;

public class Khoa {

    private String maKhoa;
    private String tenKhoa;
    private String moTa;

    public Khoa() {
    }

    public Khoa(String maKhoa, String tenKhoa, String moTa) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
        this.moTa = moTa;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}