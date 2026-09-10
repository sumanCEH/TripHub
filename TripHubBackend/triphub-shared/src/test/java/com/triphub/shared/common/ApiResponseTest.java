package com.triphub.shared.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ApiResponse")
class ApiResponseTest {

    @Test
    @DisplayName("Should create successful ApiResponse with data using builder")
    void testBuilderWithSuccess() {
        // Arrange
        String testData = "test";
        
        // Act
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Success")
                .data(testData)
                .build();
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    @DisplayName("Should create failure ApiResponse with builder")
    void testBuilderWithFailure() {
        // Arrange & Act
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Error occurred")
                .build();
        
        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Should create successful ApiResponse using success factory method")
    void testSuccessFactoryMethod() {
        // Arrange
        Integer testData = 42;
        String message = "Operation completed";
        
        // Act
        ApiResponse<Integer> response = ApiResponse.success(testData, message);
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    @DisplayName("Should create failure ApiResponse using failure factory method")
    void testFailureFactoryMethod() {
        // Arrange
        String errorMessage = "Resource not found";
        
        // Act
        ApiResponse<String> response = ApiResponse.failure(errorMessage);
        
        // Assert
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Should handle null data in success response")
    void testSuccessWithNullData() {
        // Arrange & Act
        ApiResponse<Object> response = ApiResponse.success(null, "Data is null");
        
        // Assert
        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertEquals("Data is null", response.getMessage());
    }

    @Test
    @DisplayName("Should support different generic types")
    void testGenericTypes() {
        // Arrange
        ApiResponse<Boolean> boolResponse = ApiResponse.success(true, "Boolean");
        ApiResponse<Double> doubleResponse = ApiResponse.success(3.14, "Double");
        
        // Act & Assert
        assertTrue(boolResponse.getData());
        assertEquals(3.14, doubleResponse.getData());
    }

    @Test
    @DisplayName("Should set and get properties using setters")
    void testSettersAndGetters() {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>();
        
        // Act
        response.setSuccess(true);
        response.setMessage("Test message");
        response.setData("Test data");
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Test data", response.getData());
    }

    @Test
    @DisplayName("Should create ApiResponse with no-arg constructor")
    void testNoArgConstructor() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>();
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Should create ApiResponse with all-arg constructor")
    void testAllArgConstructor() {
        // Arrange
        String data = "response data";
        
        // Act
        ApiResponse<String> response = new ApiResponse<>(true, "Success", data);
        
        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }
}
