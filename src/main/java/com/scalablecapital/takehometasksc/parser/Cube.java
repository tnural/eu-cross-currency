package com.scalablecapital.takehometasksc.parser;

import lombok.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.List;

@XmlType(namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class Cube {
    @XmlAttribute
    @XmlJavaTypeAdapter(CubeTimeAdapter.class)
    private LocalDate time;
    @XmlAttribute
    private String currency;
    @XmlAttribute
    private String rate;
    @XmlElement(name = "Cube")
    private List<Cube> cubes;
}
