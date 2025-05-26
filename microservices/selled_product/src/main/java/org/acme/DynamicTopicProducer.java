package org.acme;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class DynamicTopicProducer {

    private static KafkaProducer<String, String> producer;

    public static void initialize(String kafkaBootstrapServers) {
        if (producer == null) {
            Properties props = new Properties();
            props.put("bootstrap.servers", kafkaBootstrapServers);
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", StringSerializer.class.getName());

            producer = new KafkaProducer<>(props);
            System.out.println("Kafka producer initialized.");
        }
    }

    private static class DemoProducerCallback implements Callback{
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e){
            if(e != null){
                e.printStackTrace();
            }
        }
        
    }

    public static void send(String topic, String key, String message) {
        if (producer == null) {
            throw new IllegalStateException("Kafka producer not initialized. Call initialize() first.");
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);

        producer.send(record, new DemoProducerCallback());
    }

    public static void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
            System.out.println("Kafka producer closed.");
        }
    }
}
