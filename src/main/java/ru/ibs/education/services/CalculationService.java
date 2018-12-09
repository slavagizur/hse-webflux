package ru.ibs.education.services;

import ru.ibs.education.model.CalculationRequest;
import ru.ibs.education.model.CalculationResponse;

/**
 * Интерфейс для сервиса расчетов.
 *
 * @author vbotalov
 */
public interface CalculationService {

    /**
     * Выполнить расчет.
     *
     * @param request параметры расчета.
     * @return результат выполнения расчета
     */
    CalculationResponse calc(CalculationRequest request);

}
