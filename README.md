# Project Java (OOP) Term 2

**คำแนะนำวิธีการรันตัวเกม (How to Run)**

### 1. ติดตั้ง Maven
- สามารถดูวิธีการติดตั้งได้ที่: [Maven Official Guide](https://maven.apache.org/install.html)

### 2. เปิดใช้งานผ่าน Terminal
เปิด Terminal หรือ Command Prompt ขึ้นมา

1. **เข้าสู่โฟลเดอร์โปรเจกต์**  
   ใช้คำสั่ง `cd` ตามด้วย path ของโปรเจกต์ของคุณ (เช่น หากคุณเก็บไว้ในโฟลเดอร์ `project` ให้เข้าโฟลเดอร์นั้นใน Terminal):
   ```bash
   cd project
   ```

2. **รันตัวเกม**  
   ใช้คำสั่งด้านล่างนี้เพื่อสั่งให้ Maven คอมไพล์และเปิดเกมขึ้นมาใช้งานได้เลย:
   ```bash
   mvn compile exec:java
   ```