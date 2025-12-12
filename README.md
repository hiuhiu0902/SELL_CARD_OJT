# 💳 Card Store Backend (Sell_Card_Demo1)

Hệ thống Backend cho website bán thẻ trực tuyến, được xây dựng bằng **Spring Boot (Java 17)**. Hỗ trợ quản lý đơn hàng, tích hợp thanh toán PayOS, lưu trữ AWS S3 và bảo mật với JWT.

## 🚀 Tính năng chính (Key Features)

* **Quản lý xác thực & Phân quyền:** Đăng nhập, đăng ký, phân quyền (Admin/User) sử dụng **Spring Security** và **JWT**.
* **Thanh toán trực tuyến:** Tích hợp cổng thanh toán **PayOS**.
* **Lưu trữ đám mây:** Upload và quản lý hình ảnh/file qua **AWS S3**.
* **Email Service:** Gửi mail xác nhận/thông báo sử dụng **Java Mail Sender** kết hợp template **Thymeleaf**.
* **API Documentation:** Tự động tạo tài liệu API với **Swagger UI (OpenAPI)**.
* **Cơ sở dữ liệu:** Hỗ trợ kết nối linh hoạt (MySQL / SQL Server).

## 🛠️ Tech Stack

| Thành phần | Công nghệ sử dụng |
| --- | --- |
| **Core** | Java 17, Spring Boot 3.5.6 |
| **Database** | MySQL / SQL Server, Spring Data JPA |
| **Security** | Spring Security, JWT (io.jsonwebtoken) |
| **Payment** | PayOS SDK (vn.payos) |
| **Storage** | AWS SDK S3 |
| **Utils** | Lombok, Jackson, Maven |

## ⚙️ Cài đặt & Chạy Local (Development)

### 1. Yêu cầu
* JDK 17 trở lên.
* Maven.
* MySQL hoặc SQL Server.

### 2. Cấu hình biến môi trường
Tạo file `application.properties` (hoặc `.yml`) và cấu hình các thông số sau:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ten_database_cua_ban
spring.datasource.username=root
spring.datasource.password=your_password

# AWS S3 Configuration
cloud.aws.credentials.access-key=YOUR_ACCESS_KEY
cloud.aws.credentials.secret-key=YOUR_SECRET_KEY
cloud.aws.region.static=ap-southeast-1

# PayOS Configuration
payos.client-id=YOUR_CLIENT_ID
payos.api-key=YOUR_API_KEY
payos.checksum-key=YOUR_CHECKSUM_KEY

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```
### 3. Quy trình chạy ứng dụng trên Server (Chi tiết)

Sau khi đã upload file `.jar` lên thư mục trên server thông qua WinSCP. Tại giao diện WinSCP, nhấn **`Ctrl + P`** để mở cửa sổ dòng lệnh (Terminal/Putty) và thực hiện tuần tự các bước sau:

#### Bước 1: Kiểm tra Port đang chạy
Trước khi chạy phiên bản mới, cần kiểm tra xem port của ứng dụng (thường là 8080) có đang bị chiếm dụng bởi phiên bản cũ không.

Chạy lệnh:
```bash
sudo netstat -tulpn | grep LISTEN
```
Kết quả sẽ hiện ra danh sách các port. Hãy tìm dòng có port :::8080 (hoặc port bạn cấu hình) và nhìn sang cột cuối cùng để lấy số PID/Program name (Ví dụ: 12345/java).

### Bước 2: Tắt ứng dụng cũ (Kill process)
Dựa vào số PID tìm được ở bước 1, chạy lệnh sau để tắt ứng dụng cũ:
```bash
# Cú pháp: sudo kill -9 <PID>
# Ví dụ nếu PID là 12345:
sudo kill -9 12345
```
Lưu ý: Nếu bước 1 không thấy port nào đang chạy thì bỏ qua bước này.

### Bước 3: Chạy ứng dụng mới
Sau khi đã tắt process cũ, chạy lệnh dưới đây để khởi động ứng dụng mới.

Lựa chọn 1: Chạy trực tiếp (Debug) Dùng lệnh này nếu muốn xem lỗi trực tiếp trên màn hình (ứng dụng sẽ tắt khi đóng cửa sổ Putty).
```bash
java -jar Sell_Card_Demo1-0.0.1-SNAPSHOT.jar
```
Lựa chọn 2: Chạy ngầm (Production - Khuyên dùng) Dùng lệnh này để ứng dụng chạy nền mãi mãi kể cả khi tắt máy tính/đóng WinSCP.
```bash
nohup java -jar Sell_Card_Demo1-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```
### Bước 4: Kiểm tra log (Quan trọng)
Sau khi chạy lệnh ở Bước 3, cần xem log để chắc chắn ứng dụng đã khởi động thành công ("Started Application...").
Chạy lệnh:
```bash
tail -f app.log
```
(Nhấn Ctrl + C để thoát khỏi màn hình xem log).

---

### Giải thích thêm các lệnh trong Script này (Dành cho bạn hiểu rõ):

1.  **`grep LISTEN`**: Lọc ra các port đang ở trạng thái "Lắng nghe" (đang mở).
2.  **`sudo kill -9`**: Lệnh này là "giết không thương tiếc" (Force kill), đảm bảo process cũ chết hẳn để nhả port ra.
3.  **`nohup ... &`**:
    * `nohup`: (No Hang Up) Giúp process không bị chết khi bạn logout.
    * `> app.log`: Ghi toàn bộ những gì in ra màn hình vào file tên là `app.log`.
    * `2>&1`: Ghi cả lỗi (error) vào chung file log đó luôn.
    * `&` (ở cuối): Chạy process dưới background để bạn vẫn gõ được lệnh khác.
4.  **`tail -f`**: Xem đuôi file log theo thời gian thực (giống như bạn đang nhìn console trong IntelliJ).

Bạn có muốn mình gộp tất cả các lệnh trên thành 1 file `deploy.sh` duy nhất không? Khi đó mỗi lần update bạn chỉ cần gõ đúng 1 dòng `./deploy.sh` là nó tự tìm port, tự kill và tự chạy lại luôn, không cần gõ tay từng bước.