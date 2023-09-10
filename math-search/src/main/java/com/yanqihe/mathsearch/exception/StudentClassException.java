// Student Class Exception class - enables the passing of the error code and message (using the failure code enums) related to student class operations as the data attribute of a Result Response object

package com.yanqihe.mathsearch.exception;

import lombok.Getter;

@Getter
public class StudentClassException extends Exception {
    int code;
    String message;

    public StudentClassException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
