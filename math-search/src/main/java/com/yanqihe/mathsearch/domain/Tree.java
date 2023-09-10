// Tree class - used for organizing, representing tag relationships (root tags and nested tags)
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class Tree {
    private Integer id;
    private Integer firstTagId;
    private Integer userId;
}
