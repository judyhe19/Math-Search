// Tree Exception class - enables the passing of the error code and message (using the failure code enums) related to tree (tag organization) operations as the data attribute of a Result Response object
package com.yanqihe.mathsearch.exception;

import lombok.Getter;

@Getter
public class TreeException extends Exception{
    int code;
    String message;

    public TreeException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
