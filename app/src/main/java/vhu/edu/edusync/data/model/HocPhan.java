package vhu.edu.edusync.model;

public class HocPhan {

    // Các cột của bảng HocPhan
    private String maHocPhan;
    private String maMonHoc;
    private int hocKy;
    private String namHoc;
    private String nhomHocPhan;
    private String trangThai;

    // Thông tin hiển thị lấy từ JOIN với bảng MonHoc
    private String tenMonHoc;
    private int soTinChi;

    public HocPhan() {
    }

    public HocPhan(String maHocPhan, String maMonHoc, int hocKy,
                   String namHoc, String nhomHocPhan, String trangThai) {
        this.maHocPhan = maHocPhan;
        this.maMonHoc = maMonHoc;
        this.hocKy = hocKy;
        this.namHoc = namHoc;
        this.nhomHocPhan = nhomHocPhan;
        this.trangThai = trangThai;
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public int getHocKy() {
        return hocKy;
    }

    public void setHocKy(int hocKy) {
        this.hocKy = hocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public String getNhomHocPhan() {
        return nhomHocPhan;
    }

    public void setNhomHocPhan(String nhomHocPhan) {
        this.nhomHocPhan = nhomHocPhan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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

    public boolean isDangMo() {
        return TrangThaiHocPhan.DANG_MO.equals(trangThai);
    }
}
