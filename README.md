# 🎓 EduSync – Ứng dụng Quản lý Sinh viên và Điểm học phần

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)
![Database](https://img.shields.io/badge/Database-SQLite-blue)
![IDE](https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?logo=androidstudio)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)

## 📌 1. Giới thiệu dự án

**EduSync** là ứng dụng Android hỗ trợ quản lý thông tin sinh viên, lớp học, môn học và điểm học phần. Ứng dụng được xây dựng nhằm hỗ trợ việc lưu trữ, tra cứu và quản lý kết quả học tập một cách thuận tiện, trực quan và hiệu quả.

Dự án được thực hiện trong khuôn khổ môn học **Lập trình thiết bị di động (LTTBD)**.

### 🎯 Mục tiêu

- Xây dựng ứng dụng Android bằng Java.
- Áp dụng SQLite để lưu trữ và quản lý dữ liệu.
- Thực hiện các chức năng thêm, sửa, xóa và tìm kiếm dữ liệu.
- Hỗ trợ nhập điểm và tra cứu điểm học phần.
- Tính toán điểm trung bình và xếp loại học lực.
- Rèn luyện kỹ năng làm việc nhóm và quản lý mã nguồn bằng Git/GitHub.

## 🛠️ 2. Công nghệ sử dụng

| Công nghệ | Mục đích |
|---|---|
| Java | Ngôn ngữ lập trình chính |
| Android Studio | Môi trường phát triển |
| SQLite | Hệ quản trị cơ sở dữ liệu cục bộ |
| XML | Thiết kế giao diện |
| RecyclerView | Hiển thị danh sách dữ liệu |
| Material Design | Thiết kế giao diện người dùng |
| Git & GitHub | Quản lý mã nguồn và làm việc nhóm |

## 📱 3. Các chức năng chính

### 3.1. Quản lý sinh viên
- [ ] Hiển thị danh sách sinh viên.
- [ ] Thêm sinh viên mới.
- [ ] Cập nhật thông tin sinh viên.
- [ ] Xóa sinh viên.
- [ ] Tìm kiếm sinh viên.
- [ ] Xem thông tin sinh viên.

### 3.2. Quản lý lớp học
- [ ] Hiển thị danh sách lớp học.
- [ ] Thêm lớp học.
- [ ] Cập nhật thông tin lớp học.
- [ ] Xóa lớp học.
- [ ] Tìm kiếm lớp học.

### 3.3. Quản lý môn học
- [ ] Hiển thị danh sách môn học.
- [ ] Thêm môn học.
- [ ] Cập nhật thông tin môn học.
- [ ] Xóa môn học.
- [ ] Tìm kiếm môn học.

### 3.4. Quản lý học phần và điểm
- [ ] Quản lý dữ liệu học phần trong cơ sở dữ liệu.
- [ ] Chọn sinh viên cần nhập điểm.
- [ ] Chọn học phần cần nhập điểm.
- [ ] Thêm điểm học phần.
- [ ] Cập nhật điểm học phần.
- [ ] Xóa điểm học phần.
- [ ] Tra cứu điểm theo sinh viên.
- [ ] Tra cứu điểm theo học phần.

### 3.5. Thống kê kết quả học tập
- [ ] Tính điểm trung bình.
- [ ] Xếp loại kết quả học tập.
- [ ] Thống kê số lượng sinh viên theo xếp loại.
- [ ] Hiển thị kết quả thống kê trực quan.

### 3.6. Tài khoản và thông tin ứng dụng
- [ ] Xây dựng màn hình đăng nhập.
- [ ] Xây dựng màn hình thông tin cá nhân.
- [ ] Xây dựng màn hình giới thiệu ứng dụng.
- [ ] Xây dựng màn hình cài đặt.

## 🗄️ 4. Cơ sở dữ liệu

Ứng dụng sử dụng SQLite để lưu trữ dữ liệu cục bộ trên thiết bị Android.

### Các bảng dữ liệu dự kiến

| STT | Tên bảng | Chức năng |
|---|---|---|
| 1 | LopHoc | Lưu thông tin lớp học |
| 2 | SinhVien | Lưu thông tin sinh viên |
| 3 | MonHoc | Lưu thông tin môn học |
| 4 | HocPhan | Lưu thông tin học phần |
| 5 | DiemHocPhan | Lưu điểm học phần của sinh viên |

### Quan hệ dữ liệu

- Một lớp học có nhiều sinh viên.
- Một môn học có thể có nhiều học phần.
- Một sinh viên có thể có nhiều kết quả điểm học phần.
- Một học phần có thể có điểm của nhiều sinh viên.
- Bảng DiemHocPhan liên kết SinhVien và HocPhan.

## 🎨 5. Quy chuẩn giao diện

