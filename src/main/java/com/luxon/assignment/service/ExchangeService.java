package com.luxon.assignment.service;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.entity.*;
import com.luxon.assignment.enums.Instrument;
import com.luxon.assignment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;
    private final BalanceRepository balanceRepository;
    private final RateRepository rateRepository;
    private final ExchangeDetailsRepository exchangeDetailsRepository;

    public ResponseEntity<?> execute(ExchangeRequestDto exchangeRequestDto) {
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).build();
        // 1) Check account exists
        if (accountRepository.existsById(exchangeRequestDto.getAccountId())) {
            Account exchangeAccount = accountRepository.getOne(exchangeRequestDto.getAccountId());

            switch (exchangeRequestDto.getExchangeType()) {
                case BUY:
                    if(exchangeRequestDto.getReceiverInstrument() != null && exchangeRequestDto.getGiverInstrument() != null)
                        responseEntity = buySell(exchangeAccount, exchangeRequestDto.getReceiverInstrument(), exchangeRequestDto.getGiverInstrument(), exchangeRequestDto.getReceiverInstrumentAmount(), exchangeRequestDto, responseEntity);
                    return responseEntity;
                case SELL:
                    if (exchangeRequestDto.getReceiverInstrument() != null && exchangeRequestDto.getGiverInstrument() != null)
                        responseEntity = buySell(exchangeAccount, exchangeRequestDto.getGiverInstrument(), exchangeRequestDto.getReceiverInstrument(), exchangeRequestDto.getReceiverInstrumentAmount(), exchangeRequestDto, responseEntity);
                    return responseEntity;
                case SEND:
                    if (!exchangeRequestDto.getReceiverWalletAddress().isEmpty() && !exchangeRequestDto.getGiverWalletAddress().isEmpty())
                        responseEntity = send(exchangeAccount, exchangeRequestDto.getReceiverWalletAddress(), exchangeRequestDto.getGiverWalletAddress(), exchangeRequestDto.getReceiverInstrumentAmount(), exchangeRequestDto, responseEntity);
                    return responseEntity;
            }
        }


        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private ResponseEntity<?> buySell(Account exchangeAccount, Instrument receiverInstrument, Instrument giverInstrument, Double receiverInstrumentAmount,
                                  ExchangeRequestDto exchangeRequestDto, ResponseEntity responseEntity) {
        //check source wallet exists
        Optional<Balance> giverBalance = getBalance(exchangeAccount, giverInstrument);

        //check target wallet exists
        Optional<Balance> receiverBalance = getBalance(exchangeAccount, receiverInstrument);

        //get rate for target instrument
        Rate exampleRate = Rate.builder()
                .fromInstrument(giverInstrument)
                .toInstrument(receiverInstrument).build();

        Optional<Rate> rate = rateRepository.findOne(Example.of(exampleRate));
        if (rate.isEmpty()) {
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // No rate for giver instrument
        } else {
            responseEntity = makeTransaction(exchangeAccount, receiverInstrumentAmount, exchangeRequestDto,
                    responseEntity, receiverBalance, giverBalance, receiverInstrumentAmount * rate.get().getValue());
        }

        return responseEntity;
    }

    private ResponseEntity<?> send(Account exchangeAccount, String receiverWalletAddress, String giverWalletAddress, Double receiverInstrumentAmount,
                                   ExchangeRequestDto exchangeRequestDto, ResponseEntity responseEntity) {
        //check source wallet exists
        Wallet receiverExampleWallet = getWalletByAddress(exchangeAccount, receiverWalletAddress);

        Optional<Wallet> receiverWallet = walletRepository.findOne(Example.of(
                receiverExampleWallet
        ));

        Optional<Balance> receiverBalance;

        if (!receiverWallet.isPresent()) {
            receiverWallet = Optional.of(walletRepository.save(receiverExampleWallet));
            receiverBalance = Optional.of(balanceRepository.save(Balance.builder().wallet(receiverWallet.get()).qty(0.0).build()));
        } else {
            receiverBalance = balanceRepository.findOne(Example.of(Balance.builder().wallet(receiverWallet.get()).build()));
        }

        //check target wallet exists
        Wallet giverExampleWallet = getWalletByAddress(exchangeAccount, giverWalletAddress);

        Optional<Wallet> giverWallet = walletRepository.findOne(Example.of(
                giverExampleWallet
        ));

        Optional<Balance> giverBalance;

        if (!giverWallet.isPresent()) {
            giverWallet = Optional.of(walletRepository.save(giverExampleWallet));
            giverBalance = Optional.of(balanceRepository.save(Balance.builder().wallet(giverWallet.get()).qty(0.0).build()));
        } else {
            giverBalance = balanceRepository.findOne(Example.of(Balance.builder().wallet(giverWallet.get()).build()));
        }

        //if the wallets are in the same instrument then no need to do any rate calculations
        if(giverWallet.get().getInstrument().equals(receiverWallet.get().getInstrument())){
            //check enough balance of rated instrument
            responseEntity = makeTransaction(exchangeAccount, receiverInstrumentAmount, exchangeRequestDto,
                    responseEntity, receiverBalance, giverBalance, receiverInstrumentAmount);
        }
        else {
            //get rate for target instrument
            Rate exampleRate = Rate.builder()
                    .fromInstrument(giverWallet.get().getInstrument())
                    .toInstrument(receiverWallet.get().getInstrument()).build();

            Optional<Rate> rate = rateRepository.findOne(Example.of(exampleRate));
            if (rate.isEmpty()) {
                responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // No rate for giver instrument
            } else {
                responseEntity = makeTransaction(exchangeAccount, receiverInstrumentAmount,
                        exchangeRequestDto, responseEntity, receiverBalance, giverBalance,
                        receiverInstrumentAmount * rate.get().getValue());

            }
        }

        return responseEntity;
    }

    private Optional<Balance> getBalance(Account exchangeAccount, Instrument instrument) {
        Optional<Balance> balance;
        Wallet exampleWallet = getWalletByInstrument(exchangeAccount, instrument);

        Optional<Wallet> wallet = walletRepository.findOne(Example.of(
                exampleWallet
        ));

        if (!wallet.isPresent()) {
            wallet = Optional.of(walletRepository.save(exampleWallet));
            balance = Optional.of(balanceRepository.save(Balance.builder().wallet(wallet.get()).qty(0.0).build()));
        } else {
            balance = balanceRepository.findOne(Example.of(Balance.builder().wallet(wallet.get()).build()));
        }
        return balance;
    }

    private ResponseEntity makeTransaction(Account exchangeAccount, double receiverInstrumentAmount,
                                           ExchangeRequestDto exchangeRequestDto, ResponseEntity responseEntity,
                                           Optional<Balance> receiverBalance, Optional<Balance> giverBalance,
                                           double price) {
        //check enough balance of rated instrument
        Double giverQty = giverBalance.get().getQty();
        if (giverQty >= price) {
            //make transaction
            giverBalance.get().setQty(giverQty - price);
            receiverBalance.get().setQty(receiverBalance.get().getQty() + receiverInstrumentAmount);
            exchangeDetailsRepository.save(ExchangeDetails.buildFromRequestDto(exchangeAccount, exchangeRequestDto));

        } else {
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Not enough balance for exchange
        }
        return responseEntity;
    }

    private Wallet getWalletByInstrument(Account exchangeAccount, Instrument instrument) {
        return Wallet.builder()
                .account(exchangeAccount)
                .instrument(instrument)
                .build();
    }

    private Wallet getWalletByAddress(Account exchangeAccount, String walletAddress) {
        return Wallet.builder()
                .account(exchangeAccount)
                .walletAddress(walletAddress)
                .build();
    }

    @PostConstruct
    public void doSome() {
        Account account = accountRepository.save(Account.builder().id(1).name("Jack Black").build());
        Wallet wallet = walletRepository.save(Wallet.builder().account(account).instrument(Instrument.USD).walletAddress("AAABBBVVVCCC").build());
        List<Balance> userBalance = Collections.singletonList(Balance.builder().wallet(wallet).qty(0.0).build());
        account.setBalances(userBalance);
    }
}
