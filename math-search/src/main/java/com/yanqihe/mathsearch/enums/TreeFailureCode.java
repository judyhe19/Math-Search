// Tree Failure Code enum
// Includes all messages and codes for identifying and describing errors that may occur with operations on trees (organization of tags)
package com.yanqihe.mathsearch.enums;

import lombok.Getter;

public enum TreeFailureCode {
    TREE_EXISTS(1, "Tree with the same root already exists"),
    TREE_NOT_EXIST(2, "Question does not exists");

    @Getter
    final
    int code;
    @Getter
    final
    String message;

    TreeFailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
