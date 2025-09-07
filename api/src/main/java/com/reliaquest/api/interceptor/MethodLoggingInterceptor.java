package com.reliaquest.api.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MethodLoggingInterceptor implements MethodInterceptor {
    
  private static final Logger logger = LoggerFactory.getLogger(MethodLoggingInterceptor.class);
      
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    String className = invocation.getThis().getClass().getSimpleName();
    String methodName = invocation.getMethod().getName();
    Object[] args = invocation.getArguments();
    
    logger.info("ENTERING -> {}.{}() with arguments: {}", 
              className, methodName, args);
    
    long startTime = System.currentTimeMillis();
    
    try {
      Object result = invocation.proceed();
      long executionTime = System.currentTimeMillis() - startTime;   
      logger.info("EXITING -> {}.{}() [Execution time: {}ms]", 
                className, methodName, executionTime);
      logger.debug("Result {}", result);
      
      return result;
        
    } catch (Exception e) {
      long executionTime = System.currentTimeMillis() - startTime;
      logger.error("EXCEPTION in {}.{}() after {}ms: {}", 
                  className, methodName, executionTime, e.getMessage());
      
      throw e;
    }
  }
}