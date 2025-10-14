package ch.vaudoise.apifactory.dto;

import java.math.BigDecimal;
import java.util.Date;

public record ContractRequest(
        Date startDate,
        Date endDate,
        BigDecimal costAmount
) {
}
