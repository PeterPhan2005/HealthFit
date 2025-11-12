package com.soa.otp.templates;

public final class EmailTemplates {
    private EmailTemplates() {}

    public static String otpTemplate() {
        return "<!-- OTP Email Template (Vietnamese) -->\n" +
                "<!doctype html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <title>OTP Xác nhận giao dịch</title>\n" +
                "</head>\n" +
                "<body style=\"margin:0;padding:0;background:#f5f7fb;font-family:Segoe UI,Roboto,Helvetica,Arial,sans-serif;color:#1f2937;\">\n" +
                "  <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:#f5f7fb;padding:24px 0;\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\">\n" +
                "        <table role=\"presentation\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 10px 24px rgba(17,24,39,0.08);\">\n" +
                "          <!-- Header -->\n" +
                "          <tr>\n" +
                "            <td style=\"background:linear-gradient(90deg,#4f46e5,#7c3aed);padding:24px 28px;\">\n" +
                "              <h1 style=\"margin:0;font-size:20px;line-height:1.4;color:#ffffff;\">Hệ thống Thanh toán Học phí</h1>\n" +
                "              <p style=\"margin:4px 0 0;font-size:13px;opacity:.9;color:#e9eafc;\">Xác thực giao dịch bằng mã OTP</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "          <!-- Body -->\n" +
                "          <tr>\n" +
                "            <td style=\"padding:28px;\">\n" +
                "              <p style=\"margin:0 0 12px;font-size:16px;\">Kính gửi <strong>[[fullName]]</strong>,</p>\n" +
                "              <p style=\"margin:0 0 16px;font-size:14px;color:#6b7280;\">\n" +
                "                Quý khách vừa khởi tạo yêu cầu xác nhận giao dịch thanh toán học phí cho MSSV\n" +
                "                <strong>[[studentId]]</strong> trên hệ thống.\n" +
                "              </p>\n" +
                "\n" +
                "              <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin:16px 0;\">\n" +
                "                <tr>\n" +
                "                  <td align=\"center\" style=\"background:#f9fafb;border:1px dashed #d1d5db;padding:18px;border-radius:10px;\">\n" +
                "                    <div style=\"font-size:13px;color:#6b7280;margin-bottom:6px;\">Mã OTP của bạn</div>\n" +
                "                    <div style=\"font-weight:700;font-size:28px;letter-spacing:6px;color:#111827;\">\n" +
                "                      [[otp]]\n" +
                "                    </div>\n" +
                "                    <div style=\"font-size:12px;color:#6b7280;margin-top:6px;\">\n" +
                "                      Hiệu lực: <strong>5 phút</strong>\n" +
                "                    </div>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "\n" +
                "              <p style=\"margin:0 0 10px;font-size:13px;color:#6b7280;\">\n" +
                "                Lưu ý bảo mật:\n" +
                "              </p>\n" +
                "              <ul style=\"margin:0 0 16px 18px;padding:0;color:#6b7280;font-size:13px;\">\n" +
                "                <li>Không chia sẻ OTP cho bất kỳ ai, kể cả nhân viên.</li>\n" +
                "                <li>Mã sẽ hết hạn sau 5 phút hoặc khi yêu cầu OTP mới.</li>\n" +
                "                <li>Nếu không phải quý khách thực hiện, vui lòng bỏ qua email này.</li>\n" +
                "              </ul>\n" +
                "\n" +
                "              <div style=\"margin:18px 0 0;font-size:12px;color:#9ca3af;\">\n" +
                "                Mọi thắc mắc vui lòng liên hệ <a href=\"mailto:[[supportEmail]]\" style=\"color:#4f46e5;text-decoration:none;\">[[supportEmail]]</a>.\n" +
                "              </div>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "\n" +
                "          <!-- Footer -->\n" +
                "          <tr>\n" +
                "            <td style=\"background:#f9fafb;padding:16px 28px;\">\n" +
                "              <p style=\"margin:0;font-size:12px;color:#9ca3af;\">\n" +
                "                © 2025 Hệ thống Thanh toán Học phí. All rights reserved.\n" +
                "              </p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <div style=\"height:16px;\"></div>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>\n" +
                "</html>";
    }
}
