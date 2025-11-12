# CUSTOMER-SERVICE - iBanking Microservices

Customer management microservice cho hệ thống iBanking.

## 🚀 Thông tin Service

- **Port**: 8082
- **Database**: PostgreSQL (port 5434) - `customer_db`
- **API Documentation**: http://localhost:8082/docs
- **Health Check**: http://localhost:8082/actuator/health

## 📋 Tài khoản Test

| Username | Password  | Balance    | Email                         |
|----------|-----------|------------|-------------------------------|
| admin    | admin123  | 10,000,000 | phanhuyphat021@gmail.com      |
| mary     | mary123   | 15,000,000 | phanhuyphat6@gmail.com        |
| john     | john123   | 20,000,000 | peter.contact.work@gmail.com  |

## 🐳 Chạy bằng Docker

```bash
cd customer-service
docker-compose up --build
```

## 📡 API Endpoints

### 1. Get Current Customer Info
```http
GET http://localhost:8082/api/customers/me
Authorization: Bearer {jwt_token}
```

### 2. Get Balance
```http
GET http://localhost:8082/api/customers/balance
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "balance": "20.000.000 ₫",
  "rawBalance": 20000000
}
```

### 3. Get Customer by ID (Internal API)
```http
GET http://localhost:8082/api/customers/{customerId}
```

### 4. Deduct Balance (Internal API for PAYMENT-SERVICE)
```http
PUT http://localhost:8082/api/customers/{customerId}/deduct
Content-Type: application/json

{
  "amount": 500000
}
```

## 🔐 Security

- JWT Token validation (same secret as AUTH-SERVICE)
- All endpoints except health/docs require valid JWT
- BCrypt password encoding

## 🏗️ APIs by Purpose

**For End Users (via API Gateway):**
- `GET /api/customers/me` - Get profile
- `GET /api/customers/balance` - Check balance

**For Other Services (Internal):**
- `GET /api/customers/{id}` - Get customer data
- `PUT /api/customers/{id}/deduct` - Deduct balance (PAYMENT-SERVICE)

## 🧪 Testing với Postman

1. Login qua AUTH-SERVICE (port 8081) để lấy JWT token
2. Copy token
3. Test CUSTOMER-SERVICE endpoints với token đó

```bash
# 1. Login (AUTH-SERVICE)
POST http://localhost:8081/api/auth/login
{
  "username": "john",
  "password": "john123"
}

# 2. Get balance (CUSTOMER-SERVICE)
GET http://localhost:8082/api/customers/balance
Authorization: Bearer {token_from_step_1}
```

## 📊 Database Schema

**Table: customers**
- id (PK)
- username (unique)
- full_name
- password (BCrypt)
- phone_number (unique)
- email (unique)
- balance

## 🔄 Next Steps

- [x] CUSTOMER-SERVICE standalone - DONE
- [ ] Integrate với PAYMENT-SERVICE via OpenFeign
- [ ] Add circuit breaker (Resilience4j)
- [ ] Add caching for customer data

## 📝 Notes

- Service này KHÔNG có login endpoint (login ở AUTH-SERVICE)
- JWT secret phải GIỐNG AUTH-SERVICE để validate token
- Pessimistic locking cho concurrent balance updates
- Port 8082, database port 5434 (khác với AUTH-SERVICE)
