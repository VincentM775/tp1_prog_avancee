package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Intercepte toutes les méthodes publiques des classes du package service.
     * Log l'entrée, la sortie, la durée d'exécution et les exceptions.
     */
    @Around("execution(* org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = formatArgs(joinPoint.getArgs());

        log.info("[ENTRY] {}.{}({})", className, methodName, args);

        long start = System.currentTimeMillis();

        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            long duration = System.currentTimeMillis() - start;
            log.info("[EXIT]  {}.{} - {}ms", className, methodName, duration);
            return result;
        } finally {
            if (!success) {
                long duration = System.currentTimeMillis() - start;
                log.warn("[ERROR] {}.{} - {}ms - exception thrown", className, methodName, duration);
            }
        }
    }

    /**
     * Formate les arguments en masquant les données sensibles
     * et en évitant de déclencher le lazy loading des entités JPA.
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return Arrays.stream(args)
                .map(this::safeToString)
                .collect(Collectors.joining(", "));
    }

    private String safeToString(Object arg) {
        if (arg == null) {
            return "null";
        }
        String className = arg.getClass().getSimpleName();
        // Ne pas logger les objets JPA (risque de lazy loading)
        if (className.contains("Annonce") || className.contains("User")
                || className.contains("Category")) {
            return className + "@[entity]";
        }
        // Ne pas logger les mots de passe ou tokens
        String str = arg.toString();
        if (str.length() > 100) {
            return str.substring(0, 100) + "...";
        }
        return str;
    }
}
