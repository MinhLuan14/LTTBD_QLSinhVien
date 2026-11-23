package vhu.edu.edusync.model;

/**
 * Hằng số trạng thái học phần dùng chung cho cả nhóm.
 * Giá trị phải khớp chính xác với CHECK trong bảng HocPhan.
 *
 * Luồng chuyển trạng thái:
 *   Đang mở  -> Đã đóng, Đã kết thúc
 *   Đã đóng  -> Đang mở, Đã kết thúc
 *   Đã kết thúc là trạng thái cuối, không chuyển tiếp được.
 */
public final class TrangThaiHocPhan {

    public static final String DANG_MO = "Đang mở";
    public static final String DA_DONG = "Đã đóng";
    public static final String DA_KET_THUC = "Đã kết thúc";

    public static final String[] TAT_CA = {DANG_MO, DA_DONG, DA_KET_THUC};

    private TrangThaiHocPhan() {
    }

    public static boolean isHopLe(String trangThai) {
        for (String tt : TAT_CA) {
            if (tt.equals(trangThai)) {
                return true;
            }
        }
        return false;
    }

    public static boolean coTheChuyen(String trangThaiCu, String trangThaiMoi) {
        if (!isHopLe(trangThaiMoi)) {
            return false;
        }

        if (trangThaiMoi.equals(trangThaiCu)) {
            return true;
        }

        return !DA_KET_THUC.equals(trangThaiCu);
    }
}