Ứng dụng sử dụng phong cách Material Design, ưu tiên giao diện đơn giản, hiện đại và dễ sử dụng.

| Thành phần | Quy chuẩn |
|---|---|
| Tên ứng dụng | EduSync |
| Màu chủ đạo | `#1769E8` |
| Màu nền | `#F4F7FC` |
| Màu thẻ | `#FFFFFF` |
| Màu thành công | `#16A779` |
| Màu chữ chính | `#172554` |
| Font chữ | Roboto |
| Bo góc | 14–20dp |
| Kích thước giao diện | Sử dụng dp và sp |

### Thiết bị kiểm thử thống nhất

- [ ] Thiết bị ảo: Google Pixel 7.
- [ ] Hệ điều hành: Android 15.
- [ ] API Level: 35.
- [ ] Độ phân giải: 1080 × 2400 px.
- [ ] Mật độ điểm ảnh: 420 dpi.
- [ ] System Image: Google Play Intel x86_64.

## 📂 6. Cấu trúc thư mục dự án

```text
LTTBD_QLSinhVien/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── .../
│           │       ├── activities/
│           │       ├── fragments/
│           │       ├── adapters/
│           │       ├── models/
│           │       ├── database/
│           │       ├── repositories/
│           │       └── utils/
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   ├── menu/
│           │   ├── mipmap/
│           │   ├── navigation/
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── README.md
└── ...
```

*Lưu ý: Cấu trúc trên là cấu trúc định hướng. Các thư mục có thể được điều chỉnh theo cách tổ chức thực tế của dự án.*

## ⚙️ 7. Hướng dẫn cài đặt và chạy dự án

### Yêu cầu môi trường

- Android Studio.
- JDK tương thích với phiên bản Android Gradle Plugin của dự án.
- Android SDK API 35.
- Git.

### Các bước thực hiện

1. Clone repository:

```bash
git clone https://github.com/<USERNAME>/LTTBD_QLSinhVien.git
```

2. Mở Android Studio.

3. Chọn **Open** và trỏ đến thư mục `LTTBD_QLSinhVien`.

4. Chờ Gradle Sync hoàn tất.

5. Khởi tạo hoặc chọn máy ảo Pixel 7 – Android 15 – API 35.

6. Nhấn **Run** để chạy ứng dụng.

## 👥 8. Thành viên và phân công công việc

| Thành viên | Nhiệm vụ | Trạng thái |
|---|---|---|
| Thành viên 1 | Phân tích hệ thống, ERD, Use Case | ⏳ Đang thực hiện |
| Thành viên 2 | Quản lý sinh viên và lớp học | ⏳ Chưa thực hiện |
| Thành viên 3 | Quản lý môn học và học phần | ⏳ Chưa thực hiện |
| Thành viên 4 | Quản lý điểm và thống kê | ⏳ Chưa thực hiện |
| Thành viên 5 | Giao diện tổng thể, tích hợp và kiểm thử | ⏳ Chưa thực hiện |

*Phân công có thể điều chỉnh theo kế hoạch thực tế của nhóm.*

## 📋 9. Checklist tiến độ dự án

### Giai đoạn 1 – Phân tích và thiết kế

- [ ] Xác định yêu cầu chức năng.
- [ ] Xác định yêu cầu phi chức năng.
- [ ] Xây dựng sơ đồ Use Case tổng quát.
- [ ] Xây dựng sơ đồ ERD.
- [ ] Xác định các bảng và quan hệ dữ liệu.
- [ ] Thiết kế giao diện tổng thể.
- [ ] Thống nhất quy chuẩn đặt tên file và biến.

### Giai đoạn 2 – Xây dựng giao diện

- [ ] Splash Screen.
- [ ] Màn hình đăng nhập.
- [ ] Màn hình chính.
- [ ] Màn hình quản lý sinh viên.
- [ ] Màn hình thêm/sửa sinh viên.
- [ ] Màn hình quản lý lớp học.
- [ ] Màn hình thêm/sửa lớp học.
- [ ] Màn hình quản lý môn học.
- [ ] Màn hình thêm/sửa môn học.
- [ ] Màn hình nhập điểm.
- [ ] Màn hình tra cứu điểm.
- [ ] Màn hình thống kê.
- [ ] Màn hình cá nhân.
- [ ] Màn hình thông tin ứng dụng.
- [ ] Màn hình cài đặt.

### Giai đoạn 3 – Xây dựng cơ sở dữ liệu và xử lý

