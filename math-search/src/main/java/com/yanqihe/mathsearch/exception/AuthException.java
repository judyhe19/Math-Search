// Authentication Exception class - enables the passing of the error code and message (using the failure code enums) related to authentication operations as the data attribute of a Result Response object
package com.yanqihe.mathsearch.exception;

import lombok.Getter;

@Getter
public class AuthException extends Exception {
    int code;
    String message;

    public AuthException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
