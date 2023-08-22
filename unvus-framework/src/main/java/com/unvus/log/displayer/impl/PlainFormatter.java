package com.unvus.log.displayer.impl;

import com.unvus.log.displayer.LogFormatter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class PlainFormatter implements LogFormatter {

    public String entry(String methodName, boolean showArgs, String[] params, Object[] args) throws Exception {
        StringJoiner message = new StringJoiner(" ")
            .add("Started").add(methodName).add("method");

        if (showArgs && Objects.nonNull(params) && Objects.nonNull(args) && params.length == args.length) {

            Map<String, Object> values = new HashMap<>(params.length);

            for (int i = 0; i < params.length; i++) {
                values.put(params[i], args[i]);
            }

            message.add("with args:")
                .add(values.toString());
        }

        return message.toString();
    }

    public String exit(String methodName, boolean showResult, Object result, boolean showExecutionTime, ChronoUnit unit, Instant start, Instant end) throws Exception {
        StringJoiner message = new StringJoiner(" ")
            .add("Finished").add(methodName).add("method");

        if (showExecutionTime) {
            message.add("in").add(String.format("%s %s", unit.between(start, end), unit.name().toLowerCase()));
        }

        if (showResult) {
            message.add("with return:").add(result.toString());
        }

        return message.toString();
    }
}
