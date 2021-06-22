package com.luxon.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Account {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "account")
    private List<Balance> balances;

    @OneToMany(mappedBy = "account")
    private List<Wallet> wallets;

}
