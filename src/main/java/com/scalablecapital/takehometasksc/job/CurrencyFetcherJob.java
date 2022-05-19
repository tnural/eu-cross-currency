package com.scalablecapital.takehometasksc.job;

import com.scalablecapital.takehometasksc.dao.CurrencyRateDao;
import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import com.scalablecapital.takehometasksc.parser.Envelope;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.scalablecapital.takehometasksc.util.StaticNamingConvention.EXCHANGE_RATE_URI;
import static com.scalablecapital.takehometasksc.util.StaticNamingConvention.ROOT_BASE_CURRENCY_CODE;

@Slf4j
@Configuration
@NoArgsConstructor
@EnableScheduling
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class CurrencyFetcherJob {

    @Autowired
    CurrencyRateDao currencyRateDao;

    @Scheduled(initialDelay=0, fixedRate=24*60*60*1000)
    public void execute() throws JAXBException {
        log.info("CurrencyFetcherJob has started!");
        var restTemplate = new RestTemplate();
        var currencyRateList = new ArrayList<CurrencyRate>();
        var distinctCurrencyList = new ArrayList<String>();
        var decimalFormat = new DecimalFormat("#.#####");
        var reader = new StringReader(restTemplate.getForObject(EXCHANGE_RATE_URI, String.class));
        log.info("Currency conversion data XML from: {} has been retrieved as: {}", EXCHANGE_RATE_URI, reader);
        var root = (Envelope) JAXBContext.newInstance(Envelope.class).createUnmarshaller().unmarshal(reader);
        var timeCube = root.getCube().getCubes().get(0);
        var date = timeCube.getTime();
        var dataCubes = timeCube.getCubes();
        dataCubes.forEach(dataCube -> {
            var currencyRate = CurrencyRate.builder()
                    .currencyRateId(
                            CurrencyRateId.builder()
                                    .fromCurrency(ROOT_BASE_CURRENCY_CODE)
                                    .toCurrency(dataCube.getCurrency())
                                    .date(date)
                                    .build()
                    )
                    .requestedCount(0)
                    .rate(Double.valueOf(dataCube.getRate()))
                    .build();
            var currencyRateReversed = CurrencyRate.builder()
                    .currencyRateId(
                            CurrencyRateId.builder()
                                    .fromCurrency(dataCube.getCurrency())
                                    .toCurrency(ROOT_BASE_CURRENCY_CODE)
                                    .date(date)
                                    .build()
                    )
                    .requestedCount(0)
                    .rate(Double.valueOf(decimalFormat.format(1 / Double.valueOf(dataCube.getRate()))))
                    .build();
            currencyRateList.add(currencyRate);
            currencyRateList.add(currencyRateReversed);
            distinctCurrencyList.add(dataCube.getCurrency());
        });
        log.info("Distinct currency list based on: {} has been found as: {}", ROOT_BASE_CURRENCY_CODE, distinctCurrencyList);
        currencyRateDao.saveAll(currencyRateList);
        log.info("Currency conversion list: {} has been saved!", currencyRateList);
        var crossCurrencyRateList = new ArrayList<CurrencyRate>();
        for (String from : distinctCurrencyList) {
            for (String to : distinctCurrencyList) {
                if (!from.equals(to)) {
                    crossCurrencyRateList.add(generateCrossCurrencyList(from, to, date));
                }
            }
        }
        currencyRateDao.saveAll(crossCurrencyRateList);
        log.info("Cross currency conversion list: {} has been found and saved!", crossCurrencyRateList);
    }

    private CurrencyRate generateCrossCurrencyList(String from, String to, LocalDate date) {
        var curEurFrom = CurrencyRateId
                .builder()
                .fromCurrency(ROOT_BASE_CURRENCY_CODE)
                .toCurrency(from)
                .date(date)
                .build();
        var curEurTo = CurrencyRateId
                .builder()
                .fromCurrency(ROOT_BASE_CURRENCY_CODE)
                .toCurrency(to)
                .date(date)
                .build();
        var baseFromCurrency = currencyRateDao.findById(curEurFrom);
        var baseToCurrency = currencyRateDao.findById(curEurTo);
        var currencyRateId = CurrencyRateId.builder().fromCurrency(from).toCurrency(to).date(date).build();
        var decimalFormat = new DecimalFormat("#.#####");
        var onFlyGeneratedCurrencyRate = CurrencyRate.builder()
                .currencyRateId(currencyRateId)
                .requestedCount(0)
                .rate(Double.valueOf(decimalFormat.format(baseToCurrency.get().getRate() / baseFromCurrency.get().getRate())))
                .build();
        return onFlyGeneratedCurrencyRate;
    }
}

