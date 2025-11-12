# PAYMENT-SERVICE - iBanking Microservices 💰

Payment processing microservice với **OpenFeign** để gọi CUSTOMER và STUDENT services.

## 🚀 Thông tin Service

- **Port**: 8084
- **Database**: PostgreSQL (port 5436) - `payment_db`
- **API Documentation**: http://localhost:8084/docs
- **Health Check**: http://localhost:8084/actuator/health
- **OpenFeign**: Gọi CUSTOMER-SERVICE (8082) và STUDENT-SERVICE (8083)

## 🔗 Dependencies (OpenFeign)

PAYMENT-SERVICE cần 2 services khác **PHẢI ĐANG CHẠY**:
- ✅ **CUSTOMER-SERVICE** (Port 8082) - Check balance, deduct money
- ✅ **STUDENT-SERVICE** (Port 8083) - Verify student, get fees

## 🐳 Chạy bằng Docker

### Quan trọng: Chạy theo thứ tự!

```bash
# Bước 1: Start CUSTOMER-SERVICE
cd ../customer-service
docker-compose up -d

# Bước 2: Start STUDENT-SERVICE
cd ../student-service
docker-compose up -d

# Bước 3: Start PAYMENT-SERVICE
cd ../payment-service
docker-compose up --build
```

**Tại sao phải theo thứ tự?**
- PAYMENT-SERVICE sử dụng OpenFeign để gọi 2 service kia
- Nếu 2 service kia chưa chạy → PAYMENT-SERVICE sẽ báo lỗi khi gọi API

## 📡 API Endpoints

### 1. Process Simple Payment
```http
POST http://localhost:8084/api/payments/pay
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
  "message": "Payment successful",
  "transactionId": 1,
  "amount": 3500000,
  "studentId": "523H1001",
  "studentName": "Nguyen Van A"
}
```

### 2. Process Payment with Semester
```http
POST http://localhost:8084/api/payments/pay-semester
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "studentId": "523H1001",
  "semesterFeeId": 5
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment successful",
  "transactionId": 2,
  "amount": 3500000,
  "studentId": "523H1001",
  "studentName": "Nguyen Van A",
  "semesterCode": "HK2_2024"
}
```

### 3. Get Transaction History
```http
GET http://localhost:8084/api/payments/history
Authorization: Bearer {jwt_token}
```

**Response:**
```json
[
  {
    "id": 1,
    "amount": 3500000,
    "createdAt": "2025-10-30T11:30:00",
    "studentId": "523H1001",
    "studentName": "Nguyen Van A",
    "semesterCode": "HK2_2024"
  }
]
```

## 🔄 OpenFeign Flow

```
┌─────────────────────────────────────────────────────┐
│  User: POST /api/payments/pay                       │
│  Body: { "studentId": "523H1001" }                  │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
    ┌────────────────────────┐
    │   PAYMENT-SERVICE      │
    │   PaymentController    │
    └───────────┬────────────┘
                │
                ▼
    ┌────────────────────────┐
    │   PaymentService       │
    │   (Business Logic)     │
    └───┬──────────────┬─────┘
        │              │
        │ OpenFeign    │ OpenFeign
        │              │
        ▼              ▼
┌───────────────┐  ┌───────────────┐
│ CustomerClient│  │ StudentClient │
│  getCustomer  │  │  getStudent   │
│  deductBalance│  └───────────────┘
└───────┬───────┘
        │ HTTP GET/PUT
        ▼
┌───────────────────────────────┐
│  CUSTOMER-SERVICE (8082)      │
│  - Check balance: 20M         │
│  - Deduct: 3.5M               │
│  - New balance: 16.5M         │
└───────────────────────────────┘
        │
        ▼
┌───────────────────────────────┐
│  STUDENT-SERVICE (8083)       │
│  - Verify student exists      │
│  - Get tuition fee: 3.5M      │
└───────────────────────────────┘
        │
        ▼
┌───────────────────────────────┐
│  Transaction saved!           │
│  ID: 1, Amount: 3.5M          │
└───────────────────────────────┘
```

## 🧪 Testing Full Flow

### Bước 1: Login (AUTH-SERVICE)
```bash
POST http://localhost:8081/api/auth/login
{
  "username": "john",
  "password": "john123"
}

# Response: Copy JWT token
```

### Bước 2: Check Balance (CUSTOMER-SERVICE)
```bash
GET http://localhost:8082/api/customers/balance
Authorization: Bearer {token}

# Response: { "balance": "20.000.000 ₫" }
```

### Bước 3: Search Student (STUDENT-SERVICE)
```bash
GET http://localhost:8083/api/students/search/523H1001
Authorization: Bearer {token}

# Response: Student found with fee
```

