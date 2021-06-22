package com.luxon.assignment.enums;

import lombok.SneakyThrows;

import java.util.Arrays;

public enum Instrument {
    BTC, ETH, BCH, LTC, USD, EUR, GBP;

    public Boolean isCryptoInstrument() {
        return Arrays.asList(BTC, ETH, BCH, LTC).contains(this);
    }
    public Boolean isFiatInstrument() {
        return !isCryptoInstrument();
    }
    @SneakyThrows
    public Boolean isCurrencyPair(){
        throw new Exception("Not implemented yet... /=");
    }
}
