package com.scalablecapital.takehometasksc.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "currency_rate")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class CurrencyRate {
    @EmbeddedId
    private CurrencyRateId currencyRateId;
    private Double rate;
    private Integer requestedCount;
}
