// Tag Exception class - enables the passing of the error code and message (using the failure code enums) related to tag operations as the data attribute of a Result Response object

package com.yanqihe.mathsearch.exception;

import lombok.Getter;

@Getter
public class TagException extends Exception {
    int code;
    String message;

    public TagException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
