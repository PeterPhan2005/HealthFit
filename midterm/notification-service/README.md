# NOTIFICATION-SERVICE - iBanking Microservices 📧

Email notification service với **RabbitMQ** để nhận thông báo thanh toán từ PAYMENT-SERVICE.

## 🚀 Thông tin Service

- **Port**: 8086
- **RabbitMQ**: localhost:5672 (admin/admin123)
- **Email**: Gmail SMTP (587)
- **Health Check**: http://localhost:8086/actuator/health

## 🐰 RabbitMQ Integration

### Concept Overview

```
┌──────────────────────────────────────────────────┐
│  1. User thanh toán thành công                   │
│     PAYMENT-SERVICE                              │
└────────────────┬─────────────────────────────────┘
                 │ Publish message
                 │ rabbitTemplate.convertAndSend(...)
                 ▼
        ┌────────────────────┐
        │     RabbitMQ       │
        │  Exchange:         │
        │  payment.exchange  │
        └────────┬───────────┘
                 │ Route by key
                 ▼
        ┌────────────────────┐
        │  Queue:            │
        │  payment.completed │
        │  Messages: [1,2,3] │
        └────────┬───────────┘
                 │ Consume automatically
                 ▼
    ┌────────────────────────┐
    │  NOTIFICATION-SERVICE  │
    │  @RabbitListener       │
    │  handlePaymentSuccess()│
    └────────────┬───────────┘
                 │ Send email
                 ▼
        ┌────────────────┐
        │  Customer Email│
        │  "Thanh toán   │
        │   thành công!" │
        └────────────────┘
```

### RabbitMQ Components

#### 1. Queue
```properties
rabbitmq.queue.payment=payment.completed
```
- Nơi chứa messages
- Durable: true (không mất khi RabbitMQ restart)
- NOTIFICATION-SERVICE listen trên queue này

#### 2. Exchange
```properties
rabbitmq.exchange.payment=payment.exchange
```
- Nhận messages từ PAYMENT-SERVICE
- Type: TopicExchange
- Route messages đến queue theo routing key

#### 3. Binding
```properties
rabbitmq.routing.key.payment=payment.completed
```
- Kết nối: Exchange → Queue
- Pattern matching: `payment.completed`

#### 4. Consumer
```java
@RabbitListener(queues = "${rabbitmq.queue.payment}")
public void handlePaymentSuccess(PaymentEvent event) {
    // Tự động được gọi khi có message!
}
```

## 📡 Message Format

### PaymentEvent (JSON)
```json
{
  "transactionId": 1,
  "customerId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "studentId": "523H1001",
  "studentName": "Nguyen Van A",
  "amount": 3500000.0,
  "semesterCode": "HK2_2024",
  "createdAt": "2025-10-30T12:00:00"
}
```

### Conversion
- **PAYMENT-SERVICE**: Java Object → JSON (automatically)
- **RabbitMQ**: Store JSON
- **NOTIFICATION-SERVICE**: JSON → Java Object (automatically)

## 📧 Email Configuration

### Gmail SMTP Setup

#### Bước 1: Enable 2-Factor Authentication
1. Vào Google Account: https://myaccount.google.com
2. Security → 2-Step Verification → Enable

#### Bước 2: Generate App Password
1. Vào: https://myaccount.google.com/apppasswords
2. Select "Mail" và "Windows Computer"
3. Generate → Copy password (16 ký tự)

#### Bước 3: Update application.properties
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx  # App password (16 chars)
notification.email.from=iBanking Support <your-email@gmail.com>
```

### Email Template

Template: `src/main/resources/templates/payment-success.html`

Variables:
- `${customerName}` - Tên khách hàng
- `${transactionId}` - Mã giao dịch
- `${amount}` - Số tiền (formatted)
- `${studentId}` - Mã sinh viên
- `${studentName}` - Tên sinh viên
- `${semesterCode}` - Mã học kỳ
- `${createdAt}` - Thời gian

## 🐳 Chạy với Docker

### Prerequisites
- RabbitMQ phải đang chạy: http://localhost:15672

### Start Service
```bash
cd notification-service
docker-compose up --build
```

## 🧪 Testing RabbitMQ Flow

### Bước 1: Check RabbitMQ
```bash
# Vào Management UI
http://localhost:15672
Login: admin/admin123

# Tab "Queues" → Thấy queue "payment.completed"
```

### Bước 2: Publish Test Message (Manual)

Vào RabbitMQ Management UI:
1. Tab **"Queues"**
2. Click **"payment.completed"**
3. Expand **"Publish message"**
4. Paste JSON:
```json
{
  "transactionId": 999,
  "customerId": 1,
  "customerName": "Test User",
  "customerEmail": "your-email@gmail.com",
  "studentId": "TEST123",
  "studentName": "Test Student",
  "amount": 1000000.0,
  "semesterCode": "TEST",
  "createdAt": "2025-10-30T12:00:00"
}
```
5. Click **"Publish message"**

### Bước 3: Check Logs
```bash
docker-compose logs -f notification-service
```

Bạn sẽ thấy:
```
📨 Received payment event from RabbitMQ:
   Transaction ID: 999
   Customer: Test User (your-email@gmail.com)
   ...
