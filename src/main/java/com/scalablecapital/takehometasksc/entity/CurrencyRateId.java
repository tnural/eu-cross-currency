package com.scalablecapital.takehometasksc.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class CurrencyRateId implements Serializable {
    private String fromCurrency;
    private String toCurrency;
    private LocalDate date;
}
