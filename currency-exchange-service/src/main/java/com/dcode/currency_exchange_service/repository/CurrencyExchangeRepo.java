package com.dcode.currency_exchange_service.repository;

import com.dcode.currency_exchange_service.model.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepo extends JpaRepository<CurrencyExchange, Long>{

    CurrencyExchange findByFromAndTo(String from, String to);


}
