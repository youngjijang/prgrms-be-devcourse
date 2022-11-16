package org.prgrms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(org.prgrms.kdt.aop.TrackTime)") // 포인트컷
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called. {}",joinPoint.getSignature().toString());
        var startTime = System.nanoTime(); // 1 -> 1,000,000,000
        var result = joinPoint.proceed();
        var endTime = System.nanoTime() - startTime;
        log.info("After method called with result => {} and time taken {} nanoseconds",result,endTime);
        return result;
    }
}
