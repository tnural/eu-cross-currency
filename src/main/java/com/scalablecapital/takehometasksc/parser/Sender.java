package com.scalablecapital.takehometasksc.parser;

import lombok.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
@ToString
public class Sender {
    @XmlElement
    private String name;
}
