# AUTH-SERVICE - iBanking Microservices

Authentication microservice cho hệ thống iBanking.

## 🚀 Thông tin Service

- **Port**: 8081
- **Database**: PostgreSQL (port 5433) - `auth_db`
- **API Documentation**: http://localhost:8081/docs
- **Health Check**: http://localhost:8081/actuator/health

## 📋 Tài khoản Test

| Username | Password  | Balance    | Email                         |
|----------|-----------|------------|-------------------------------|
| admin    | admin123  | 10,000,000 | phanhuyphat021@gmail.com      |
| mary     | mary123   | 15,000,000 | phanhuyphat6@gmail.com        |
| john     | john123   | 20,000,000 | peter.contact.work@gmail.com  |

## 🐳 Chạy bằng Docker

### Prerequisites
- Docker Desktop installed
- Port 8081 và 5433 không bị chiếm

### Build và Run
```bash
cd auth-service
docker-compose up --build
```

### Stop
```bash
docker-compose down
```

### Stop và xóa data
```bash
docker-compose down -v
```

## 🔧 Chạy trực tiếp (không Docker)

### Prerequisites
- Java 17
- Maven 3.9+
- PostgreSQL 17.6 (chạy trên port 5433 hoặc sửa application.properties)

### Tạo Database
```sql
CREATE DATABASE auth_db;
```

### Run Service
```bash
mvn clean install
mvn spring-boot:run
```

## 📡 API Endpoints

### 1. Login
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "john123"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "john",
  "fullName": "John Tran",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 2. Get Balance
```http
GET http://localhost:8081/api/home/balance
Authorization: Bearer {token}
```

**Response:**
```json
{
  "balance": "20.000.000 ₫"
}
```

## 🔐 Security

- JWT Token authentication
- Token expiration: 24 hours (86400000 ms)
- BCrypt password encoding
- Protected endpoints require valid JWT token

## 🏗️ Cấu trúc Project

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/soa/auth/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── security/        # Security & JWT
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utilities
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## 🧪 Testing

### Test Login với curl:
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john\",\"password\":\"john123\"}"
```

### Test Balance với token:
```bash
curl -X GET http://localhost:8081/api/home/balance \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

### View Logs
```bash
docker-compose logs -f auth-service
```

## 🔄 Microservices Architecture

AUTH-SERVICE là service đầu tiên trong kiến trúc microservices iBanking:

1. **AUTH-SERVICE** (Port 8081) ✅ - Hiện tại
2. CUSTOMER-SERVICE (Port 8082) - Coming soon
3. STUDENT-SERVICE (Port 8083) - Coming soon
4. PAYMENT-SERVICE (Port 8084) - Coming soon
5. OTP-SERVICE (Port 8085) - Coming soon
6. NOTIFICATION-SERVICE (Port 8086) - Coming soon
7. API-GATEWAY (Port 8080) - Coming soon
8. SERVICE-DISCOVERY (Port 8761) - Coming soon

## ⚙️ Environment Variables

Service hỗ trợ override config qua environment variables:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://auth-postgres:5432/auth_db
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: 123456
  SPRING_APP_JWTSECRET: mySecretKey123...
  SPRING_APP_JWTEXPIRATIONMS: 86400000
```

## 🐛 Troubleshooting

### Port đã được sử dụng
```bash
# Windows - Kill process trên port 8081
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Hoặc đổi port trong application.properties
server.port=8082
```

### Database connection failed
- Kiểm tra PostgreSQL đang chạy: `docker ps`
- Kiểm tra logs: `docker-compose logs auth-postgres`
- Restart database: `docker-compose restart auth-postgres`

### JWT Token invalid
- Token expires sau 24h
- Login lại để lấy token mới
- Kiểm tra `jwtSecret` trong application.properties

## 📝 Notes

- Service này độc lập hoàn toàn, không phụ thuộc vào monolith
- Database riêng biệt (auth_db) trên port 5433
- Có thể chạy song song với monolith (port 8080)
- Ready cho service-to-service communication (OpenFeign)

## 🎯 Next Steps

1. ✅ AUTH-SERVICE standalone - DONE
2. Extract CUSTOMER-SERVICE
3. Extract STUDENT-SERVICE
4. Extract PAYMENT-SERVICE
5. Setup API Gateway
6. Implement Service Discovery
7. Add distributed tracing
