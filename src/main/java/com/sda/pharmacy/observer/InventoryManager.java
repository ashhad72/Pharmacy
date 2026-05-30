package com.sda.pharmacy.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryManager {

    private final List<Observer> observers =
            new ArrayList<>();

    public void addObserver(
            Observer observer
    ) {

        observers.add(observer);
    }

    public void notifyObservers() {

        for (Observer observer : observers) {

            observer.update();
        }
    }
}