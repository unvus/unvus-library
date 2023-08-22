package com.unvus.log.displayer;

import com.unvus.log.NvLogConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DefaultFormatter implements LogFormatter {

    @Override
    public String entry(String methodName, boolean showArgs, String[] params, Object[] args) throws Exception {
        return NvLogConfig.getFormatter().entry(methodName, showArgs, params, args);
    }

    @Override
    public String exit(String methodName, boolean showResult, Object result, boolean showExecutionTime, ChronoUnit unit, Instant start, Instant end) throws Exception {
        return NvLogConfig.getFormatter().exit(methodName, showResult, result, showExecutionTime, unit, start, end);
    }
}
