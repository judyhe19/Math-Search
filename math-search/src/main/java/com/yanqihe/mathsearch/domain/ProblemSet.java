// Problem Set class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class ProblemSet {
    private Integer id;
    private String name;
    private Integer ownerId;
    private Integer classId;
}
