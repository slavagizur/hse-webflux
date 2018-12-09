package ru.ibs.education.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibs.education.kafka.Consumer;
import ru.ibs.education.kafka.Producer;
import ru.ibs.education.model.CalculationRequest;
import ru.ibs.education.model.CalculationResponse;

/**
 * Реализация сервиса расчетов с помощью агентов расчетов, работающих через Kafka.
 *
 * @author vbotalov
 */
public class KafkaCalculationService implements CalculationService {

    private static final String UNKNOWN_EXECUTOR = "UNKNOWN";

    @Autowired
    private Producer producer;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Consumer consumer;

    @Override
    public CalculationResponse calc(CalculationRequest request) {
        CalculationResponse response;
        try {
            String message = mapper.writeValueAsString(request);
            String key = Long.toHexString(System.nanoTime());
            producer.send(message, key);
            String result = consumer.getResult(key);
            response = mapper.readValue(result, CalculationResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            response = new CalculationResponse(UNKNOWN_EXECUTOR);
            response.setSuccess(false);
            StringBuilder message = new StringBuilder();
            fillMessageRecursive(message, e);
            response.setMessage(message.toString());
        }

        return response;
    }

    private void fillMessageRecursive(StringBuilder message, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        if (message.length() > 0) {
            message.append(" вызвано ");
        }
        message.append(throwable.getMessage());
        fillMessageRecursive(message, throwable.getCause());
    }
}
