package ch.vaudoise.apifactory.dto;

import java.math.BigDecimal;

public record ContractSumResponse(
        Long clientId,
        String clientEmail,
        int totalContracts,
        BigDecimal totalActiveContractAmount

) {
}
