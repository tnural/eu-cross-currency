package com.scalablecapital.takehometasksc.dto;

import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class CurrencyConversionDto {
    @NotNull
    private CurrencyRateId currency;
    @DecimalMin(value = "0.01", message = "Minimum amount must be 0,01")
    @DecimalMax(value = "1000000.0", message = "Maximum amount can be 1.000.000")
    @NotNull
    private Double amount;
}
