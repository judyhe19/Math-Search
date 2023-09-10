// Student Class Failure Code enum
// Includes all messages and codes for identifying and describing errors that may occur with operations on student classes
package com.yanqihe.mathsearch.enums;

import lombok.Getter;

public enum StudentClassFailureCode {
    CLASS_Q_EXISTS(1, "Class with the same question already exists"),
    CLASS_N_EXISTS(2, "Class with the same name already exists"),
    CLASS_NOT_EXIST(3, "Class does not exists");


    @Getter
    final
    int code;
    @Getter
    final
    String message;

    StudentClassFailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
