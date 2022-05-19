package com.scalablecapital.takehometasksc.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scalablecapital.takehometasksc.dao.CurrencyRateDao;
import com.scalablecapital.takehometasksc.dto.CurrencyConversionDto;
import com.scalablecapital.takehometasksc.entity.CurrencyRate;
import com.scalablecapital.takehometasksc.entity.CurrencyRateId;
import com.scalablecapital.takehometasksc.service.CurrencyRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest
@AutoConfigureMockMvc
class CurrencyRateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    CurrencyRateDao currencyRateDao;

    @InjectMocks
    @Resource
    CurrencyRateService currencyRateService;

    @BeforeEach
    void init(){
        ReflectionTestUtils.setField(currencyRateService, "currencyRateDao", currencyRateDao);
    }

    @Test
    void getAllCurrencyRates() throws Exception {
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
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/currencyrate"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].requestedCount", is(1)))
                .andExpect(jsonPath("$[1].requestedCount", is(1)))
                .andExpect(jsonPath("$[2].requestedCount", is(1)));
    }

    @Test
    void getCurrencyRatesFromTo() throws Exception {

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
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/currencyrate?from=USD&to=TRY"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].requestedCount", is(currencyRateListIncreasedCount.get(0).getRequestedCount())))
                .andExpect(jsonPath("$[1].requestedCount", is(currencyRateListIncreasedCount.get(1).getRequestedCount())))
                .andExpect(jsonPath("$[0].rate", is(currencyRateListIncreasedCount.get(0).getRate())))
                .andExpect(jsonPath("$[1].rate", is(currencyRateListIncreasedCount.get(1).getRate())));
    }

    @Test
    void getCurrencyRatesFromToDate() throws Exception {
        var currencyRateId =
                CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now()).build();
        var currencyRate =
                CurrencyRate.builder().currencyRateId(currencyRateId).rate(15.0).requestedCount(0).build();
        var currencyRateRequestCountIncreased = new CurrencyRate(currencyRateId, currencyRate.getRate(), currencyRate.getRequestedCount()+1);

        Mockito.when(currencyRateDao.existsById(currencyRateId)).thenReturn(true);
        Mockito.when(currencyRateDao.saveAll(any())).thenReturn(List.of());
        Mockito.when(currencyRateDao.findById(currencyRateId)).thenReturn(Optional.of(currencyRate))
                .thenReturn(Optional.of(currencyRateRequestCountIncreased));
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/currencyrate?from=USD&to=TRY&date=" + date))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.requestedCount", is(1)))
                .andExpect(jsonPath("$.rate", is(15.0)));
    }

    @Test
    void getConversionAmountOfCurrency() throws Exception {
        var currencyRateId =
                CurrencyRateId.builder().fromCurrency("USD").toCurrency("TRY").date(LocalDate.now()).build();
        var currencyRate =
                CurrencyRate.builder().currencyRateId(currencyRateId).rate(15.0).requestedCount(0).build();
        var currencyRateRequestCountIncreased = new CurrencyRate(currencyRateId, currencyRate.getRate(), currencyRate.getRequestedCount()+1);

        Mockito.when(currencyRateDao.existsById(currencyRateId)).thenReturn(true);
        Mockito.when(currencyRateDao.saveAll(any())).thenReturn(List.of());
        Mockito.when(currencyRateDao.findById(currencyRateId)).thenReturn(Optional.of(currencyRate))
                .thenReturn(Optional.of(currencyRateRequestCountIncreased));
        var currencyConversionDto = CurrencyConversionDto.builder().currency(currencyRateId).amount(55.55).build();
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/currencyrate/convert")
                                .content(asJsonString(currencyConversionDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", is(currencyRateRequestCountIncreased.getRate()*currencyConversionDto.getAmount())));
    }

    public static String asJsonString(final Object obj) {
        try {
            var objMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .registerModule(new Jdk8Module());
            objMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return objMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}