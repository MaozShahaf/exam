package com.luxon.assignment.repository;

import com.luxon.assignment.entity.ExchangeDetails;
import com.luxon.assignment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeDetailsRepository extends JpaRepository<ExchangeDetails, Integer> {}
