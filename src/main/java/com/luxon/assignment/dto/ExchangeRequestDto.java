package com.luxon.assignment.dto;

import com.luxon.assignment.enums.ExchangeType;
import com.luxon.assignment.enums.Instrument;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExchangeRequestDto {

    @NotNull
    private Integer accountId;

    @NotNull
    private ExchangeType exchangeType;

    private Instrument receiverInstrument;

    private Instrument giverInstrument;

    private String receiverWalletAddress;

    private String giverWalletAddress;

    @NotNull
    private Double receiverInstrumentAmount;

}
