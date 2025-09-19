<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
     ỨNG DỤNG GỬI FILE QUA UDP (PHÂN QUYỀN ADMIN - USER)
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
    <li><strong>Main</strong>: Điểm vào chính của chương trình, khởi động giao diện đăng nhập đầu tiên.</li>

    <li><strong>LoginGUI</strong>: Giao diện đăng nhập, kiểm tra thông tin người dùng trong MongoDB. 
        Nếu đăng nhập là admin thì mở <code>AdminPanelGUI</code>, nếu là người dùng thì mở <code>UserPanelGUI</code>.
    </li>

    <li><strong>RegisterGUI</strong>: Giao diện đăng ký tài khoản mới, ghi thông tin người dùng mới vào MongoDB.</li>

    <li><strong>UserPanelGUI</strong>: Giao diện dành cho người dùng thường — cho phép:
        <ul>
            <li>Gửi file (qua UDP bằng <code>UDPClient</code>)</li>
            <li>Hiển thị danh sách file đã gửi</li>
            <li>Xoá file đã gửi của chính mình khỏi MongoDB</li>
        </ul>
    </li>

    <li><strong>AdminPanelGUI</strong>: Giao diện dành cho admin — cho phép:
        <ul>
            <li>Xem toàn bộ file của tất cả người dùng</li>
            <li>Xoá file bất kỳ khỏi giao diện</li>
            <li>Phân biệt màu hàng trong bảng theo từng <code>user_id</code> để dễ theo dõi</li>
        </ul>
    </li>

    <li><strong>FileTransferGUI</strong>: Cửa sổ hiển thị quá trình gửi file, tiến độ truyền, trạng thái thành công/thất bại.</li>

    <li><strong>UDPServer</strong>: Chương trình <strong>Server (Receiver)</strong> lắng nghe gói tin UDP và ghi dữ liệu nhận được thành file trên server.</li>

    <li><strong>UDPClient</strong>: Chương trình <strong>Client (Sender)</strong> đọc file, chia nhỏ thành các gói tin và gửi qua UDP tới <code>UDPServer</code>.</li>

    <li><strong>MongoDBHelper</strong>: Lớp tiện ích kết nối MongoDB, hỗ trợ các thao tác:
        <ul>
            <li>Xem danh sách file</li>
            <li>Kiểm tra đăng nhập và đăng ký người dùng</li>
        </ul>
    </li>
</ul>

### Kiến trúc
- **Client**: Giao diện người dùng — đăng nhập, đăng ký, gửi file, xem và xoá file.
- **Server**: Lắng nghe gói UDP, nhận file và ghi lại, đồng thời MongoDB lưu metadata file và tài khoản.

Đặc điểm nổi bật:
- Phân quyền admin và user rõ ràng.
- Hiển thị màu sắc khác nhau cho từng user (trên bảng admin).
- Giao diện Swing đơn giản, dễ sử dụng.

## 2. Ngôn ngữ & Công nghệ
[![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Java Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![UDP Socket](https://img.shields.io/badge/UDP%20Socket-007396?style=for-the-badge&logo=socketdotio&logoColor=white)](https://datatracker.ietf.org/doc/html/rfc768)

## 3. Một số màn hình giao diện
<p align="center">
   <img src="images/login.png" alt="Đăng nhập" width="500"/>
</p>
<p align="center">
   <em>Hình 1: Giao diện đăng nhập/đăng ký</em>
</p>

<p align="center">
   <img src="images/userpanel.png" alt="User Panel" width="500"/>
</p>
<p align="center">
   <em>Hình 2: Giao diện người dùng gửi/xoá file</em>
</p>

<p align="center">
   <img src="images/adminpanel.png" alt="Admin Panel" width="500"/>
</p>
<p align="center">
   <em>Hình 3: Giao diện quản lý file của admin</em>
</p>

*(Bạn có thể chụp màn hình thực tế của ứng dụng để thay thế các ảnh trên)*

## 4. Cài đặt & Sử dụng
**Yêu cầu môi trường:**
- Java Development Kit (JDK) 8 trở lên.
- MongoDB.
- IDE: Eclipse/IntelliJ hoặc chạy trực tiếp qua terminal.
- Thư viện MongoDB (.jar): mongodb-driver-sync-5.5.0, bson-5.5.0, mongodb-driver-core-5.5.0

**Cách triển khai:**
1. Import project vào IDE.
2. Chạy file `BTL_LTM` ở MongoDB để khởi tạo cơ sở dữ liệu.
3. Chạy `UDPServer.java`.
4. Chạy nhiều `Main.java` để mở nhiều cửa sổ client.
5. Đăng ký tài khoản và bắt đầu gửi file.

<p><strong>Lưu ý:</strong> UDP không đảm bảo toàn vẹn dữ liệu, nên chỉ nên truyền file nhỏ.</p>

## 5. Thành viên & Thông tin
- **Sinh viên thực hiện**: Đỗ Trường Anh
- **Lớp**: CNTT 16-01
- **Email**: dotruonganh2004@gmail.com

© 2025 AIoTLab – Faculty of Information Technology, DaiNam University
