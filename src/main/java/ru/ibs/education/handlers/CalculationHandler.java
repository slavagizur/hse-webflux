package ru.ibs.education.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.ibs.education.model.CalculationRequest;
import ru.ibs.education.model.CalculationResponse;
import ru.ibs.education.services.CalculationService;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Обработчик для REST вызова расчета.
 *
 * @author vbotalov
 */
@Component
public class CalculationHandler {

    @Autowired
    @Qualifier("kafka")
    private CalculationService calculationService;

    public Mono<ServerResponse> calc(ServerRequest serverRequest) {
        Mono<CalculationRequest> calculationMono = serverRequest.bodyToMono(CalculationRequest.class);
        return calculationMono.flatMap(officer ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(Mono.justOrEmpty(calculationService.calc(officer)), CalculationResponse.class));
    }
}
