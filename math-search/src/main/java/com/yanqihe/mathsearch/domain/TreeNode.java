// Tree Node class - used for representing the relationship of tags in one object
package com.yanqihe.mathsearch.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    public Tag tag;
    public List<TreeNode> children;

    public void addToChildren(TreeNode childNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(childNode);
    }
}


