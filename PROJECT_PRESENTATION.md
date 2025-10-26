# Architecture Overview

## 📋 ภาพรวมโปรเจกต์

**E-Commerce Backend System** เป็นระบบ Backend สำหรับแพลตฟอร์ม E-Commerce ที่พัฒนาด้วย **Spring Boot 3.4.5** และ **Java 17** โดยใช้สถาปัตยกรรมแบบ **Module-based Architecture** เพื่อความยืดหยุ่น ขยายได้ง่าย และบำรุงรักษาได้สะดวก

### เทคโนโลยีหลักที่ใช้

- **Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL 8.0
- **Security**: JWT (JSON Web Token)
- **ORM**: Spring Data JPA / Hibernate
- **Email**: Spring Mail (SMTP)
- **Documentation**: Swagger/OpenAPI
- **Container**: Docker & Docker Compose

---

## 🏗️ สถาปัตยกรรมของระบบ (Architecture)

### 1. Module-based Architecture

โปรเจกต์นี้ใช้โครงสร้างแบบ **Module-based** ซึ่งแบ่งระบบออกเป็นโมดูลย่อยๆ ตามฟีเจอร์ (Feature-based modules) โดยแต่ละโมดูลจะมีโครงสร้างภายในที่เป็นมาตรฐานเดียวกัน

```text
src/main/java/sit/int202/ecommerce/
├── modules/
│   ├── auth/          → การยืนยันตัวตนและการลงทะเบียน
│   ├── brand/         → จัดการข้อมูลแบรนด์สินค้า
│   ├── user/          → จัดการข้อมูลผู้ใช้
│   ├── saleitem/      → จัดการสินค้าที่วางขาย
│   ├── order/         → จัดการคำสั่งซื้อ
│   ├── file/          → จัดการไฟล์และการอัปโหลด
│   ├── email/         → ส่งอีเมล (verification, notification)
│   └── security/      → JWT, Filters, Authentication
├── config/            → Configuration classes
└── common/            → Shared utilities, DTOs, Exceptions
```

### 2. โครงสร้างภายในแต่ละโมดูล

แต่ละโมดูลมีโครงสร้างที่เป็นมาตรฐาน ทำให้ง่ายต่อการพัฒนาและบำรุงรักษา:

```text
modules/<module-name>/
├── controller/    → REST API endpoints (รับ HTTP requests)
├── service/       → Business logic และ transaction boundaries
├── repository/    → Database access (Spring Data JPA)
├── model/         → JPA Entities (โครงสร้างตาราง)
├── dto/           → Data Transfer Objects (Request/Response)
├── mapper/        → แปลงระหว่าง Entity ↔ DTO
└── storage/       → (เฉพาะบางโมดูล เช่น file module)
```

### 3. Layer Architecture และ Data Flow

```text
┌─────────────────────────────────────────┐
│  Client (Browser/Mobile App)            │
└──────────────┬──────────────────────────┘
               │ HTTP Request (JSON)
               ↓
┌─────────────────────────────────────────┐
│  Controller Layer                       │
│  - รับ HTTP Request                     │
│  - Validate ข้อมูล (DTO Validation)     │
│  - แปลง Request → DTO                   │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│  Service Layer                          │
│  - Business Logic                       │
│  - Transaction Management               │
│  - เรียกใช้ Repository                  │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│  Repository Layer                       │
│  - Database Operations (CRUD)           │
│  - Query Methods                        │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│  Database (MySQL)                       │
└─────────────────────────────────────────┘
```

**การไหลของข้อมูล (Data Flow)**

1. **Request** → Client ส่ง HTTP Request มาที่ Controller
2. **Validation** → Controller validate ข้อมูล (DTO)
3. **Business Logic** → ส่งต่อไป Service เพื่อประมวลผล
4. **Database Access** → Service เรียกใช้ Repository เพื่อเข้าถึงฐานข้อมูล
5. **Mapping** → แปลง Entity เป็น DTO ก่อนส่งกลับ
6. **Response** → ส่ง JSON Response กลับไปยัง Client

---

## 🔐 ระบบ Security (JWT Authentication)

### การทำงานของ JWT

