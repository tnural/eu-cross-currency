package com.scalablecapital.takehometasksc.service;

import com.scalablecapital.takehometasksc.dao.CurrencyRateDao;
import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest
class CurrencyRateServiceTest {
    @Mock
    CurrencyRateDao currencyRateDao;

    @InjectMocks
    CurrencyRateService currencyRateService;

    @Test
    void getAllCurrencyRates() {
        var currencyRateList = List.of(
                CurrencyRate.builder().rate(15.0).requestedCount(0).currencyRateId(
                        CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now()).build()
                ).build(),
                CurrencyRate.builder().rate(1.05).requestedCount(0).currencyRateId(
                        CurrencyRateId.builder().fromCurrency("EUR").toCurrency("USD").date(LocalDate.now()).build()
                ).build(),
                CurrencyRate.builder().rate(14.0).requestedCount(0).currencyRateId(
                        CurrencyRateId.builder().fromCurrency("EUR").toCurrency("TRY").date(LocalDate.now()).build()
                ).build()
        );
        var currencyRateListIncreasedCount = currencyRateList.stream()
                .map(curRate -> new CurrencyRate(
                            curRate.getCurrencyRateId(),
                            curRate.getRate(),
                            curRate.getRequestedCount() + 1))
                .collect(Collectors.toList());
        Mockito.when(currencyRateDao.findAll())
                .thenReturn(currencyRateList)
                .thenReturn(currencyRateListIncreasedCount);
        Mockito.when(currencyRateDao.saveAll(any())).thenReturn(List.of());
        var expected = currencyRateService.getAllCurrencyRates();
        assertThat(expected.size()).isEqualTo(3);
        assertThat(expected.get(0).getRequestedCount()).isEqualTo(1);
        assertThat(expected.get(1).getRequestedCount()).isEqualTo(1);
        assertThat(expected.get(2).getRequestedCount()).isEqualTo(1);
    }

    @Test
    void getCurrencyRateFromTo() {
        var currencyRateList = List.of(
                CurrencyRate.builder().rate(15.0).requestedCount(0).currencyRateId(
                        CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now()).build()
                ).build(),
                CurrencyRate.builder().rate(15.2).requestedCount(0).currencyRateId(
                        CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now().minus(1, ChronoUnit.DAYS)).build()
                ).build()
        );

        var currencyRateListIncreasedCount = currencyRateList.stream()
                .map(curRate -> new CurrencyRate(
                        curRate.getCurrencyRateId(),
                        curRate.getRate(),
                        curRate.getRequestedCount() + 1))
                .collect(Collectors.toList());

        Mockito.when(currencyRateDao.findAllByCurrencyRateIdFromCurrencyAndCurrencyRateIdToCurrency(any(), any()))
                .thenReturn(currencyRateList)
                .thenReturn(currencyRateListIncreasedCount);
        Mockito.when(currencyRateDao.saveAll(any())).thenReturn(List.of());
        var expected = currencyRateService.getCurrencyRateFromTo("EUR","USD");
        assertThat(expected).isNotEmpty();
        assertThat(expected.get().size()).isEqualTo(2);
        assertThat(expected.get().get(0).getRequestedCount()).isEqualTo(1);
        assertThat(expected.get().get(0).getRate()).isEqualTo(currencyRateListIncreasedCount.get(0).getRate());
        assertThat(expected.get().get(1).getRequestedCount()).isEqualTo(1);
        assertThat(expected.get().get(1).getRate()).isEqualTo(currencyRateListIncreasedCount.get(1).getRate());
    }

    @Test
    void getCurrencyRateById() {
        var currencyRateId =
                CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now()).build();
        var currencyRate =
                CurrencyRate.builder().currencyRateId(currencyRateId).rate(15.0).requestedCount(0).build();
        var currencyRateRequestCountIncreased = new CurrencyRate(currencyRateId, currencyRate.getRate(), currencyRate.getRequestedCount()+1);

        Mockito.when(currencyRateDao.existsById(currencyRateId)).thenReturn(true);
        Mockito.when(currencyRateDao.saveAll(any())).thenReturn(List.of());
        Mockito.when(currencyRateDao.findById(currencyRateId)).thenReturn(Optional.of(currencyRate))
                .thenReturn(Optional.of(currencyRateRequestCountIncreased));

        var expected = currencyRateService.getCurrencyRateById(currencyRateId);
        assertThat(expected).isNotEmpty();
        assertThat(expected.get().getRequestedCount()).isEqualTo(currencyRateRequestCountIncreased.getRequestedCount());
    }
}