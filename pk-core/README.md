# pk-core – Backend Jewelry Commerce (Khóa luận tốt nghiệp)

Multi-module Maven, Spring Boot 3.2.0, Java 17, PostgreSQL + Flyway. Tài liệu thiết kế nằm ở `../../document`.

## Cấu trúc

| Module | Vai trò |
| --- | --- |
| `pk-common` | BaseEntity, exception, ApiError, PageResponse, SlugUtil |
| `pk-model` | Entity + Repository (catalog) |
| `pk-identity` | Auth (register/login/refresh/logout), user-role, JWT RS256, SecurityConfig, CORS |
| `pk-business` | Service nghiệp vụ, `business.config` (OpenAPI, GlobalExceptionHandler) |
| `pk-api` | `api.rest` (*Rest) |
| `pk-service` | Ứng dụng chạy được (`PkServiceApplication`), `application*.yml`, Flyway migration, DataSeeder |
| `pk-unit-test` | Unit test + integration test |

## 1. Yêu cầu

JDK 17+, Maven 3.9+, Docker (cho PostgreSQL) hoặc PostgreSQL cài sẵn.

## 2. Chạy PostgreSQL

```bash
docker run --name pk-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=pk_core -p 5432:5432 -d postgres:16
```

## 3. Chạy ứng dụng (dev, có dữ liệu demo)

```bash
mvn -DskipTests install
mvn -pl pk-service spring-boot:run -Dspring-boot.run.profiles=dev
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Admin demo: `admin@pk.local` / `Admin@12345` (chỉ profile `dev`).
- Lần chạy đầu, Flyway tạo schema (`V1__init.sql`) rồi Mirage truy vấn theo file SQL. Nếu báo lỗi cột/bảng: đọc kỹ thông báo, sửa entity hoặc thêm migration mới, **không sửa file migration đã chạy**.

## 4. Chạy test

```bash
mvn test
```

Test chạy trên H2 (không cần Docker). Xem `../../document/06-ke-hoach-kiem-thu.md`.

## 5. Kiểm tra với PostgreSQL thật (nên làm trước khi demo)

Chạy app như mục 3 với profile `dev`. Nếu khởi động thành công tức Flyway đã áp dụng migration.

## 6. Sinh khoá JWT cho môi trường deploy

```bash
openssl genrsa -out private.pem 2048
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_pkcs8.pem -nocrypt
openssl rsa -in private.pem -pubout -out public.pem
```

Đặt nội dung `private_pkcs8.pem` vào `JWT_PRIVATE_KEY`, `public.pem` vào `JWT_PUBLIC_KEY` (biến môi trường của nơi deploy). Không commit hai file này.

## 7. Biến môi trường

`DB_URL` (dạng `jdbc:postgresql://host:5432/db`), `DB_USERNAME`, `DB_PASSWORD`, `JWT_PRIVATE_KEY`, `JWT_PUBLIC_KEY`,
`CORS_ALLOWED_ORIGINS`, `SEED_ENABLED` (mặc định false), `PORT`. Chi tiết: `../../document/03-kien-truc-he-thong.md`.

## 8. Đóng gói

```bash
mvn -DskipTests package
java -jar pk-service/target/pk-service-0.0.1-SNAPSHOT-exec.jar
```

## 9. Thêm migration mới

Lấy bản nháp V3..V10 trong `../../document/database/`, đọc lại US tương ứng, copy vào
`pk-service/src/main/resources/db/migration/`, viết entity khớp, chạy `mvn test` rồi chạy app với PostgreSQL thật.

## 10. Ghi chú về các file cũ

Ba lớp rỗng `JwtAuthenticationFilter`, `JwtDecoderImpl`, `CustomOAuth2UserService` trong `pk-identity` không còn được dùng
(Spring Security tự lo việc đọc/kiểm tra JWT); có thể xóa. Project `../jewelry-backend` là bản cũ, đã bị thay bằng pk-core.
