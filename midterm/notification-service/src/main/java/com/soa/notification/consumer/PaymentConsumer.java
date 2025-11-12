package com.soa.notification.consumer;

import com.soa.notification.dto.PaymentEvent;
import com.soa.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

/**
 * PaymentConsumer - Lắng nghe messages từ RabbitMQ
 * 
 * ============================================
 * GIẢI THÍCH @RabbitListener:
 * ============================================
 * 
 * @RabbitListener(queues = "payment.completed")
 * - Spring Boot tự động:
 *   1. Connect đến RabbitMQ
 *   2. Subscribe vào queue "payment.completed"
 *   3. Khi có message mới → Gọi method này
 *   4. Tự động convert JSON → PaymentEvent object
 * 
 * - Bạn KHÔNG CẦN code gì thêm!
 * - Chỉ cần annotation + method → Done!
 * 
 * ============================================
 * LUỒNG HOẠT ĐỘNG:
 * ============================================
 * 
 * 1. PAYMENT-SERVICE publish message:
 *    rabbitTemplate.convertAndSend("payment.exchange", "payment.completed", event);
 * 
 * 2. RabbitMQ nhận message → Lưu vào queue "payment.completed"
 * 
 * 3. NOTIFICATION-SERVICE (service này) đang listen:
 *    - Spring AMQP tự động detect message mới
 *    - Convert JSON → PaymentEvent
 *    - Gọi method handlePaymentSuccess(event)
 * 
 * 4. Method này gửi email confirmation
 * 
 * 5. Xong → Message bị xóa khỏi queue (acknowledged)
 * 
 * ============================================
 * ERROR HANDLING:
 * ============================================
 * 
 * - Nếu method throw exception:
 *   → Message sẽ được requeue (đưa lại vào queue)
 *   → Retry lại (theo config: max 3 lần)
 *   → Sau 3 lần → Message vào Dead Letter Queue (nếu config)
 * 
 * - Best practice: Try-catch trong method để handle errors
 */
@Component
public class PaymentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);

    @Autowired
    private EmailService emailService;

    /**
     * Handle payment success event
     * 
     * QUAN TRỌNG:
     * - Method này được gọi TỰ ĐỘNG khi có message
     * - Parameter "event" được convert TỰ ĐỘNG từ JSON
     * - Không cần return gì (void)
     * - Throw exception → Message sẽ retry
     * 
     * @param event Payment event từ PAYMENT-SERVICE
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment}")
    public void handlePaymentSuccess(PaymentEvent event) {
        logger.info("==========================================================");
        logger.info("📨 Received payment event from RabbitMQ:");
        logger.info("   Transaction ID: {}", event.getTransactionId());
        logger.info("   Customer: {} ({})", event.getCustomerName(), event.getCustomerEmail());
        logger.info("   Student: {} ({})", event.getStudentName(), event.getStudentId());
        logger.info("   Amount: {}", event.getFormattedAmount());
        logger.info("   Semester: {}", event.getSemesterCode());
        logger.info("==========================================================");

        try {
            // Send email confirmation
            sendPaymentConfirmationEmail(event);
            
            logger.info("✅ Payment notification processed successfully!");
            
        } catch (Exception e) {
            logger.error("❌ Failed to process payment notification: {}", e.getMessage());
            // Throw exception để RabbitMQ retry
            throw new RuntimeException("Failed to send payment notification", e);
        }
    }

    /**
     * Send payment confirmation email
     * 
     * THYMELEAF TEMPLATE:
     * - Template file: src/main/resources/templates/payment-success.html
     * - Variables: ${customerName}, ${amount}, ${studentName}, etc.
     * 
     * CONTEXT:
     * - Context object chứa variables cho template
     * - context.setVariable("key", value)
     * - Template dùng: ${key}
     */
    private void sendPaymentConfirmationEmail(PaymentEvent event) {
        try {
            // Create Thymeleaf context với variables
            Context context = new Context();
            context.setVariable("customerName", event.getCustomerName());
            context.setVariable("transactionId", event.getTransactionId());
            context.setVariable("amount", event.getFormattedAmount());
            context.setVariable("studentId", event.getStudentId());
            context.setVariable("studentName", event.getStudentName());
            context.setVariable("semesterCode", event.getSemesterCode() != null ? event.getSemesterCode() : "N/A");
            context.setVariable("createdAt", event.getFormattedDateTime());

            // Send email using template
            emailService.sendHtmlEmail(
                    event.getCustomerEmail(),
                    "✅ Thanh toán học phí thành công - iBanking",
                    "payment-success", // Template name
                    context
            );

            logger.info("📧 Email sent to: {}", event.getCustomerEmail());

        } catch (Exception e) {
            logger.error("❌ Failed to send email: {}", e.getMessage());
            
            // Fallback: Send simple text email
            String simpleMessage = String.format(
                    "Xin chào %s,\n\n" +
                    "Thanh toán học phí thành công!\n" +
                    "Mã giao dịch: %d\n" +
                    "Số tiền: %s\n" +
                    "Sinh viên: %s (%s)\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ iBanking!",
                    event.getCustomerName(),
                    event.getTransactionId(),
                    event.getFormattedAmount(),
                    event.getStudentName(),
                    event.getStudentId()
            );

            emailService.sendSimpleEmail(
                    event.getCustomerEmail(),
                    "Thanh toán học phí thành công",
                    simpleMessage
            );
        }
    }
}
