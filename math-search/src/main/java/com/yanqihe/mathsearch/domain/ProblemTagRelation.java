// Problem-Tag Relationship Class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class ProblemTagRelation {
    private Integer id;
    private Integer problemId;
    private Integer tagId;
}
