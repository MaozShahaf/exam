package com.luxon.assignment.controller;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping
    public ResponseEntity<?> exchange(ExchangeRequestDto exchangeRequestDto){
        return exchangeService.execute(exchangeRequestDto);
    }
}
