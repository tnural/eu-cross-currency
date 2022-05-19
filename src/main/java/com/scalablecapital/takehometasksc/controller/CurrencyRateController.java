package com.scalablecapital.takehometasksc.controller;

import com.scalablecapital.takehometasksc.dto.CurrencyConversionDto;
import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import com.scalablecapital.takehometasksc.service.CurrencyRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/currencyrate")
public class CurrencyRateController {
    @Autowired
    CurrencyRateService currencyRateService;

    @GetMapping()
    public ResponseEntity<List<CurrencyRate>> getAllCurrencyRates() {
        log.info("Controller getting all currency rates.");
        return new ResponseEntity<>(currencyRateService.getAllCurrencyRates(), HttpStatus.OK);
    }

    @GetMapping(params = {"from", "to"})
    public ResponseEntity<List<CurrencyRate>> getCurrencyRatesFromTo(@RequestParam @NotEmpty @Valid String from, @RequestParam @NotEmpty @NotNull String to) {
        log.info("Controller getting all currencies from: {}, to: {}", from, to);
        var resp = currencyRateService.getCurrencyRateFromTo(from, to);
        if (resp.isEmpty()) {
            log.info("No possible conversion is possible for from: {}, to: {}", from, to);
            return ResponseEntity.unprocessableEntity().build();
        } else {
            return resp
                    .get()
                    .stream()
                    .map(checkout -> ResponseEntity.ok().body(resp.get()))
                    .findFirst()
                    .get();
        }
    }

    @GetMapping(params = {"from", "to", "date"})
    public ResponseEntity<CurrencyRate> getCurrencyRatesFromToDate(@RequestParam @Valid @NotEmpty String from,
                                                                   @RequestParam @Valid @NotEmpty String to,
                                                                   @RequestParam @Valid @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Controller getting currency from: {}, to: {}, date: {}", from, to, date);
        var resp = this.currencyRateService.getCurrencyRateById(
                CurrencyRateId.builder().fromCurrency(from)
                        .toCurrency(to).date(date).build()
        );
        if (resp.isEmpty()) {
            log.info("No possible conversion is possible for from: {}, to: {}, date: {}", from, to, date);
            return ResponseEntity.unprocessableEntity().build();
        } else {
            return resp
                    .stream()
                    .map(checkout -> ResponseEntity.ok().body(resp.get()))
                    .findFirst()
                    .get();
        }
    }

    @GetMapping(path = "/convert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> getConversionAmountOfCurrency(@RequestBody @Valid @NotNull CurrencyConversionDto currencyConversionDto) {
        log.info("Controller getting conversion amount for currency input: {}", currencyConversionDto);
        var currencyRatePersisted = this.currencyRateService.getCurrencyRateById(currencyConversionDto.getCurrency());
        if (currencyRatePersisted.isEmpty()) {
            log.info("No possible conversion is possible for : {}", currencyConversionDto);
            return ResponseEntity.unprocessableEntity().build();
        }
        var convertedAmount = currencyRatePersisted.get().getRate() * currencyConversionDto.getAmount();
        log.info("Controller has found converted amount for input: {}, as: {}", currencyConversionDto, convertedAmount);
        return currencyRatePersisted
                .stream()
                .map(checkout -> ResponseEntity.ok().body(convertedAmount))
                .findFirst()
                .get();
    }

}
