// User Failure Code enum
// Includes all messages and codes for identifying and describing errors that may occur with operations on users
package com.yanqihe.mathsearch.enums;

import lombok.Getter;

public enum UserFailureCode {
    AUTH_FAILURE(1, "Authentication Failed, wrong username and password combination"),
    NO_VALID_ACCOUNT(2, "No valid account, please sign up"),
    ACCOUNT_EXISTS(3, "Email is already used"),
    ACCOUNT_NOT_EXIST(4, "User with id not found"),
    ACCOUNT_LOGOUT(5, "Your current status is logged out. Please log in."),
    INVALID_INVITATION(6, "Code INVALID: Please contact Admin or try again."),
    NOT_ADMIN(7, "This is not an Admin Account.");

    @Getter
    int code;
    @Getter
    String message;

    UserFailureCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
