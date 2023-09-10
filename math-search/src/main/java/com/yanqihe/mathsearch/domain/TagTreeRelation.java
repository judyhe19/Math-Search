// Tag-Tree Relationship class
package com.yanqihe.mathsearch.domain;

import lombok.Data;

@Data
public class TagTreeRelation {
    private Integer id;
    private Integer treeId;
    private Integer parentTagId;
    private Integer childTagId;
}
