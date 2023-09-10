// Question Failure Code enum
// Includes all messages and codes for identifying and describing errors that may occur with operations on problems and problem sets
package com.yanqihe.mathsearch.enums;

import lombok.Getter;

public enum QuestionFailureCode {
    QUESTION_EXISTS(1, "Problem with the same question already exists"),
    QUESTION_NOT_EXIST(2, "Question does not exists"),
    PS_EXISTS(3, "Problem set with the same name already exists"),
    PS_NOT_EXISTS(4, "Problem set does not exist"),
    NO_TAG_SELECTED(5, "Problem must be created with a chosen tag");

    @Getter
    final
    int code;
    @Getter
    final
    String message;

    QuestionFailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
