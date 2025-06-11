package com.fource.hrbank.aop;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@within(com.fource.hrbank.annotation.Logging)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("[호출] : {} with args = {}", methodName, Arrays.toString(args));

        Object result = null;
        try {
            result = joinPoint.proceed();
            log.info("[리턴값] : {} with result {}", methodName, result);
            return result;
        } catch (Exception e) {
          log.error("[예외 발생] : {} with message = {}", methodName, e.getMessage());
          throw e;
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;

            if (time > 1000) {
                log.warn("[느린 실행] : {} = {}ms ", methodName, time);
            } else {
                log.info("[실행 완료] : {} = {}ms ", methodName, time);
            }
        }
    }
}