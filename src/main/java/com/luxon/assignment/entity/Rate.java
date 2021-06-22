package com.luxon.assignment.entity;

import com.luxon.assignment.enums.Instrument;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@RequiredArgsConstructor
public class Rate {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private Instrument fromInstrument;

    @Enumerated(value = EnumType.STRING)
    private Instrument toInstrument;

    private Double value;
}
