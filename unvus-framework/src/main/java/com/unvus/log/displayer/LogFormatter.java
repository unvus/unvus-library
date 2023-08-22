package com.unvus.log.displayer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public interface LogFormatter {
    String entry(String methodName, boolean showArgs, String[] params, Object[] args) throws Exception;
    String exit(String methodName, boolean showResult, Object result, boolean showExecutionTime, ChronoUnit unit, Instant start, Instant end) throws Exception;
}
