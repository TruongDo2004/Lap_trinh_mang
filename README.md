<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
     ỨNG DỤNG GỬI FILE QUA UDP
</h2>
<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="https://github.com/user-attachments/assets/711a2cd8-7eb4-4dae-9d90-12c0a0a208a2" />
        <img alt="AIoTLab Logo" width="180" src="https://github.com/user-attachments/assets/dc2ef2b8-9a70-4cfa-9b4b-f6c2f25f1660" />
        <img alt="DaiNam University Logo" width="200" src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)
</div>

## 1. Giới thiệu hệ thống
<ul>
     <li><strong>Main</strong>: Điểm vào chính của chương trình, khởi chạy <code>LoginGUI</code> làm cửa sổ đầu tiên.</li>

  <li><strong>LoginGUI</strong>: Giao diện đăng nhập. 
      - Kiểm tra tài khoản trong MongoDB thông qua <code>MongoDBHelper.login()</code>.  
      - Nếu đăng nhập là <strong>admin</strong> → mở <code>AdminPanelGUI</code>.  
      - Nếu là <strong>user</strong> → mở <code>FileTransferGUI</code>.
  </li>

  <li><strong>RegisterGUI</strong>: Giao diện đăng ký tài khoản mới, ghi thông tin vào MongoDB (tài khoản có role "user" mặc định).</li>

  <li><strong>FileTransferGUI</strong>: Giao diện chính của người dùng:
    <ul>
      <li>Chọn và gửi file đến người nhận qua UDP bằng <code>UDPClient</code>.</li>
      <li>Hiển thị tiến trình truyền file (progress bar, trạng thái).</li>
      <li>Lưu file nhận được vào thư mục <code>downloads/</code>.</li>
    </ul>
  </li>

  <li><strong>AdminPanelGUI</strong>: Giao diện quản trị dành cho admin:
    <ul>
      <li>Xem danh sách tất cả các giao dịch file từ MongoDB.</li>
      <li>Tìm kiếm giao dịch theo người gửi.</li>
      <li>Đăng xuất quay về màn hình đăng nhập.</li>
    </ul>
  </li>

  <li><strong>UDPServer</strong>: Chương trình server lắng nghe trên một cổng UDP (8894/8895).
    <ul>
      <li>Nhận file từ client qua chuỗi gói tin <code>UPLOAD;...;UPLOAD_CHUNK;...;UPLOAD_END</code>.</li>
      <li>Lưu file vào thư mục <code>server_storage/</code>.</li>
      <li>Lưu metadata giao dịch vào MongoDB (tên file, người gửi, người nhận, thời gian).</li>
      <li>Hỗ trợ client tải file về qua gói tin <code>DOWNLOAD</code>.</li>
    </ul>
  </li>

  <li><strong>UDPClient</strong>: Chương trình client cho từng user.
    <ul>
      <li>Mỗi user lắng nghe trên port riêng (tính từ hash username).</li>
      <li>Gửi file: chia nhỏ thành chunk và gửi qua UDP.</li>
      <li>Nhận file: lắp ghép chunk lại, ghi thành file.</li>
    </ul>
  </li>

  <li><strong>MongoDBHelper</strong>: Lớp tiện ích kết nối MongoDB:
    <ul>
      <li>Đăng nhập (kiểm tra username, password, role).</li>
      <li>Đăng ký tài khoản mới.</li>
      <li>Lưu và truy vấn danh sách giao dịch file.</li>
    </ul>
  </li>
</ul>

### Kiến trúc
- **Client**: gồm LoginGUI, RegisterGUI, FileTransferGUI (người dùng) và AdminPanelGUI (admin).  
- **Server**: UDPServer nhận/gửi file, MongoDB lưu metadata.  
- **Giao thức**: UDP, gói tin header + chunks + end marker. Dữ liệu file có thể gửi raw bytes hoặc base64 để đảm bảo an toàn khi đóng gói.

Đặc điểm nổi bật:
- Phân quyền rõ ràng: admin ↔ user.
- Giao diện Swing đơn giản, có màu sắc trực quan.
- Lưu lịch sử giao dịch file vào MongoDB.

## 2. Ngôn ngữ & Công nghệ
[![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Java Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![UDP Socket](https://img.shields.io/badge/UDP%20Socket-007396?style=for-the-badge&logo=socketdotio&logoColor=white)](https://datatracker.ietf.org/doc/html/rfc768)

## 3. Một số màn hình giao diện
<p align="center">
   <img width="588" height="342" alt="image" src="https://github.com/user-attachments/assets/44b99aab-b0dd-42a6-9eaf-a3f604757be4" />
</p>
<p align="center">
   <em>Hình 1: Giao diện đăng nhập</em>
</p>

<p align="center">
   <img width="561" height="392" alt="image" src="https://github.com/user-attachments/assets/10dd68f7-c028-4a86-acfe-b4078b65e7d5" />
</p>
<p align="center">
   <em>Hình 2: Giao diện đăng ký</em>
</p>

<p align="center">
   <img width="1236" height="802" alt="image" src="https://github.com/user-attachments/assets/04fd0e26-ae33-4ccd-a558-6762c8485256" />
</p>
<p align="center">
   <em>Hình 3: Giao diện người dùng</em>
</p>

<p align="center">
   <img width="1170" height="860" alt="image" src="https://github.com/user-attachments/assets/7ad7cb37-830d-468b-a652-49493f22dac8" />
</p>
<p align="center">
   <em>Hình 4: Giao diện admin</em>
</p>

## 4. Cài đặt & Sử dụng
**Yêu cầu môi trường:**
- Java Development Kit (JDK) 8 trở lên.
- MongoDB (chạy trên localhost).
- IDE: Eclipse/IntelliJ hoặc chạy trực tiếp qua terminal.
- Thư viện MongoDB (.jar): mongodb-driver-sync-5.5.0, bson-5.5.0, mongodb-driver-core-5.5.0.

**Cách triển khai:**
1. Import project vào IDE.
2. Chạy script khởi tạo DB `BTL_LTM` trong MongoDB.
3. Chạy `UDPServer.java` để mở server.
4. Chạy nhiều lần `Main.java` để mở nhiều client.
5. Đăng ký tài khoản mới, đăng nhập và thử gửi/nhận file.
6. Nếu đăng nhập bằng tài khoản admin, giao diện <code>AdminPanelGUI</code> sẽ được mở.

<p><strong>Lưu ý:</strong> UDP không đảm bảo toàn vẹn dữ liệu, phù hợp cho truyền file nhỏ. Nếu cần an toàn hơn có thể cải tiến bằng cơ chế ACK/retry.</p>

## 5. Thành viên & Thông tin
- **Sinh viên thực hiện**: Đỗ Trường Anh
- **Lớp**: CNTT 16-01
- **Email**: dotruonganh2004@gmail.com

© 2025 AIoTLab – Faculty of Information Technology, DaiNam University
