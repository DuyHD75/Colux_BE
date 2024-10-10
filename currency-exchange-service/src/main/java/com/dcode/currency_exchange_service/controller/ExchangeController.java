package com.dcode.currency_exchange_service.controller;

import com.dcode.currency_exchange_service.model.CurrencyExchange;
import com.dcode.currency_exchange_service.repository.CurrencyExchangeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ExchangeController {


    @Autowired
    private CurrencyExchangeRepo currencyExchangeRepo;

    @Autowired
    private Environment environment;

    private Logger logger = LoggerFactory.getLogger(CurrencyExchange.class);

    @GetMapping
    public String test() {
        return "Hello";
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retrieveExchangeValue(@PathVariable String from,
                                                  @PathVariable String to) {


        logger.info("retrieveExchangeValue called with {} to {}", from, to);

        CurrencyExchange currencyExchange = currencyExchangeRepo.findByFromAndTo(from, to);
        if (currencyExchange == null) {
            throw new RuntimeException("Unable to find data for " + from + " to " + to);
        }

        String port = environment.getProperty("local.server.port");

        /*Change kubernetes*/
        String host = environment.getProperty("HOSTNAME");
        String version = "v11";

        currencyExchange.setEnvironment(port + " " + host + " " + version);

        return currencyExchange;
    }


}
