package com.unvus.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UnvusEventPublisher {
    enum DOMAIN {
        ESTATE, RENT, MEMBER
    }

    private final ApplicationEventPublisher applicationEventPublisher;

    public UnvusEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void onUpdate(final UnvusEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
