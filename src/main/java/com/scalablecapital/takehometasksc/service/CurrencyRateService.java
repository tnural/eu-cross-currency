package com.scalablecapital.takehometasksc.service;

import com.scalablecapital.takehometasksc.dao.CurrencyRateDao;
import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CurrencyRateService {
    @Autowired
    CurrencyRateDao currencyRateDao;

    public List<CurrencyRate> getAllCurrencyRates() {
        log.info("Service finding all currency rates.");
        var currencyRateList = this.currencyRateDao.findAll();
        currencyRateList.forEach(currencyRate -> {
            currencyRate.setRequestedCount(currencyRate.getRequestedCount()+1);
        });
        log.info("Service increasing request count for currency rates: {}", currencyRateList);
        this.currencyRateDao.saveAll(currencyRateList);
        return this.currencyRateDao.findAll();
    }

    public Optional<List<CurrencyRate>> getCurrencyRateFromTo(String from, String to) {
        log.info("Service finding from {} to {} currencies.", from, to);
        var currencyRateList = this.currencyRateDao.findAllByCurrencyRateIdFromCurrencyAndCurrencyRateIdToCurrency(from, to);
        currencyRateList.forEach(currencyRate -> {
            currencyRate.setRequestedCount(currencyRate.getRequestedCount()+1);
        });
        log.info("Service increased request count for currency rates: {}", currencyRateList);
        this.currencyRateDao.saveAll(currencyRateList);
        currencyRateList = this.currencyRateDao.findAllByCurrencyRateIdFromCurrencyAndCurrencyRateIdToCurrency(from, to);
        return currencyRateList.size() !=0 ? Optional.of(currencyRateList): Optional.empty();
    }

    public Optional<CurrencyRate> getCurrencyRateById(CurrencyRateId currencyRateId) {
        log.info("Service finding currency by id: {}.", currencyRateId);
        if (currencyRateDao.existsById(currencyRateId)) {
            var currencyRate = currencyRateDao.findById(currencyRateId);
            currencyRate.get().setRequestedCount(currencyRate.get().getRequestedCount() + 1);
            currencyRateDao.save(currencyRate.get());
            var resp = currencyRateDao.findById(currencyRateId);
            log.info("Service has found currency id: {}", resp.get());
            return resp;
        } else {
            log.info("No currency has been found for currency id: {}", currencyRateId);
            return Optional.empty();
        }
    }
}
