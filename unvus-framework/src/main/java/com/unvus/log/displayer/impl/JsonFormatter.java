package com.unvus.log.displayer.impl;

import com.unvus.log.displayer.LogFormatter;
import com.unvus.util.FieldMap;
import com.unvus.util.JsonUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class JsonFormatter implements LogFormatter {

    public String entry(String methodName, boolean showArgs, String[] params, Object[] args) throws Exception {
        StringJoiner message = new StringJoiner(" ")
            .add("Started").add(methodName).add("method");


        FieldMap map = new FieldMap();
        if (showArgs && Objects.nonNull(params) && Objects.nonNull(args) && params.length == args.length) {

            FieldMap argMap = new FieldMap();

            map.put("args", argMap);

            for (int i = 0; i < params.length; i++) {
                argMap.put(params[i], args[i]);
            }
        }

        return message.add(":").add(JsonUtil.toJson(map)).toString();
    }

    public String exit(String methodName, boolean showResult, Object result, boolean showExecutionTime, ChronoUnit unit, Instant start, Instant end) throws Exception {

        StringJoiner message = new StringJoiner(" ")
            .add("Finished").add(methodName).add("method");


        FieldMap map = new FieldMap();
        if(showExecutionTime) {
            map.put("executionTime",
                new FieldMap()
                    .add("start", start)
                    .add("end", end)
                    .add("duration", String.format("%s %s", unit.between(start, end), unit.name().toLowerCase()))
            );
        }
        if(showResult) {
            map.put("result", result);
        }

        return message.add(":").add(JsonUtil.toJson(map)).toString();
    }
}
