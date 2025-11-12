# User Service Testing Guide

## Prerequisites

1. **Auth Service** must be running on port 8081
2. **User Service** must be running on port 8082
3. **PostgreSQL** database `user_db` must be available on port 5434
4. Have a **JWT token** from Auth Service login

## Getting Started

### 1. Start Auth Service (if not running)
```bash
cd auth-service
mvn spring-boot:run
```

### 2. Start User Service
```bash
cd user-service
mvn spring-boot:run
```

### 3. Login to Auth Service
Use Postman or curl to login:

```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "test@example.com",
    "role": "USER"
  }
}
```

Copy the **token** value for use in User Service requests.

## Testing User Service Endpoints

### Import Postman Collection

1. Open Postman
2. Import `postman/User-Service.postman_collection.json`
3. Set environment variable:
   - `base_url`: `http://localhost:8082`
   - `jwt_token`: (paste your JWT token from login)

---

## API Endpoints

### 👤 User Profile

#### 1. Get Current User Profile
```http
GET /api/users/me
Authorization: Bearer {jwt_token}
```

**First time calling this endpoint:**
- If user doesn't exist in user-service database, you'll get a 401 error
- This is because user-service needs to sync the user first

**Workaround:** Create user manually using UserService.createUser() or register a new user in auth-service

#### 2. Update Profile
```http
PUT /api/users/me
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "fullName": "John Doe Updated",
  "gender": "MALE",
  "dateOfBirth": "1995-05-15",
  "currentWeight": 75.5,
  "currentHeight": 175.0,
  "activityLevel": "MODERATELY_ACTIVE"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "email": "test@example.com",
    "fullName": "John Doe Updated",
    "gender": "MALE",
    "dateOfBirth": "1995-05-15",
    "currentWeight": 75.5,
    "currentHeight": 175.0,
    "currentBmi": 24.65,  // Auto-calculated!
    "activityLevel": "MODERATELY_ACTIVE",
    "role": "USER",
    "isActive": true
  }
}
```

---

### 📊 Health Metrics

#### 3. Log Health Metric
```http
POST /api/users/1/metrics
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "recordedDate": "2024-11-12",
  "weight": 75.5,
  "height": 175.0,
  "bodyFatPercentage": 18.5,
  "muscleMass": 32.5,
  "waistCircumference": 82.0,
  "chestCircumference": 95.0,
  "hipCircumference": 92.0,
  "notes": "After morning workout"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Health metric logged successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "recordedDate": "2024-11-12",
    "weight": 75.5,
    "height": 175.0,
    "bmi": 24.65,  // Auto-calculated!
    "bodyFatPercentage": 18.5,
    "muscleMass": 32.5,
    "waistCircumference": 82.0,
    "chestCircumference": 95.0,
    "hipCircumference": 92.0,
    "notes": "After morning workout",
    "createdAt": "2024-11-12T10:30:00"
  }
}
```

#### 4. Get All Metrics
```http
GET /api/users/1/metrics
Authorization: Bearer {jwt_token}
```

#### 5. Get Latest Metric
```http
GET /api/users/1/metrics/latest
Authorization: Bearer {jwt_token}
```

#### 6. Get Metrics by Date Range
```http
GET /api/users/1/metrics?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer {jwt_token}
```

---

### 🎯 Goals Management

#### 7. Create Goal
```http
POST /api/users/1/goals
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "goalType": "WEIGHT_LOSS",
  "title": "Lose 10 kg by summer",
  "description": "Target weight of 70kg through balanced diet and exercise",
  "targetValue": 70.0,
  "currentValue": 80.0,
  "unit": "kg",
  "startDate": "2024-11-12",
  "targetDate": "2025-06-01"
}
```

**Goal Types:**
- `WEIGHT_LOSS`
- `WEIGHT_GAIN`
- `MUSCLE_GAIN`
- `FITNESS_LEVEL`
- `CALORIE_INTAKE`
- `WORKOUT_FREQUENCY`
- `CUSTOM`

**Response:**
```json
{
  "success": true,
  "message": "Goal created successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "goalType": "WEIGHT_LOSS",
    "title": "Lose 10 kg by summer",
    "description": "Target weight of 70kg through balanced diet and exercise",
    "targetValue": 70.0,
    "currentValue": 80.0,
    "unit": "kg",
    "startDate": "2024-11-12",
    "targetDate": "2025-06-01",
    "status": "IN_PROGRESS",  // Auto-calculated!
    "progress": 0.0,  // (targetValue - currentValue) / (targetValue - startValue) * 100
    "createdAt": "2024-11-12T10:35:00"
  }
}
```