```
┌──────────┐                              ┌──────────┐
│  Client  │                              │  Server  │
└─────┬────┘                              └────┬─────┘
      │                                        │
      │  1. Login (username/password)          │
      ├───────────────────────────────────────>│
      │                                        │
      │  2. Verify credentials                 │
      │     Generate JWT Token                 │
      │<───────────────────────────────────────┤
      │                                        │
      │  3. Request with JWT in Header         │
      │     Authorization: Bearer <token>      │
      ├───────────────────────────────────────>│
      │                                        │
      │  4. Validate Token                     │
      │     Extract user info                  │
      │     Process request                    │
      │<───────────────────────────────────────┤
      │  5. Response                           │
```

**Security Components**

- `modules/security/jwt/JwtTokenProvider` → สร้างและตรวจสอบ JWT Token
- `modules/security/jwt/JwtAuthenticationFilter` → Filter สำหรับตรวจสอบ Token ในทุก Request
- `modules/auth/` → จัดการการ Login, Register, Verification

---

## 📦 โมดูลหลักของระบบ

### 1. **Auth Module** (การยืนยันตัวตน)

- ลงทะเบียนผู้ใช้ใหม่
- Login / Logout
- ส่งอีเมลยืนยันตัวตน (Email Verification)
- Refresh Token

### 2. **User Module** (จัดการผู้ใช้)

- จัดการข้อมูลผู้ใช้ (Profile)
- อัปเดตข้อมูล, อัปโหลดรูปโปรไฟล์
- จัดการสิทธิ์และบทบาท (Roles)

### 3. **Brand Module** (จัดการแบรนด์)

- CRUD แบรนด์สินค้า
- ค้นหาและกรองแบรนด์

### 4. **Sale Item Module** (จัดการสินค้า)

- CRUD สินค้า
- อัปโหลดรูปภาพสินค้า
- จัดการสต็อกและราคา

### 5. **Order Module** (จัดการคำสั่งซื้อ)

- สร้างคำสั่งซื้อ
- ติดตามสถานะคำสั่งซื้อ
- ประวัติการซื้อ

### 6. **File Module** (จัดการไฟล์)

- อัปโหลดไฟล์ (รูปภาพ, เอกสาร)
- จัดเก็บในระบบไฟล์ (`uploads/`)

### 7. **Email Module** (ส่งอีเมล)

- ส่งอีเมลยืนยันตัวตน
- ส่งการแจ้งเตือนต่างๆ
- ใช้ Thymeleaf Template

---

## 🗄️ ฐานข้อมูล (Database Design)

### ความสัมพันธ์หลัก (Entity Relationships)

- **User** → มีหลาย Order
- **Order** → มีหลาย SaleItem (Many-to-Many)
- **SaleItem** → มี 1 Brand
- **User** → มีหลาย File (รูปโปรไฟล์)
- **SaleItem** -> มีหลาย File (รูปสินค้า)

---

## 🔧 Configuration และ Profiles

### Spring Profiles

โปรเจกต์รองรับ 4 environments:

| Profile | ไฟล์ Config | การใช้งาน |
|---------|------------|----------|
| **local** | `application-local.yml` | พัฒนาบนเครื่องตัวเอง |
| **dev** | `application-dev.yml` | Development Server |
| **uat** | `application-uat.yml` | User Acceptance Testing |
| **prod** | `application-prod.yml` | Production Server |

### Environment Variables สำคัญ

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecommerce
SPRING_DATASOURCE_USERNAME=ecommerce
SPRING_DATASOURCE_PASSWORD=ecommerce123

# JWT
JWT_SECRET=your-secret-key
JWT_ISSUER=https://your-domain.com

# Email
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Application
APP_ORGANIZER_EMAIL=admin@example.com
APP_FRONTEND_URL=http://localhost:3000
```

---

## 🐳 Docker และ Container

### Docker Compose Architecture

```
┌─────────────────────────────────────────┐
│  integrated-network                     │
│                                         │
│  ┌─────────────┐    ┌───────────────┐   │
│  │   MySQL     │◄───│   Backend     │   │
│  │   :3306     │    │   :8080       │   │
│  └─────────────┘    └───────────────┘   │
│        │                     │          │
│    Volume                Volume         │
│  (mysql_data)        (uploads)          │
└─────────────────────────────────────────┘
```

### การรัน

```bash
# Production
docker-compose up -d

