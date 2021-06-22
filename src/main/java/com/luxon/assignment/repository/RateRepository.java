package com.luxon.assignment.repository;

import com.luxon.assignment.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate,Integer> {}
