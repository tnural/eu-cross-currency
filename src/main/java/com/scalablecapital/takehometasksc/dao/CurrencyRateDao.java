package com.scalablecapital.takehometasksc.dao;

import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurrencyRateDao extends JpaRepository<CurrencyRate, CurrencyRateId> {
    List<CurrencyRate> findAllByCurrencyRateIdFromCurrencyAndCurrencyRateIdToCurrency(String fromCurrency, String toCurrency);
}
