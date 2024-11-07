package com.example.smart_task_management.Util;



import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

/*
    @Around("execution(* com.example.smart_task_management..*(..))")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getDeclaringTypeName().concat(".").concat(joinPoint.getSignature().getName());




        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();


        StringBuilder params = new StringBuilder();
        if (parameterNames != null && parameterValues != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                params.append(parameterNames[i]).append("=").append(parameterValues[i]);
                if (i < parameterNames.length - 1) {
                    params.append(", ");
                }
            }
        }

        logger.info("Method '{}' called with parameters:" +"\n [{}]", methodName, params.toString());


        return joinPoint.proceed();
    }

    // Intercept methods in the specified packages
    @Around("execution(* com.example.smart_task_management..*(..)) || execution(* com.example.smart_task_management..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();


        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getDeclaringTypeName().concat(".").concat(joinPoint.getSignature().getName());
        logger.info("{} executed in {} ms", methodName, (endTime - startTime));


        return result;
    }

 */
}