### Bước 4: Process Payment (PAYMENT-SERVICE)
```bash
POST http://localhost:8084/api/payments/pay
Authorization: Bearer {token}
{
  "studentId": "523H1001"
}

# Response: Payment successful!
```

### Bước 5: Verify Balance Deducted
```bash
GET http://localhost:8082/api/customers/balance
Authorization: Bearer {token}

# Response: { "balance": "16.500.000 ₫" } ✅
```

## 📊 Database Schema

**Table: transactions**
- id (PK) - BIGINT
- amount - DOUBLE
- createdTime - TIMESTAMP
- processing - BOOLEAN
- customerId - BIGINT (FK to CUSTOMER-SERVICE)
- customerName - VARCHAR(100)
- studentId - VARCHAR(20) (FK to STUDENT-SERVICE)
- studentName - VARCHAR(100)
- semesterId - BIGINT
- semesterCode - VARCHAR(30)
- studentSemesterFeeId - BIGINT

**Note:** Trong microservices, chúng ta lưu IDs thay vì entity relationships!

## ⚙️ OpenFeign Configuration

### application.properties
```properties
# URLs of other services
customer-service.url=http://localhost:8082
student-service.url=http://localhost:8083

# Timeout settings
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000
```

### Feign Clients
- **CustomerServiceClient**: Gọi `/api/customers/{id}` và `/api/customers/{id}/deduct`
- **StudentServiceClient**: Gọi `/api/students/{id}` và `/api/students/{id}/semesters`

## ⚠️ Error Handling

### InsufficientBalanceException
```json
{
  "success": false,
  "message": "Insufficient balance. Need 3500000, have 1000000"
}
```

### AlreadyPaidException
```json
{
  "success": false,
  "message": "Tuition already paid"
}
```

### Student Not Found
```json
{
  "success": false,
  "message": "Student not found"
}
```

### Service Down (Feign Exception)
```
FeignException$ServiceUnavailable: [503] 
Service temporarily unavailable
```

## 🔐 Security

- JWT Token validation (same secret as AUTH-SERVICE)
- All endpoints require valid JWT
- Customer ID extracted from JWT token
- No direct database access to customers/students (via OpenFeign)

## 🚨 Troubleshooting

### Error: "Connection refused to localhost:8082"
**Cause:** CUSTOMER-SERVICE not running
**Fix:**
```bash
cd ../customer-service
docker-compose up -d
```

### Error: "Connection refused to localhost:8083"
**Cause:** STUDENT-SERVICE not running
**Fix:**
```bash
cd ../student-service
docker-compose up -d
```

### Error: "Customer not found"
**Cause:** JWT token invalid hoặc customer không tồn tại
**Fix:** Login lại để lấy token mới

### Error: "Timeout"
**Cause:** Service khác phản hồi quá chậm
**Fix:** Tăng timeout trong application.properties:
```properties
feign.client.config.default.readTimeout=20000
```

## 🎯 Key Features

### ✅ OpenFeign Integration
- Declarative REST client
- Automatic JSON serialization/deserialization
- Type-safe API calls
- Built-in error handling

### ✅ Transaction Management
- Save transaction history
- Store customer and student IDs (not entities)
- Semester-based payment support

### ✅ Business Logic
- Balance validation before payment
- Check if already paid
- Sequential semester payment enforcement

## 🔄 Next Steps

- [x] PAYMENT-SERVICE with OpenFeign - DONE
- [ ] Add Circuit Breaker (Resilience4j) for fault tolerance
- [ ] Add RabbitMQ for async notifications
- [ ] Implement retry logic for failed Feign calls
- [ ] Add distributed tracing (Zipkin/Sleuth)

## 📝 Notes

- **Không có Customer entity** trong service này - chỉ lưu ID
- **Không có Student entity** - gọi via OpenFeign
- OpenFeign tự động convert JSON → Java objects
- Service này phụ thuộc vào 2 services khác (tight coupling)
- Cần implement Circuit Breaker để handle service downtime

## 🎓 Học từ PAYMENT-SERVICE

Đây là service đầu tiên sử dụng **OpenFeign** trong dự án!

**Bài học:**
1. ✅ OpenFeign đơn giản hơn RestTemplate rất nhiều
2. ✅ Interface giống Repository → dễ sử dụng
3. ✅ Tự động serialize/deserialize JSON
4. ⚠️ Cần ensure services khác đang chạy
5. ⚠️ Error handling quan trọng (FeignException)

**Pattern:**
```java
// Define interface
@FeignClient(name = "customer-service", url = "...")
interface CustomerServiceClient {
    @GetMapping("/api/customers/{id}")
    Map<String, Object> getCustomer(@PathVariable Long id);
}

// Use it
@Autowired
CustomerServiceClient customerClient;

// Call like normal method!
Map<String, Object> customer = customerClient.getCustomer(1L);
```
