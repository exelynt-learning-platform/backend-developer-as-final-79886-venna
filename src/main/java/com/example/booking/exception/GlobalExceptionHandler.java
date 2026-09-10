package com.example.booking.exception;

import com.example.booking.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest req) { return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler({BadRequestException.class, ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorResponse> badRequest(Exception ex, HttpServletRequest req) { return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ErrorResponse> forbidden(ForbiddenException ex, HttpServletRequest req) { return error(HttpStatus.FORBIDDEN, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, "Access denied", req, Map.of());
    }
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> unauthorized(BadCredentialsException ex, HttpServletRequest req) { return error(HttpStatus.UNAUTHORIZED, "Invalid username or password", req, Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", req, fields);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req, Map.of());
    }
    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest req, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
                message == null ? status.getReasonPhrase() : message, req.getRequestURI(), fields));
    }
}
