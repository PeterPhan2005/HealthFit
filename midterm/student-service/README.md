# STUDENT-SERVICE - iBanking Microservices

Student management microservice cho hệ thống iBanking.

## 🚀 Thông tin Service

- **Port**: 8083
- **Database**: PostgreSQL (port 5435) - `student_db`
- **API Documentation**: http://localhost:8083/docs
- **Health Check**: http://localhost:8083/actuator/health

## 📋 Data mẫu

### Students (12 sinh viên)
- 523H1001 - 523H1010 (10 sinh viên mới)
- 523H0073, 523H0074 (2 sinh viên cũ)

### Semesters (5 học kỳ)
- HK1_2024 - Học kỳ 1 năm 2024
- HK2_2024 - Học kỳ 2 năm 2024
- HK3_2024 - Học kỳ hè năm 2024
- HK1_2025 - Học kỳ 1 năm 2025
- HK2_2025 - Học kỳ 2 năm 2025

## 🐳 Chạy bằng Docker

```bash
cd student-service
docker-compose up --build
```

## 📡 API Endpoints

### 1. Search Student
```http
GET http://localhost:8083/api/students/search/{studentId}
Authorization: Bearer {jwt_token}
```

**Example:**
```http
GET http://localhost:8083/api/students/search/523H1001
```

**Response:**
```json
{
  "success": true,
  "studentId": "523H1001",
  "fullName": "Nguyen Van A",
  "email": "s1001@example.com",
  "tuitionFee": 3500000
}
```

### 2. Get Student by ID (Internal API)
```http
GET http://localhost:8083/api/students/{studentId}
```

### 3. Get Student Semesters with Fees
```http
GET http://localhost:8083/api/students/{studentId}/semesters
Authorization: Bearer {jwt_token}
```

**Response:**
```json
[
  {
    "id": 1,
    "semesterCode": "HK1_2024",
    "semesterName": "Học kỳ 1 năm 2024",
    "fee": 3500000,
    "paidAmount": 3500000,
    "remainingFee": 0,
    "isPaid": true,
    "canPay": false
  },
  {
    "id": 2,
    "semesterCode": "HK2_2024",
    "semesterName": "Học kỳ 2 năm 2024",
    "fee": 3500000,
    "paidAmount": 0,
    "remainingFee": 3500000,
    "isPaid": false,
    "canPay": true
  }
]
```

### 4. Get Student Fees (Detailed)
```http
GET http://localhost:8083/api/students/{studentId}/fees
Authorization: Bearer {jwt_token}
```

### 5. Get All Semesters
```http
GET http://localhost:8083/api/students/semesters/all
Authorization: Bearer {jwt_token}
```

## 🔐 Security

- JWT Token validation (same secret as AUTH-SERVICE)
- All endpoints except health/docs require valid JWT
- No login endpoint (login ở AUTH-SERVICE)

## 🏗️ APIs by Purpose

**For End Users (via API Gateway):**
- `GET /api/students/search/{studentId}` - Search student
- `GET /api/students/{studentId}/semesters` - Get semester fees
- `GET /api/students/{studentId}/fees` - Get detailed fees

**For Other Services (Internal):**
- `GET /api/students/{studentId}` - Get student data (for PAYMENT-SERVICE)
- `GET /api/students/semesters/all` - Get all semesters

## 🧪 Testing với Postman

1. Login qua AUTH-SERVICE để lấy JWT token
2. Test STUDENT-SERVICE với token đó

```bash
# 1. Login (AUTH-SERVICE)
POST http://localhost:8081/api/auth/login
{
  "username": "john",
  "password": "john123"
}

# 2. Search student (STUDENT-SERVICE)
GET http://localhost:8083/api/students/search/523H1001
Authorization: Bearer {token}

# 3. Get semesters
GET http://localhost:8083/api/students/523H1001/semesters
Authorization: Bearer {token}
```

## 📊 Database Schema

**Table: students**
- studentId (PK) - VARCHAR(20)
- fullName - VARCHAR(50)
- email - VARCHAR(100) UNIQUE
- fee - DOUBLE
- version - BIGINT (Optimistic locking)

**Table: semesters**
- id (PK) - BIGINT
- semesterCode - VARCHAR(30) UNIQUE
- semesterName - VARCHAR(100)
- year - VARCHAR(20)
- semesterOrder - INT
- isActive - BOOLEAN

**Table: student_semester_fees**
- id (PK) - BIGINT
- studentId (FK) - VARCHAR(20)
- semesterId (FK) - BIGINT
- fee - DOUBLE
- paidAmount - DOUBLE
- isPaid - BOOLEAN
- version - BIGINT (Optimistic locking)
- UNIQUE(studentId, semesterId)

## 🔄 Business Logic

### Quy tắc thanh toán học phí:
- Sinh viên phải thanh toán **tuần tự** theo thứ tự học kỳ
- Chỉ có thể thanh toán học kỳ tiếp theo nếu học kỳ trước **đã thanh toán**
- `canPay = !isPaid && allPreviousSemestersPaid`

### Ví dụ:
```
HK1_2024: PAID ✅ → canPay = false
HK2_2024: UNPAID ❌ → canPay = true (có thể thanh toán)
HK3_2024: UNPAID ❌ → canPay = false (chưa thanh toán HK2)
```

## 🔄 Next Steps

- [x] STUDENT-SERVICE standalone - DONE
- [ ] Integrate với PAYMENT-SERVICE via OpenFeign
- [ ] Add caching for student/semester data
- [ ] Add search by name, email

## 📝 Notes

- Service này KHÔNG có Customer entity (đã tách riêng)
- Optimistic locking cho Student và StudentSemesterFee
- Pessimistic locking cho StudentRepository.findByStudentIdForUpdate()
- Port 8083, database port 5435
