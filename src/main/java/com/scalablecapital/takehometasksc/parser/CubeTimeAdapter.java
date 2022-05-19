package com.scalablecapital.takehometasksc.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CubeTimeAdapter extends XmlAdapter<String, LocalDate> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String marshal(LocalDate time) throws Exception {
        if (time != null) {
            return new SimpleDateFormat(DATE_FORMAT).format(time);
        }
        return null;
    }

    @Override
    public LocalDate unmarshal(String time) throws Exception {
        if (time != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            return LocalDate.parse(time, formatter);
        }
        return null;
    }

}
