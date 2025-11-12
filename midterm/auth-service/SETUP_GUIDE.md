# AUTH-SERVICE - Setup Guide

## 📋 Danh Sách Files Cần Copy từ Monolith

### ✅ ĐÃ TẠO:
- [x] pom.xml
- [x] src/main/java/com/soa/auth/entity/Customer.java

### 📝 CẦN COPY VÀ SỬA PACKAGE (từ com.soa.midterm → com.soa.auth):

#### 1. Repository Layer
```
Nguồn: ibanking/src/main/java/com/soa/midterm/repository/
Đích: auth-service/src/main/java/com/soa/auth/repository/

Files:
- CustomerRepository.java
```

#### 2. Security Layer (COPY TOÀN BỘ)
```
Nguồn: ibanking/src/main/java/com/soa/midterm/security/
Đích: auth-service/src/main/java/com/soa/auth/security/

Files:
- WebSecurityConfig.java
- jwt/AuthEntryPointJwt.java
- jwt/AuthTokenFilter.java
- jwt/JwtUtils.java
- request/LoginRequest.java
- response/MessageResponse.java
- response/UserInfoResponse.java
- services/UserDetailsImpl.java
- services/UserDetailsServiceImpl.java
```

#### 3. Controller Layer
```
Nguồn: ibanking/src/main/java/com/soa/midterm/controller/
Đích: auth-service/src/main/java/com/soa/auth/controller/

Files:
- AuthController.java
```

#### 4. Service Layer
```
Nguồn: ibanking/src/main/java/com/soa/midterm/service/
Đích: auth-service/src/main/java/com/soa/auth/service/

Files:
- AuthService.java
- AuthServiceImpl.java
```

#### 5. Config Layer
```
Nguồn: ibanking/src/main/java/com/soa/midterm/config/
Đích: auth-service/src/main/java/com/soa/auth/config/

Files:
- OpenApiConfig.java (optional)
- DataInitialization.java (để seed test data)
```

#### 6. Main Application
```
Tạo mới: auth-service/src/main/java/com/soa/auth/AuthServiceApplication.java
```

#### 7. Application Properties
```
Tạo mới: auth-service/src/main/resources/application.properties
Hoặc copy từ: ibanking/src/main/resources/application.properties
```

---

## 🔧 Các Thay Đổi Cần Làm Sau Khi Copy

### 1. Đổi Package Name trong TẤT CẢ files
```java
// Từ:
package com.soa.midterm.*;

// Thành:
package com.soa.auth.*;
```

### 2. Đổi Import Statements
```java
// Từ:
import com.soa.midterm.entity.Customer;
import com.soa.midterm.repository.*;
import com.soa.midterm.service.*;

// Thành:
import com.soa.auth.entity.Customer;
import com.soa.auth.repository.*;
import com.soa.auth.service.*;
```

### 3. Xóa Dependencies Không Cần Thiết
```java
// Trong Customer.java, xóa:
@OneToMany(mappedBy = "payer", ...)
private List<Transaction> transactions;
```

### 4. Simplify DataInitialization.java
Chỉ giữ phần seed Customers, xóa:
- seedStudents()
- seedSemesters()
- seedStudentSemesterFees()

---

## 📂 Cấu Trúc Cuối Cùng

```
auth-service/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── soa/
│   │   │           └── auth/
│   │   │               ├── AuthServiceApplication.java
│   │   │               ├── config/
│   │   │               │   ├── DataInitialization.java
│   │   │               │   └── OpenApiConfig.java
│   │   │               ├── controller/
│   │   │               │   └── AuthController.java
│   │   │               ├── entity/
│   │   │               │   └── Customer.java
│   │   │               ├── repository/
│   │   │               │   └── CustomerRepository.java
│   │   │               ├── security/
│   │   │               │   ├── WebSecurityConfig.java
│   │   │               │   ├── jwt/
│   │   │               │   │   ├── AuthEntryPointJwt.java
│   │   │               │   │   ├── AuthTokenFilter.java
│   │   │               │   │   └── JwtUtils.java
│   │   │               │   ├── request/
│   │   │               │   │   └── LoginRequest.java
│   │   │               │   ├── response/
│   │   │               │   │   ├── MessageResponse.java
│   │   │               │   │   └── UserInfoResponse.java
│   │   │               │   └── services/
│   │   │               │       ├── UserDetailsImpl.java
│   │   │               │       └── UserDetailsServiceImpl.java
│   │   │               └── service/
│   │   │                   ├── AuthService.java
│   │   │                   └── AuthServiceImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── soa/
│                   └── auth/
└── Dockerfile (tạo sau)
```

---

## ⚙️ application.properties

```properties
spring.application.name=auth-service
server.port=8081

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=sa123456
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
spring.app.jwtSecret=mySecretKey123912738aopsgjnspkmndfsopkvajoirjg94gf2opfng2moknm
spring.app.jwtExpirationMs=86400000

# SpringDoc OpenAPI
springdoc.api-docs.path=/openapi.json
springdoc.swagger-ui.path=/docs

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.com.soa.auth=DEBUG
```

---

## 🚀 Các Bước Thực Hiện

### Bước 1: Copy Files Thủ Công
Sử dụng Windows Explorer hoặc terminal:
```powershell
# Set paths
$source = "d:\TODO-HK120252026\KienTrucHuongDV\midterm\ibanking\src\main\java\com\soa\midterm"
$dest = "d:\TODO-HK120252026\KienTrucHuongDV\midterm\auth-service\src\main\java\com\soa\auth"

# Copy repository
Copy-Item "$source\repository\CustomerRepository.java" "$dest\repository\" -Force

# Copy controller
Copy-Item "$source\controller\AuthController.java" "$dest\controller\" -Force

# Copy service
Copy-Item "$source\service\AuthService.java" "$dest\service\" -Force
Copy-Item "$source\service\AuthServiceImpl.java" "$dest\service\" -Force

# Copy entire security folder
Copy-Item "$source\security" "$dest\" -Recurse -Force

# Copy config files
Copy-Item "$source\config\DataInitialization.java" "$dest\config\" -Force
Copy-Item "$source\config\OpenApiConfig.java" "$dest\config\" -Force
```

### Bước 2: Find & Replace trong IDE
Mở IntelliJ IDEA hoặc VS Code:
1. Ctrl + Shift + H (Find in Path)
2. Find: `com.soa.midterm`
3. Replace: `com.soa.auth`
4. Scope: Directory `auth-service`
5. Replace All

### Bước 3: Clean Up
- Xóa imports không dùng (Ctrl + Alt + O trong IntelliJ)
- Fix compilation errors
- Simplify DataInitialization.java

### Bước 4: Test
```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

Access:
- API: http://localhost:8081
- Swagger: http://localhost:8081/docs

---

## ✅ Checklist

- [ ] Copy tất cả files
- [ ] Replace package names
- [ ] Fix imports
- [ ] Create AuthServiceApplication.java
- [ ] Create application.properties
- [ ] Simplify DataInitialization
- [ ] Remove Transaction references from Customer
- [ ] Build successfully (mvn clean install)
- [ ] Run successfully (mvn spring-boot:run)
- [ ] Test /api/auth/login endpoint
