// Problem-Problem Set Relationship Class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class ProblemProblemSetRelation {
    private Integer id;
    private Integer problemId;
    private Integer setId;
}
