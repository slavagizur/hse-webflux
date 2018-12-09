package ru.ibs.education;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.ibs.education.handlers.CalculationHandler;
import ru.ibs.education.kafka.Consumer;
import ru.ibs.education.kafka.Producer;
import ru.ibs.education.model.CalculationRequest;
import ru.ibs.education.model.CalculationResponse;
import ru.ibs.education.services.CalculationService;
import ru.ibs.education.services.KafkaCalculationService;

import javax.annotation.Resource;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Класс инициализации и запуска приложения.
 *
 * @author vbotalov
 */
@SpringBootApplication
public class WebfluxApplication {

    private static final String KAFKA_URL = "kafka.url";

    @Resource
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(CalculationHandler calculationHandler) {
        return route(POST("/api/calculator/calc"), calculationHandler::calc);
    }

    @Bean
    @Qualifier("kafka")
    public CalculationService getDatabaseService() {
        return new KafkaCalculationService();
    }

    @Bean
    public Producer producer() {
        return new Producer(environment.getProperty("calc.request"), environment.getProperty(KAFKA_URL));
    }

    @Bean
    public Consumer consumer() {
        Consumer consumer = new Consumer(environment.getProperty("calc.result"), environment.getProperty(KAFKA_URL));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                consumer.receiveInBackground();
            }
        });
        thread.setDaemon(true);
        thread.start();
        return consumer;
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule calculatorModule = new SimpleModule();
        calculatorModule.registerSubtypes(CalculationRequest.class, CalculationResponse.class);
        mapper.registerModule(calculatorModule);
        return mapper;
    }
}
