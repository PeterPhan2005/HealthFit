# OTP-SERVICE - iBanking Microservices 🔐

One-Time Password service for secure payment verification.

## 🚀 Thông tin Service

- **Port**: 8085
- **Database**: PostgreSQL (port 5437) - `otp_db`
- **API Documentation**: http://localhost:8085/actuator/health
- **Health Check**: http://localhost:8085/actuator/health

## 💡 Chức năng

OTP-SERVICE quản lý mã OTP 6 chữ số cho xác thực thanh toán:
- ✅ **Generate OTP**: Tạo mã OTP 6 chữ số ngẫu nhiên
- ✅ **Verify OTP**: Xác thực OTP code
- ✅ **Auto-expire**: OTP tự động hết hạn sau 5 phút
- ✅ **One-time use**: OTP chỉ sử dụng được 1 lần
- ✅ **Scheduled cleanup**: Tự động xóa OTP hết hạn mỗi 5 phút

## 🐳 Chạy bằng Docker

```bash
# Start service với PostgreSQL
docker-compose up --build

# Stop service
docker-compose down

# View logs
docker-compose logs -f otp-service
```

## 📡 API Endpoints

### 1. Generate OTP
```http
POST http://localhost:8085/api/otp/generate
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": "523H1001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP generated successfully",
  "code": "123456",
  "expiresInMinutes": 5
}
```

### 2. Verify OTP
```http
POST http://localhost:8085/api/otp/verify
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": "523H1001",
  "code": "123456"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "OTP verified successfully"
}
```

**Response (Invalid/Expired):**
```json
{
  "success": false,
  "message": "Invalid or expired OTP"
}
```

## 🔄 OTP Flow

```
┌─────────────────────────────────────────────────────┐
│  User: Make payment request                         │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
    ┌────────────────────────┐
    │   PAYMENT-SERVICE      │
    │   (Future: OpenFeign)  │
    └───────────┬────────────┘
                │ Call OTP-SERVICE
                ▼
    ┌────────────────────────┐
    │   OTP-SERVICE          │
    │   POST /api/otp/generate│
    └───────────┬────────────┘
                │ Generate 6-digit code
                ▼
        ┌───────────────┐
        │  Code: 123456 │
        │  Expires: 5min│
        │  Used: false  │
        └───────┬───────┘
                │
                ▼
    ┌────────────────────────┐
    │   Send OTP to User     │
    │   (Email/SMS)          │
    └───────────┬────────────┘
                │ User enters OTP
                ▼
    ┌────────────────────────┐
    │   PAYMENT-SERVICE      │
    │   POST /api/otp/verify │
    └───────────┬────────────┘
                │ Valid?
                ▼
        ┌───────────────┐
        │  Mark as used │
        │  Process pay  │
        └───────────────┘
```

## 🧪 Testing Flow

### Bước 1: Login (AUTH-SERVICE)
```bash
POST http://localhost:8081/api/auth/login
{
  "username": "john",
  "password": "john123"
}

# Response: Copy JWT token
```

### Bước 2: Generate OTP
```bash
POST http://localhost:8085/api/otp/generate
Authorization: Bearer {token}
{
  "studentId": "523H1001"
}

# Response: { "code": "123456" }
```

### Bước 3: Verify OTP
```bash
POST http://localhost:8085/api/otp/verify
Authorization: Bearer {token}
{
  "studentId": "523H1001",
  "code": "123456"
}

# Response: { "success": true }
```

### Bước 4: Try Again (Should Fail)
```bash
POST http://localhost:8085/api/otp/verify
Authorization: Bearer {token}
{
  "studentId": "523H1001",
  "code": "123456"
}

# Response: { "success": false, "message": "Invalid or expired OTP" }
# Because OTP was already used!
```

## 📊 Database Schema

**Table: otps**
- id (PK) - BIGINT
- student_id - VARCHAR(20) - Student ID này thanh toán
- customer_id - BIGINT - Customer ID thanh toán
- code - VARCHAR(6) - Mã OTP 6 chữ số
- is_used - BOOLEAN - Đã sử dụng chưa?
- created_at - TIMESTAMP - Thời gian tạo

