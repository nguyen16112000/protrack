package com.ban.protrack.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class APIException {
    private final int code;
    private final String message;
    @Nullable
    private final String error;

    public APIException(HttpStatus code, String message, String error) {
        this.code = code.value();
        this.message = message;
        this.error = error;
    }
}
