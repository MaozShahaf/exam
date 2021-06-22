package com.luxon.assignment.entity;

import com.luxon.assignment.enums.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Balance {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Double qty;

    @OneToOne
    private Wallet wallet;
}
