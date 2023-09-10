// Tag Failure Code enum
// Includes all messages and codes for identifying and describing errors that may occur with operations on tags
package com.yanqihe.mathsearch.enums;

import lombok.Getter;

public enum TagFailureCode {
    TAG_EXISTS(1, "Tag with the same name already exists"),
    TAG_NOT_EXIST(2, "Tag does not exists"),
    PARENT_TAG_NOT_EXIST(3, "Parent tag does not exists");


    @Getter
    final
    int code;
    @Getter
    final
    String message;

    TagFailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
