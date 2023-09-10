// User class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String token;
    private Timestamp tokenExpire;
    private String accountType;
}