#### 8. Get All Goals
```http
GET /api/users/1/goals
Authorization: Bearer {jwt_token}
```

#### 9. Get Goals by Status
```http
GET /api/users/1/goals?status=IN_PROGRESS
Authorization: Bearer {jwt_token}
```

**Status values:**
- `NOT_STARTED`
- `IN_PROGRESS`
- `COMPLETED`
- `OVERDUE`
- `CANCELLED`

#### 10. Update Goal Progress
```http
PATCH /api/users/1/goals/1/progress
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "currentValue": 72.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Progress updated successfully",
  "data": {
    "id": 1,
    "currentValue": 72.0,
    "progress": 80.0,  // 80% progress towards goal!
    "status": "IN_PROGRESS"
  }
}
```

#### 11. Cancel Goal
```http
POST /api/users/1/goals/1/cancel
Authorization: Bearer {jwt_token}
```

#### 12. Delete Goal
```http
DELETE /api/users/1/goals/1
Authorization: Bearer {jwt_token}
```

---

## Auto-Calculated Fields

### BMI (Body Mass Index)
- **Formula:** `weight / (height/100)^2`
- **Calculated in:** User entity, HealthMetric entity
- **Triggers:** `@PrePersist`, `@PreUpdate`

### Goal Progress
- **Formula:** `(currentValue / targetValue) * 100`
- **Calculated in:** Goal entity
- **Trigger:** `getProgress()` method

### Goal Status
- **Logic:**
  - `NOT_STARTED`: currentValue = 0 or startDate > today
  - `IN_PROGRESS`: currentValue > 0 and < targetValue
  - `COMPLETED`: progress >= 100%
  - `OVERDUE`: targetDate passed and not completed
  - `CANCELLED`: manually cancelled
- **Trigger:** `updateStatus()` in `@PrePersist`, `@PreUpdate`

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL,
  gender VARCHAR(10) CHECK (gender IN ('MALE','FEMALE','OTHER')),
  date_of_birth DATE,
  current_weight FLOAT,
  current_height FLOAT,
  current_bmi FLOAT,
  activity_level VARCHAR(20),
  role VARCHAR(20) CHECK (role IN ('USER','ADMIN')),
  is_active BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Health Metrics Table
```sql
CREATE TABLE health_metrics (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  recorded_date DATE NOT NULL,
  weight FLOAT NOT NULL,
  height FLOAT,
  bmi FLOAT,
  body_fat_percentage FLOAT,
  muscle_mass FLOAT,
  waist_circumference FLOAT,
  chest_circumference FLOAT,
  hip_circumference FLOAT,
  notes VARCHAR(255),
  created_at TIMESTAMP
);
```

### Goals Table
```sql
CREATE TABLE goals (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  goal_type VARCHAR(30) NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(1000),
  target_value FLOAT,
  current_value FLOAT,
  unit VARCHAR(20),
  start_date DATE NOT NULL,
  target_date DATE,
  status VARCHAR(20) NOT NULL,
  completed_date DATE,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

---

## Error Handling

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Authentication failed: JWT signature does not match",
  "data": null
}
```

**Causes:**
- Invalid JWT token
- Expired token
- User not found in user-service database

**Solution:**
- Re-login to auth-service to get fresh token
- Ensure user exists in user-service (call createUser first)

### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied: Goal does not belong to user",
  "data": null
}
```

**Causes:**
- Trying to access another user's data

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed: Weight must be positive",
  "data": null
}
```

**Causes:**
- Invalid request body
- Missing required fields
- Validation constraints violated

---

## Next Steps

1. ✅ **Phase 3 Complete:** User Service with profile, metrics, goals
2. ⏳ **Phase 4:** Fitness Service (workout tracking, exercises)
3. ⏳ **Phase 5:** Meal Service (nutrition, meal logging)
4. ⏳ **Phase 6:** API Gateway (centralized routing, authentication)

---

## Troubleshooting

### User Service won't start
- Check if PostgreSQL is running: `docker ps`
- Check if user_db exists on port 5434
- Check application.properties for correct database config

### JWT validation fails
- Ensure `jwt.secret` is **identical** in auth-service and user-service
- Check token expiration (default: 24 hours)
- Verify Authorization header format: `Bearer <token>`

### User not found error
- User must exist in **both** auth_db and user_db
- First time: call auth-service register, then sync to user-service
- Or manually insert user in user_db with same email

---

**Date:** November 12, 2024  
**Version:** 1.0.0  
**Status:** ✅ User Service Running on Port 8082
