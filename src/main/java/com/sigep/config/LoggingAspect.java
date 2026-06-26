package com.sigep.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object profileControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + method.getName();

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        Map<String, Object> additionalInfo = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                String className = args[i].getClass().getName();
                // Omitir clases de infraestructura para no saturar el log
                if (className.startsWith("jakarta.servlet") || className.startsWith("org.springframework")) {
                    continue;
                }

                String paramName = (parameterNames != null && parameterNames.length > i) 
                        ? parameterNames[i] 
                        : "arg" + i;

                // Omitir o enmascarar campos de autenticación o sensibles
                if (paramName.toLowerCase().contains("password") 
                        || paramName.toLowerCase().contains("token") 
                        || paramName.toLowerCase().contains("contraseña")) {
                    additionalInfo.put(paramName, "******");
                } else {
                    additionalInfo.put(paramName, args[i]);
                }
            }
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("additionalInfo", additionalInfo);
            request.setAttribute("controllerMethod", methodName);
        }

        return joinPoint.proceed();
    }
}