✅ Payment notification processed successfully!
📧 Email sent to: your-email@gmail.com
```

### Bước 4: Check Email
- Check inbox: `your-email@gmail.com`
- Thấy email "✅ Thanh toán học phí thành công - iBanking"

## 🔄 Full Integration Flow

```
┌────────────────────────────────────────────────┐
│  1. Start RabbitMQ                             │
│     cd rabbitmq                                │
│     docker-compose up -d                       │
└────────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────────┐
│  2. Start NOTIFICATION-SERVICE                 │
│     cd notification-service                    │
│     docker-compose up -d                       │
└────────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────────┐
│  3. Check RabbitMQ Connection                  │
│     Tab "Connections" → Thấy 1 connection      │
│     Tab "Queues" → Thấy queue với 1 consumer  │
└────────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────────┐
│  4. Publish Test Message (Manual)              │
│     RabbitMQ UI → Queues → Publish message     │
└────────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────────┐
│  5. NOTIFICATION-SERVICE receives → Send email │
│     Check logs → Check inbox                   │
└────────────────────────────────────────────────┘
```

## 📊 Monitoring RabbitMQ

### Management UI: http://localhost:15672

#### Overview Tab
- Queued messages: Số lượng messages chờ xử lý
- Message rates: Messages/second
- Connections: Số services đang connect

#### Queues Tab
- **Queue name**: payment.completed
- **Messages**: Số messages trong queue
- **Consumers**: 1 (NOTIFICATION-SERVICE)
- **Get messages**: Xem message content

#### Connections Tab
- **Connection name**: NOTIFICATION-SERVICE connection
- **User**: admin
- **Channels**: 1 channel (for consuming)

## ⚠️ Error Handling

### Scenario 1: RabbitMQ Down
**Symptom:**
```
Connection refused: localhost:5672
```

**Fix:**
```bash
cd ../rabbitmq
docker-compose up -d
```

### Scenario 2: Email Send Failed
**Behavior:**
- Log: ❌ Failed to send email
- Fallback: Send simple text email instead
- Message still consumed (not requeued)

### Scenario 3: Invalid Message Format
**Behavior:**
- Spring AMQP cannot convert JSON
- Message moved to Dead Letter Queue (if configured)
- Or rejected permanently

### Scenario 4: Service Restart
**RabbitMQ Queue Persistence:**
- Queue: Durable = true → Không mất
- Messages: Persistent = true → Không mất
- Consumer restart → Automatically reconnect

## 🎯 Key Features

### ✅ Asynchronous Processing
- PAYMENT-SERVICE không cần đợi email gửi xong
- User experience tốt hơn (fast response)

### ✅ Reliability
- Message persistence (không mất khi restart)
- Retry mechanism (3 attempts)
- Dead Letter Queue (for failed messages)

### ✅ Scalability
- Có thể chạy nhiều instances của NOTIFICATION-SERVICE
- RabbitMQ load balancing tự động

### ✅ Decoupling
- PAYMENT-SERVICE không cần biết NOTIFICATION-SERVICE
- Services independent, loosely coupled

## 🔐 Security

- RabbitMQ: Username/Password authentication
- Gmail: App Password (không phải password chính)
- No JWT needed (internal communication)

## 📝 Configuration Summary

### application.properties
```properties
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin123

# Queue/Exchange names
rabbitmq.queue.payment=payment.completed
rabbitmq.exchange.payment=payment.exchange
rabbitmq.routing.key.payment=payment.completed

# Email (UPDATE THESE!)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
notification.email.from=iBanking Support <your-email@gmail.com>
```

## 🚨 Troubleshooting

### Cannot connect to RabbitMQ
```bash
# Check RabbitMQ is running
docker ps | Select-String rabbitmq

# Check port
Test-NetConnection -ComputerName localhost -Port 5672

# Restart RabbitMQ
cd ../rabbitmq
docker-compose restart
```

### Email not sent
1. Check Gmail App Password is correct
2. Check 2FA is enabled on Gmail
3. Check logs for error messages
4. Try test with Gmail directly first

### Queue not created
1. Check RabbitMQ Config class
2. Check application.properties values
3. Restart service

### Consumer not receiving messages
1. Check @RabbitListener annotation
2. Check queue name matches
3. Check RabbitMQ Connections tab
4. Check logs for connection errors

## 🎓 Learning Points

### @RabbitListener Magic
```java
@RabbitListener(queues = "payment.completed")
public void handlePaymentSuccess(PaymentEvent event) {
    // Spring Boot làm TẤT CẢ:
    // 1. Connect RabbitMQ
    // 2. Subscribe queue
    // 3. Convert JSON → Object
    // 4. Gọi method này
    // 5. Acknowledge message
}
```

### Message Flow
1. **Publish**: PAYMENT-SERVICE → Exchange
2. **Route**: Exchange → Queue (by routing key)
3. **Consume**: Queue → NOTIFICATION-SERVICE
4. **Process**: Send email
5. **Acknowledge**: Remove from queue

### Error Handling
- **Try**: Gửi HTML email với template
- **Catch**: Fallback to simple text email
- **Finally**: Log kết quả

## 🔄 Next Steps

- [ ] Update PAYMENT-SERVICE để publish messages
- [ ] Test full flow: Payment → RabbitMQ → Email
- [ ] Monitor RabbitMQ Management UI
- [ ] Check email inbox

---

**QUAN TRỌNG**: Nhớ update Gmail credentials trong `application.properties` trước khi test!
