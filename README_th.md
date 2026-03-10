# โปรเจกต์จบรายวิชา Object-Oriented Programming (2/68)
[English Version](./README.md)

**ขั้นตอนการ Compile และรันโปรเจกต์**

### 1. สิ่งที่ต้องมี
- Java 17 หรือสูงกว่า
- Maven (หากเครื่องยังไม่มี Maven ให้ทำตามขั้นตอนการติดตั้งจาก [Maven Official Guide](https://maven.apache.org/install.html))

### 3. เปิด Terminal หรือ Command Prompt
```sh
# เข้าไปยังโฟลเดอร์ของโปรเจกต์
cd project

mvn compile exec:java
# สำหรับผู้ใช้งาน MacOS ให้ใช้คำสั่งนี้แทน:
mvn compile exec:exec
```