package com.luxon.assignment.entity;

import com.luxon.assignment.enums.Instrument;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@Entity
@RequiredArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(value = EnumType.STRING)
    private Instrument instrument;

    @Column
    private String walletAddress;

    @ManyToOne
    private Account account;
}
