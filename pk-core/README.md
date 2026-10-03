# pk-core – Backend Jewelry Commerce (Khóa luận tốt nghiệp)

## Cấu trúc

| Module | Vai trò |
| --- 						| --- 																						|
| `pk-common` 				| Lớp nền dùng chung 																		|
| `pk-model` 				| Thông tin model 																			|
| `pk-identity` 			| Đăng nhập/đăng ký/refresh token, cấu hình bảo mật (JWT RS256) 							|
| `pk-business` 			| Nghiệp vụ dự án 																			|
| `pk-api` 					| Api kết nối																				|
| `pk-service` 				| Module chạy app 																			|
| `pk-unit-test` 			| Unit test và integration test 															|

## 1. Yêu cầu

- JDK 17+,
- Maven
- PostgreSQL

## 2. Chạy PostgreSQL

- Tạo database kltn_ecommerce
- Chạy file tạo bảng sql: 11-tao-bang.sql
- Chạy file tạo dữ liệu sql: 12-du-lieu-mau.sql

## 3. Chạy ứng dụng (dev, có dữ liệu demo)

- Add Maven project pk-core vào idea
- maven: clean install
- run application run -Dspring-boot.run.profiles=dev

-Kết nối cổng localhost:8082
- Swagger UI: http://localhost:8082/swagger-ui.html

