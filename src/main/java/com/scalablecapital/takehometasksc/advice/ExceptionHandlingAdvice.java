package com.scalablecapital.takehometasksc.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ExceptionHandlingAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException ex) {
        log.error("Wrong request input provided! - {}", ex.getMessage());
        return new ResponseEntity<List<String>>(
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(x -> x.getDefaultMessage())
                        .collect(Collectors.toList())
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException
            (ConstraintViolationException ex) {
        log.error("Constraint violation! - {}", ex.getMessage());
        return new ResponseEntity<String>(ex.getMessage().split("\\.")[1], HttpStatus.NOT_FOUND);
    }

}
