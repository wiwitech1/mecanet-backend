package com.wiwitech.mecanetbackend.shared.infrastructure.web;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.wiwitech.mecanetbackend.iam.domain.exceptions.UserLimitExceededException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.PlantLimitExceededException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.MachineLimitExceededException;
import com.wiwitech.mecanetbackend.assetmanagment.domain.exceptions.ProductionLineLimitExceededException;

/**
 * Global Exception Handler
 * <p>
 * This class handles all exceptions thrown by the application and returns
 * structured JSON responses with appropriate HTTP status codes.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle RuntimeException and return a structured error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        LOGGER.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException and return a structured error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        LOGGER.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle UserLimitExceededException and return a structured error response
     */
    @ExceptionHandler(UserLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleUserLimitExceededException(
            UserLimitExceededException ex, WebRequest request) {
        
        LOGGER.warn("User limit exceeded for tenant {}: current={}, limit={}", 
                   ex.getTenantId(), ex.getCurrentCount(), ex.getLimit());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "User Limit Exceeded",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Agregar información adicional sobre el límite
        errorResponse.put("tenantId", ex.getTenantId());
        errorResponse.put("currentCount", ex.getCurrentCount());
        errorResponse.put("limit", ex.getLimit());
        errorResponse.put("resourceType", "users");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle PlantLimitExceededException and return a structured error response
     */
    @ExceptionHandler(PlantLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handlePlantLimitExceededException(
            PlantLimitExceededException ex, WebRequest request) {
        
        LOGGER.warn("Plant limit exceeded for tenant {}: current={}, limit={}", 
                   ex.getTenantId(), ex.getCurrentCount(), ex.getLimit());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Plant Limit Exceeded",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Agregar información adicional sobre el límite
        errorResponse.put("tenantId", ex.getTenantId());
        errorResponse.put("currentCount", ex.getCurrentCount());
        errorResponse.put("limit", ex.getLimit());
        errorResponse.put("resourceType", "plants");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle MachineLimitExceededException and return a structured error response
     */
    @ExceptionHandler(MachineLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMachineLimitExceededException(
            MachineLimitExceededException ex, WebRequest request) {
        
        LOGGER.warn("Machine limit exceeded for tenant {}: current={}, limit={}", 
                   ex.getTenantId(), ex.getCurrentCount(), ex.getLimit());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Machine Limit Exceeded",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Agregar información adicional sobre el límite
        errorResponse.put("tenantId", ex.getTenantId());
        errorResponse.put("currentCount", ex.getCurrentCount());
        errorResponse.put("limit", ex.getLimit());
        errorResponse.put("resourceType", "machines");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle ProductionLineLimitExceededException and return a structured error response
     */
    @ExceptionHandler(ProductionLineLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleProductionLineLimitExceededException(
            ProductionLineLimitExceededException ex, WebRequest request) {
        
        LOGGER.warn("Production line limit exceeded for tenant {}: current={}, limit={}", 
                   ex.getTenantId(), ex.getCurrentCount(), ex.getLimit());
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Production Line Limit Exceeded",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Agregar información adicional sobre el límite
        errorResponse.put("tenantId", ex.getTenantId());
        errorResponse.put("currentCount", ex.getCurrentCount());
        errorResponse.put("limit", ex.getLimit());
        errorResponse.put("resourceType", "production_lines");
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle generic Exception and return a structured error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        LOGGER.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create a structured error response
     */
    private Map<String, Object> createErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
} 