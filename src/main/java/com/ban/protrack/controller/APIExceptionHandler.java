package com.ban.protrack.controller;

import com.ban.protrack.exception.APIException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public APIException handleAllException(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR", ex.getMessage());
    }

    @ExceptionHandler(value = {NoSuchElementException.class, NullPointerException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public APIException handleNotFoundException(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.NOT_FOUND, "RESOURCE NOT FOUND", ex.getMessage());
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, IllegalStateException.class, SizeLimitExceededException.class})
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public APIException handlePayloadTooLarge(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.PAYLOAD_TOO_LARGE, "FILE TOO LARGE", ex.getMessage());
    }

    @ExceptionHandler(value = {MethodNotAllowedException.class, HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public APIException handleMethodNotAllowedException(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.METHOD_NOT_ALLOWED, "METHOD NOT ALLOWED", ex.getMessage());
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class, PropertyValueException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public APIException handleBadRequestException(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.BAD_REQUEST, "BAD REQUEST", ex.getMessage());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public APIException handleUsernameAlreadyExistedException(Exception ex, WebRequest request) {
        return new APIException(HttpStatus.CONFLICT, "Username already existed", ex.getMessage());
    }
}
