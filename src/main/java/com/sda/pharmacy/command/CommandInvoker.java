package com.sda.pharmacy.command;

import org.springframework.stereotype.Component;

@Component
public class CommandInvoker {

    public void executeCommand(Command command) {

        command.execute();

    }
}