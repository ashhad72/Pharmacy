package com.sda.pharmacy.observer;

import org.springframework.stereotype.Component;

@Component
public class LowStockObserver
        implements Observer {

    @Override
    public void update() {

        System.out.println(
                "Low stock alerts refreshed"
        );
    }
}