package com.unvus.event;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import lombok.Data;

@Getter
@Setter
public class UnvusEvent<E> extends ApplicationEvent {

    public static final String MESSAGE = "message";

    public enum TYPE {
        ADDED, UPDATED, DELETED, STATUS_CHANGED, APPROVED, REJECT, DUPLICATED
    }


    private TYPE type;

    private E payload;

    private Map<String, Object> intent = new HashMap<>();


    /**
     * Create a new ApplicationEvent.
     *
     * @param type
     * @param source
     * @param intent
     */
    public UnvusEvent(TYPE type, E source, Map<String, Object> intent) {
        super(source);
        this.type = type;
        this.payload = source;
        this.intent = intent;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param type
     * @param source the object on which the event initially occurred (never {@code null})
     * @param message
     */
    public UnvusEvent(TYPE type, E source, String message) {
        super(source);
        this.type = type;
        this.payload = source;
        this.intent = new HashMap<>();
        this.intent.put(MESSAGE, message);
    }


    public UnvusEvent(TYPE type, E source) {
        super(source);
        this.type = type;
        this.payload = source;
        this.intent = new HashMap<>();
    }

    public void addParam(String key, Object val) {
        intent.put(key, val);
    }

    public Object getParam(String key) {
        return intent.get(key);
    }

    public Map<String, Object> getIntent() {
        return intent;
    }

    public String getMessage() {
        return (String)this.intent.get(MESSAGE);
    }


    public String getSourceClassName() {
        return payload.getClass().getSimpleName();
    }

}
