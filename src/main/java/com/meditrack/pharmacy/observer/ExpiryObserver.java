package com.meditrack.pharmacy.observer;

import org.springframework.stereotype.Component;

@Component
public class ExpiryObserver
        implements Observer {

    @Override
    public void update() {

        System.out.println(
                "Expiry alerts refreshed"
        );
    }
}