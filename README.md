# 🧪 Kafka + Spring Boot

## 🧩 Câu 1: Tạo 2 ứng dụng Spring Boot trao đổi dữ liệu (JSON) với nhau thông qua Kafka

### ✅ Mô tả:
- Tạo hai ứng dụng Spring Boot:
  - `producer-app`: Gửi dữ liệu JSON lên Kafka.
  - `consumer-app`: Nhận dữ liệu JSON từ Kafka và log ra màn hình.
- Cấu hình sử dụng Kafka với Docker (Confluent Platform).
- Gửi các object dạng JSON và deserialize đúng kiểu ở consumer.

### ⚙️ Các thành phần chính:
- **Kafka topic**: `test-topic`
- **Kafka Docker image**: `confluentinc/cp-kafka:7.3.2`
- **Dữ liệu JSON ví dụ**:

```json
{
  "id": 1,
  "message": "Hello from Producer"
}
```

### 📸 Kết quả:
![Message nhận trên spring app consumer](https://github.com/user-attachments/assets/6c937f42-cb72-466b-8f06-36e0e50ccb42)
![Message nhận trên postman khi gọi tới api gửi message của producer](https://github.com/user-attachments/assets/935206eb-cc21-42e5-ad92-67b65c3fc032)


---

## 🧩 Câu 2: Nâng cấp Kafka thành cluster gồm 3 máy

### ✅ Mô tả:
- Cấu hình Kafka cluster với **3 broker** sử dụng Docker Compose.
- Mỗi broker có port riêng: `9092`, `9093`, `9094`
- Dùng Zookeeper làm service điều phối.

### 📁 File `docker-compose.yml`

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.2
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka1:
    image: confluentinc/cp-kafka:7.3.2
    hostname: kafka1
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka1:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3

  kafka2:
    image: confluentinc/cp-kafka:7.3.2
    hostname: kafka2
    ports:
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka2:29093,PLAINTEXT_HOST://localhost:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3

  kafka3:
    image: confluentinc/cp-kafka:7.3.2
    hostname: kafka3
    ports:
      - "9094:9094"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka3:29094,PLAINTEXT_HOST://localhost:9094
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
```

### 📸 Kết quả:

![image](https://github.com/user-attachments/assets/24c2c25b-7f02-4d31-b007-03eeaf642d5d)

---

## 🧩 Câu 3: Kiểm tra trong 3 máy, máy nào làm leader. Dừng máy kafka leader, kiểm tra xem hệ thống có còn hoạt động bình thường?

### ✅ Mô tả:
- Tạo topic `test-topic` với `replication-factor: 3`, `partitions: 3`.
- Kiểm tra leader cho từng partition bằng câu lệnh:

```bash
docker exec -it kafka1 kafka-topics --bootstrap-server kafka1:29092 --describe --topic test-topic
```

- Sau khi biết broker nào là leader của partition nào → dùng lệnh `docker stop` để dừng container leader đó.

### 🧪 Kiểm tra sau khi stop leader:
- Kafka sẽ tự động bầu leader mới cho partition.
- ISR (In-Sync Replicas) giảm xuống còn 2 broker.
- Gửi message vẫn hoạt động bình thường nếu `min.insync.replicas >= 2`.

### 📸 Trước khi dừng leader:

![Screenshot 2025-04-08 161929](https://github.com/user-attachments/assets/df43806c-3d19-4ece-a932-388281cf403f)

### 📸 Sau khi dừng leader:

![Screenshot 2025-04-08 162204](https://github.com/user-attachments/assets/b5d63bca-d8ab-4bde-927f-1f39c6a21581)

---

## ✅ Kết luận:

- Hai ứng dụng Spring Boot đã gửi/nhận JSON thông qua Kafka thành công.
- Kafka cluster hoạt động ổn định với 3 node và khả năng failover tốt.
- Khi một broker (leader) bị dừng, hệ thống vẫn tiếp tục hoạt động ổn định.
