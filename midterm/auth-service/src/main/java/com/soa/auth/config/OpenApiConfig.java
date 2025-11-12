package com.soa.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iBanking API - Hệ thống thanh toán học phí")
                        .version("1.0.0")
                        .description("""
                                # Hệ thống thanh toán học phí trực tuyến - iBanking
                                
                                ## Tổng quan
                                API này cung cấp các chức năng thanh toán học phí trực tuyến an toàn với xác thực OTP qua email.
                                
                                ## Tính năng chính
                                - **Xác thực người dùng**: Đăng nhập/đăng xuất với session
                                - **Quản lý thanh toán**: Thanh toán học phí theo học kỳ với validation thứ tự
                                - **OTP Security**: Xác thực giao dịch bằng mã OTP gửi qua email (TTL 5 phút)
                                - **Tra cứu**: Tìm kiếm sinh viên, xem lịch sử giao dịch
                                - **Real-time Balance**: Kiểm tra số dư tài khoản theo thời gian thực
                                
                                ## Luồng thanh toán
                                1. Đăng nhập: `POST /api/auth/login`
                                2. Tìm sinh viên: `GET /api/student/search?studentId={mssv}`
                                3. Lấy danh sách học kỳ: `GET /api/student/{studentId}/semesters`
                                4. Gửi OTP: `POST /api/mail/send-otp/{studentId}`
                                5. Xác thực OTP & thanh toán: `GET /api/otp/transaction?otp={otp}&studentId={id}&semesterFeeId={feeId}`
                                6. Xem lịch sử: `GET /api/transactions/history`
                                
                                ## Bảo mật
                                - Session-based authentication với HTTP cookies
                                - OTP timeout 5 phút
                                - Pessimistic locking để tránh race condition
                                - Payment order validation (phải thanh toán học kỳ trước mới thanh toán tiếp)
                                
                                ## Test Accounts
                                - **Admin**: `admin/admin123` (Số dư: 10.000.000 VNĐ)
                                - **Mary**: `mary/mary123` (Số dư: 15.000.000 VNĐ)
                                - **John**: `john/john123` (Số dư: 20.000.000 VNĐ)
                                
                                ## Test Student IDs
                                - **523H1001** đến **523H1012**: 12 sinh viên với 4 học kỳ mỗi người
                                - **523H1005**, **523H0073**: Đã thanh toán đủ tất cả học kỳ
                                """)
                        .contact(new Contact()
                                .name("iBanking Support")
                                .email("support@ibanking.edu.vn")
                                .url("https://ibanking.edu.vn"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.ibanking.edu.vn")
                                .description("Production Server")
                ));
    }
}