# Development
docker-compose -f docker-compose.dev.yml up -d
```

---

## 📊 API Documentation (Swagger)

ระบบมี Swagger/OpenAPI สำหรับดูเอกสาร API และทดสอบ endpoints

**เข้าถึงที่**: `http://localhost:8080/v1/swagger-ui`

### API Endpoints ตัวอย่าง

```
Authentication:
  POST   /api/auth/register
  POST   /api/auth/login
  POST   /api/auth/verify-email

Users:
  GET    /api/users
  GET    /api/users/{id}
  PUT    /api/users/{id}
  DELETE /api/users/{id}

Sale Items:
  GET    /api/saleitems
  POST   /api/saleitems
  PUT    /api/saleitems/{id}
  DELETE /api/saleitems/{id}

Orders:
  GET    /api/orders
  POST   /api/orders
  GET    /api/orders/{id}
```

---

## 🎯 จุดเด่นของสถาปัตยกรรม

### ✅ Scalability (ขยายตัวได้)

- แต่ละโมดูลทำงานอิสระ สามารถแยกเป็น Microservices ได้ในอนาคต
- ใช้ Docker ทำให้ scale ได้ง่าย

### ✅ Maintainability (บำรุงรักษาได้)

- โครงสร้างชัดเจน หาโค้ดได้ง่าย
- แต่ละ layer มีหน้าที่ชัดเจน (Separation of Concerns)

### ✅ Testability (ทดสอบได้)

- แต่ละ layer แยกจากกัน สามารถเขียน unit test ได้ง่าย
- ใช้ Dependency Injection ของ Spring

### ✅ Security (ความปลอดภัย)

- ใช้ JWT สำหรับ authentication
- Password encryption
- Input validation

### ✅ Flexibility (ยืดหยุ่น)

- รองรับหลาย environment (local, dev, uat, prod)
- Configuration แยกออกจากโค้ด (YAML files)

---

## 🚀 วิธีการรันโปรเจกต์

### แบบที่ 1: รันด้วย Maven (Development)

```bash
# Clone repository
git clone <repository-url>
cd Integrated-BE

# รันโปรเจกต์
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### แบบที่ 2: รันด้วย Docker Compose (Production-like)

```bash
# Build และรัน
docker-compose up --build -d

# ดู logs
docker-compose logs -f backend

# หยุด
docker-compose down
```

### แบบที่ 3: Build JAR และรัน

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

---

## 📝 Key Take Away

### จุดสำคัญที่ควรเน้น

1. **Module-based Architecture** → แบ่งโค้ดตาม feature ทำให้จัดการง่าย
2. **Layer Separation** → Controller → Service → Repository (ชัดเจน)
3. **Security with JWT** → ปลอดภัย, Stateless
4. **Docker Support** → Deploy ง่าย, Portable
5. **Multiple Environments** → รองรับ local, dev, uat, prod
6. **API Documentation** → มี Swagger ทำให้ทีมทำงานง่าย
7. **File Upload** → รองรับการอัปโหลดรูปภาพสินค้า
8. **Email Integration** → ส่งอีเมลยืนยันตัวตนได้

### ข้อดีของสถาปัตยกรรมนี้

- ✅ **ง่ายต่อการเพิ่มฟีเจอร์ใหม่** → สร้างโมดูลใหม่ได้เลย
- ✅ **ทีมทำงานร่วมกันได้ดี** → แต่ละคนดูแลคนละโมดูล
- ✅ **Debug ง่าย** → รู้ว่าปัญหาอยู่ที่ layer ไหน
- ✅ **Scale ได้** → ถ้าในอนาคตต้องแยกเป็น Microservices ก็ทำได้

---

## 💡 แนวทางการพัฒนาต่อ

- 🔄 Implement Caching (Redis)
- 📊 Add Monitoring & Logging (ELK Stack)
- 🔔 Implement WebSocket for real-time notifications
- 💳 Payment Gateway Integration
- 📱 API Rate Limiting
- 🌐 Multi-language Support (i18n)

---