**Indexes:**
- idx_otp_student_customer (student_id, customer_id) - Query nhanh
- idx_otp_created_at (created_at) - Cleanup nhanh

## ⚙️ Configuration

### application.properties
```properties
# Server
server.port=8085

# Database
spring.datasource.url=jdbc:postgresql://localhost:5437/otp_db

# OTP Settings
otp.length=6
otp.validity.minutes=5

# JWT (same secret as other services)
jwt.secret=357638792F423F4428472B4B6250655368566D597133743677397A2443264629
```

### Scheduled Tasks
```java
@Scheduled(fixedDelay = 300000)  // Every 5 minutes
public void cleanupExpiredOtps() {
    LocalDateTime expireTime = LocalDateTime.now().minusMinutes(5);
    otpRepository.deleteExpiredOtps(expireTime);
}
```

## ⚠️ Error Handling

### Student ID Missing
```json
{
  "success": false,
  "message": "Student ID is required"
}
```

### OTP Code Missing
```json
{
  "success": false,
  "message": "Student ID and OTP code are required"
}
```

### OTP Expired (> 5 minutes)
```json
{
  "success": false,
  "message": "Invalid or expired OTP"
}
```

### OTP Already Used
```json
{
  "success": false,
  "message": "Invalid or expired OTP"
}
```

## 🔐 Security

- JWT Token validation (same secret as AUTH-SERVICE)
- All endpoints require valid JWT
- Customer ID extracted from JWT token
- OTP tied to specific (customerId, studentId) pair
- One-time use enforcement

## 🚨 Troubleshooting

### Error: "Invalid or expired OTP"
**Possible causes:**
1. OTP đã quá 5 phút
2. OTP đã được sử dụng rồi
3. Wrong OTP code
4. Wrong student ID
5. Wrong customer (JWT token khác)

**Fix:** Generate new OTP

### Error: Database connection failed
**Cause:** PostgreSQL not running
**Fix:**
```bash
docker-compose up postgres -d
```

## 🎯 Key Features

### ✅ Security
- 6-digit random OTP
- SecureRandom generator
- Customer/Student binding
- One-time use

### ✅ Performance
- Database indexes for fast lookup
- Scheduled cleanup (no manual intervention)
- Lightweight (no OpenFeign dependencies)

### ✅ Reliability
- Auto-expire after 5 minutes
- Transaction-safe (verifyAndConsumeOtp)
- Proper error handling

## 🔄 Future Enhancements

- [ ] Add OpenFeign client cho PAYMENT-SERVICE
- [ ] Send OTP via Email (NOTIFICATION-SERVICE)
- [ ] Send OTP via SMS (Twilio integration)
- [ ] Rate limiting (prevent OTP spam)
- [ ] Admin API để xem OTP history
- [ ] Metrics (success rate, average time to verify)

## 📝 Notes

- **Không lưu Customer entity** - chỉ lưu customer ID
- **Không lưu Student entity** - chỉ lưu student ID
- OTP chỉ valid cho cặp (customerId, studentId)
- Schedule cleanup chạy mỗi 5 phút
- OTP code là **6 chữ số** (000000 - 999999)

## 🎓 Học từ OTP-SERVICE

**Pattern:**
```java
// Generate OTP
String code = String.format("%06d", random.nextInt(1000000));
Otp otp = new Otp(studentId, customerId, code);
otpRepository.save(otp);

// Verify và mark as used (atomic)
@Transactional
public boolean verifyAndConsumeOtp(...) {
    Optional<Otp> otpOpt = otpRepository.findValidOtp(...);
    if (otpOpt.isPresent()) {
        otpRepository.markAsUsed(otp.getId());
        return true;
    }
    return false;
}
```

**Bài học:**
1. ✅ SecureRandom cho security
2. ✅ @Transactional để ensure consistency
3. ✅ Scheduled task cho cleanup
4. ✅ One-time use pattern
5. ✅ Time-based expiration
