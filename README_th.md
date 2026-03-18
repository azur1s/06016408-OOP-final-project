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

# ใบอนุญาต
โปรเจกต์นี้เผยแพร่ภายใต้สัญญาอนุญาต GPL-3.0 ดูรายละเอียดเพิ่มเติมได้ที่ไฟล์ [LICENSE](./LICENSE)
รูปภาพที่ใช้ในโปรเจกต์นี้เป็นผลงานวาดด้วยมือโดยผู้จัดทำ และเผยแพร่ภายใต้สัญญาอนุญาต CC BY-NC-SA 4.0 ดูข้อมูลเพิ่มเติมได้ที่ [Creative Commons License](https://creativecommons.org/licenses/by-nc-sa/4.0/)