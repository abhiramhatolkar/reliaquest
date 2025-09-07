package com.reliaquest.api.config;

import com.reliaquest.api.interceptor.MethodLoggingInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfiguration {
    
    @Bean
    public MethodLoggingInterceptor methodLoggingInterceptor() {
        return new MethodLoggingInterceptor();
    }
    
    @Bean
    public Pointcut serviceAndControllerPointcut() {
        return new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // Only intercept public methods
                if (!Modifier.isPublic(method.getModifiers())) {
                    return false;
                }
                
                // Match methods in classes annotated with Spring stereotypes
                return targetClass.isAnnotationPresent(Service.class) ||
                       targetClass.isAnnotationPresent(Controller.class) ||
                       targetClass.isAnnotationPresent(RestController.class) ||
                       targetClass.isAnnotationPresent(Repository.class);
            }
            
            @Override
            public ClassFilter getClassFilter() {
                return clazz -> {
                    // Filter classes by package or annotation
                    String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
                    
                    // Only apply to your application packages
                    return packageName.startsWith("com.reliaquest.api") &&
                           (clazz.isAnnotationPresent(Service.class) ||
                            clazz.isAnnotationPresent(Controller.class) ||
                            clazz.isAnnotationPresent(RestController.class) ||
                            clazz.isAnnotationPresent(Repository.class));
                };
            }
        };
    }
    
    @Bean
    public Advisor loggingAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(serviceAndControllerPointcut());
        advisor.setAdvice(methodLoggingInterceptor());
        advisor.setOrder(1); // Set order if you have multiple advisors
        return advisor;
    }
}
