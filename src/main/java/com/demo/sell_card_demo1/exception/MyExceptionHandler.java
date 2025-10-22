package com.demo.sell_card_demo1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {
    // Sửa đối số thành BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin: " + exception.getMessage());

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST); // Trả về 400
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin");
        StringBuilder responseMessage = new StringBuilder();
        for(FieldError fieldError: exception.getFieldErrors()){
            responseMessage.append(fieldError.getDefaultMessage()).append("\n");
        }
        return new ResponseEntity<>(responseMessage.toString(), HttpStatus.BAD_REQUEST);
    }
}
