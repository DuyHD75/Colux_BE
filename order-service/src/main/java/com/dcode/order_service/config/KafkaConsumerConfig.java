package com.dcode.order_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"); // Địa chỉ Kafka broker
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-consumer-group"); // Nhóm consumer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Deserializer cho key
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Deserializer cho value
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Cấu hình offset
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // Thời gian timeout session
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10"); // Số lượng bản ghi tối đa trong mỗi lần poll
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Tắt tự động commit

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // Số lượng luồng xử lý đồng thời
        factory.getContainerProperties().setPollTimeout(3000); // Thời gian chờ poll dữ liệu từ Kafka
        return factory;
    }
}
