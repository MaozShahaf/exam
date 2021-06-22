package com.luxon.assignment.entity;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.enums.ExchangeType;
import com.luxon.assignment.enums.Instrument;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@RequiredArgsConstructor
public class ExchangeDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Account account;

    @Enumerated(value = EnumType.STRING)
    private ExchangeType exchangeType;

    @Enumerated(value = EnumType.STRING)
    private Instrument receiverInstrument;

    @Enumerated(value = EnumType.STRING)
    private Instrument giverInstrument;

    @Column
    private String receiverWalletAddress;

    @Column
    private String giverWalletAddress;

    @Column
    private Double receiverInstrumentAmount;

    @Column
    private Date exchangeDate;

    //can be replace with @mapper
    public static ExchangeDetails buildFromRequestDto(Account account, ExchangeRequestDto exchangeRequestDto) {
        return ExchangeDetails.builder()
                .exchangeDate(new Date())
                .account(account)
                .exchangeType(exchangeRequestDto.getExchangeType())
                .receiverInstrument(exchangeRequestDto.getReceiverInstrument())
                .giverInstrument(exchangeRequestDto.getGiverInstrument())
                .receiverInstrumentAmount(exchangeRequestDto.getReceiverInstrumentAmount())
                .giverWalletAddress(exchangeRequestDto.getGiverWalletAddress())
                .receiverWalletAddress(exchangeRequestDto.getReceiverWalletAddress())
                .build();
    }
}
