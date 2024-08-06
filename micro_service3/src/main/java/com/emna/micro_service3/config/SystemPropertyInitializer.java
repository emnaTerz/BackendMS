package com.emna.micro_service3.config;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SystemPropertyInitializer {

    @PostConstruct
    public void init() {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
    }
}

