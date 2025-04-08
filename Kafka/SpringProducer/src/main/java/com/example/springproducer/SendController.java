package com.example.springproducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/send")
public class SendController {

    private final KafkaProducer<String, String> producer;

    @Value("${topic.name}")
    private String topic;

    public SendController(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> send(@RequestBody String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record);
        return ResponseEntity.ok("Sent: " + message);
    }
}
