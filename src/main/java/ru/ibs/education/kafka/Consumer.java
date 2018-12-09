package ru.ibs.education.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Считыватель для данных из очереди.
 *
 * @author vbotalov
 */
public class Consumer {

    private static final long WAIT_TIME = 100L;

    private Properties properties;

    private Map<String, String> results;

    private String topic;

    /**
     * Инициализация считывателя.
     *
     * @param topic имя очереди;
     * @param url   URL Kafka.
     */
    public Consumer(String topic, String url) {
        results = new ConcurrentHashMap<>();
        this.topic = topic;
        properties = new Properties();
        properties.put("bootstrap.servers", url);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, getClientId());
    }

    protected String getClientId() {
        return new StringBuilder().append("serviceClient").append(new Date().getTime()).toString();
    }

    public void receiveInBackground() {
        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List<String> topics = new ArrayList();
        topics.add(topic);
        kafkaConsumer.subscribe(topics);

        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.of(WAIT_TIME, ChronoUnit.MILLIS));
                for (ConsumerRecord<String, String> curRecord : records) {
                    results.put(curRecord.key(), curRecord.value());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kafkaConsumer.close();
        }
    }

    public String getResult(String key) throws InterruptedException {
        while (true) {
            String result = results.remove(key);
            if (result != null) {
                return result;
            }
            Thread.sleep(WAIT_TIME);
        }
    }
}
