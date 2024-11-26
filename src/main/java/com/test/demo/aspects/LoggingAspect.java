package com.test.demo.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @AfterReturning(pointcut = "execution(* com.test.demo.controller..*.*(..)) || execution(* com.test.demo.service..*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("AtferReturning: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + " result: " + result);
    }

    @AfterThrowing(pointcut = "execution(* com.test.demo.controller..*.*(..)) || execution(* com.test.demo.service..*.*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.info("AfterThrowing: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + " exception: " + e.getMessage());
    }

    @Around("execution(* com.test.demo.controller..*.*(..)) || execution(* com.test.demo.service..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around before: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        log.info("Around after: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() +"Execution time: " + (end - start) + "ms");
        return result;
    }
}
