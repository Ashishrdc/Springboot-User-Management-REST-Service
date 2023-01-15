package com.develop.assignmentPart2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    // Handling the RuntimeException.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map> userNotFound(RuntimeException e){
        Map<String, String> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("success", "false");
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    // Handling the MethodArgumentNotValidException thrown when invalid data is sent through api.
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgsNotValidException(BindException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
           String fieldName = ((FieldError) error).getField();
           String message = error.getDefaultMessage();
           errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
