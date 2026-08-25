package com.bcafinance.backend_saku.core.exception;

import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.SignatureException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String PESAN_AUTENTIKASI_DIPERLUKAN = "Autentikikasi diperlukan";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgument(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> entityNotFound(EntityNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BussinessRuleException.class)
    public ResponseEntity<Map<String, Object>> bussinessRuleException(BussinessRuleException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // BadCredentialException
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> badCredentials(BadCredentialsException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> usernameNotFound(UsernameNotFoundException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // SignatureException
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> signature(SignatureException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> authentication(AuthenticationException e) {
        return build(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(org.springframework.context.support.DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Format input tidak valid");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "statusCode", HttpStatus.BAD_REQUEST.value(),
                        "message", errorMessage
                )
        );
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {

        return ResponseEntity.status(status).body(body(status, message));
    }

    private Map<String, Object> body(HttpStatus status, String message) {

        return Map.of(
                "statusCode", status.value(),
                "message", message);
    }
}