- [ ] Tạo DatabaseHelper.
- [ ] Tạo bảng LopHoc.
- [ ] Tạo bảng SinhVien.
- [ ] Tạo bảng MonHoc.
- [ ] Tạo bảng HocPhan.
- [ ] Tạo bảng DiemHocPhan.
- [ ] Xây dựng các Model.
- [ ] Xây dựng các Repository.
- [ ] Xây dựng chức năng CRUD.
- [ ] Xây dựng chức năng tìm kiếm.
- [ ] Xây dựng chức năng nhập và cập nhật điểm.
- [ ] Xây dựng chức năng tra cứu điểm.
- [ ] Xây dựng chức năng tính điểm trung bình.
- [ ] Xây dựng chức năng xếp loại học lực.

### Giai đoạn 4 – Tích hợp và kiểm thử

- [ ] Tích hợp các chức năng vào ứng dụng.
- [ ] Kiểm tra điều hướng giữa các màn hình.
- [ ] Kiểm tra thêm dữ liệu.
- [ ] Kiểm tra sửa dữ liệu.
- [ ] Kiểm tra xóa dữ liệu.
- [ ] Kiểm tra tìm kiếm.
- [ ] Kiểm tra tính toán điểm.
- [ ] Kiểm tra dữ liệu SQLite.
- [ ] Kiểm tra giao diện trên Pixel 7 Emulator.
- [ ] Khắc phục lỗi phát sinh.

### Giai đoạn 5 – Hoàn thiện và báo cáo

- [ ] Hoàn thiện giao diện.
- [ ] Kiểm tra toàn bộ chức năng.
- [ ] Chụp ảnh màn hình ứng dụng.
- [ ] Hoàn thiện tài liệu báo cáo.
- [ ] Chuẩn bị slide thuyết trình.
- [ ] Chuẩn bị kịch bản demo.
- [ ] Kiểm tra repository trước khi nộp.
- [ ] Hoàn thiện README.md.

## 🔀 10. Quy tắc làm việc với Git và GitHub

### Quy tắc đặt tên nhánh

| Nhánh | Mục đích |
|---|---|
| main | Phiên bản ổn định |
| develop | Nhánh phát triển chung |
| feature/student | Chức năng sinh viên |
| feature/class | Chức năng lớp học |
| feature/subject | Chức năng môn học |
| feature/grade | Chức năng điểm |
| feature/statistics | Chức năng thống kê |
| fix/bug-name | Sửa lỗi |

### Quy tắc commit

Sử dụng các tiền tố thống nhất:

- `feat:` Thêm chức năng mới.
- `fix:` Sửa lỗi.
- `ui:` Cập nhật giao diện.
- `refactor:` Tái cấu trúc mã nguồn.
- `docs:` Cập nhật tài liệu.
- `test:` Bổ sung hoặc chỉnh sửa kiểm thử.

Ví dụ:

```bash
git add .
git commit -m "feat: add student management"
git push origin feature/student
```

### Quy trình làm việc nhóm

- [ ] Pull code mới nhất trước khi bắt đầu.
- [ ] Làm việc trên nhánh chức năng riêng.
- [ ] Không chỉnh sửa trực tiếp nhánh main.
- [ ] Commit thường xuyên với nội dung rõ ràng.
- [ ] Push code lên GitHub.
- [ ] Tạo Pull Request vào develop.
- [ ] Kiểm tra và xử lý conflict trước khi merge.
- [ ] Chỉ merge khi chức năng đã được kiểm tra.

## 📊 11. Tiến độ dự án

| Hạng mục | Trạng thái |
|---|---|
| Phân tích yêu cầu | ⏳ Đang thực hiện |
| Thiết kế ERD và Use Case | ⏳ Đang thực hiện |
| Thiết kế giao diện | ⏳ Chưa thực hiện |
| Xây dựng cơ sở dữ liệu | ⏳ Chưa thực hiện |
| Lập trình chức năng | ⏳ Chưa thực hiện |
| Tích hợp hệ thống | ⏳ Chưa thực hiện |
| Kiểm thử | ⏳ Chưa thực hiện |
| Hoàn thiện báo cáo | ⏳ Chưa thực hiện |

## 📌 12. Quy định và lưu ý

- Không commit các file cấu hình cá nhân hoặc thông tin nhạy cảm.
- Không tự ý thay đổi cấu trúc cơ sở dữ liệu khi chưa thống nhất với nhóm.
- Không tạo thêm Model, Repository hoặc chức năng trùng lặp.
- Mỗi thành viên cần kiểm tra chức năng trước khi gửi Pull Request.
- Giao diện cần tuân thủ màu sắc và quy chuẩn thiết kế chung.
- Mọi thay đổi quan trọng cần được cập nhật trong nhóm.

## 📄 13. Giấy phép

Đây là dự án học tập thuộc môn **Lập trình thiết bị di động (LTTBD)**, được xây dựng với mục đích nghiên cứu và thực hành.

---

**EduSync – Simplify Student Management, Connect Learning.**