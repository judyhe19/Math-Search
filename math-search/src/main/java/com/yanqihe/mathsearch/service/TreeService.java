// Tree Service class - contains all methods for realizing tree-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.TagDao;
import com.yanqihe.mathsearch.dao.TagTreeRelationDao;
import com.yanqihe.mathsearch.dao.TreeDao;
import com.yanqihe.mathsearch.domain.Tag;
import com.yanqihe.mathsearch.domain.TagTreeRelation;
import com.yanqihe.mathsearch.domain.Tree;
import com.yanqihe.mathsearch.domain.TreeNode;
import com.yanqihe.mathsearch.enums.TreeFailureCode;
import com.yanqihe.mathsearch.exception.TreeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class TreeService {

    // Tree Service
    @Autowired
    private TreeDao treeDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private TagTreeRelationDao tagTreeRelationDao;

    // Creating a new tag tree (will be called when creating a new root tag)
    public void createTree(Tree tree) throws TreeException {
        Tree byFirstTagId = treeDao.findByFirstTagId(tree.getFirstTagId(), tree.getUserId());
        if (byFirstTagId != null) {
            throw new TreeException(TreeFailureCode.TREE_EXISTS.getCode(), TreeFailureCode.TREE_EXISTS.getMessage());
        }
        else {
            treeDao.insert(tree);
        }
    }

    // Retrieve/find all tag trees related to a user - retrieve/find all the root tags of a user
    public Tree[] getAllTrees(Integer userId) {
        return treeDao.getAllTrees(userId);
    }
    // Retrieve/find a tag tree (root tag) by its id
    public Tree findById(Integer id) {
        return treeDao.findById(id);
    }
    // Retrieve/find all tag trees related to a user - retrieve/find all the root tags of a user
    public Tree[] findTreesByUserId(Integer userId) {
        return treeDao.findTreesByUserId(userId);
    }
    // Retrieve/find a tag tree by its first tag (a root tag)
    public Tree findByFirstTagId(Integer firstTagId, Integer userId) {
        return treeDao.findByFirstTagId(firstTagId, userId);
    }
    // Update the root tag of a tag tree
    public void updateTreeRoot(Tree tree) {
        treeDao.updateTreeRoot(tree);
    }
    // Delete a tag tree
    public void deleteTree(Integer id) {
        treeDao.deleteTree(id);
    }
    // Delete a tag tree, finding it by its first tag's id (root tag's id)
    public void deleteTreeByFirstTagId(Integer id) {
        treeDao.deleteTreeByFirstTagId(id);
    }

    // Retrieve a Tree Node with a root that links to the entire structure of a tag tree
    // Breath First Search (BFS) is used to create a Tree Node for a tag tree.
    // If the user has multiple trees (multiple root tags), the getTagTrees method calling this service in the
    // Tag Controller class will return an array list of TreeNode objects.
    // This array list will be sent as a JSON object to the frontend for nested trees starting at the root TreeNode.
    public TreeNode getTreeNode(int rootTagId) {
        Tag tag = tagDao.findById(rootTagId);

        // BFS on the tree
        TreeNode root  = new TreeNode();
        root.setTag(tag);
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while(!queue.isEmpty()) {
            TreeNode ptr = queue.poll();
            TagTreeRelation[] tagTreeRelations = tagTreeRelationDao.findRelationsByParentTagId(ptr.getTag().getId());
            // create list of child tags
            List<Tag> children = new ArrayList<>();
            for (TagTreeRelation ttR: tagTreeRelations) {
                // obtain child tag id first from each tag-tree relation
                int childTagId = ttR.getChildTagId();
                children.add(tagDao.findById(childTagId));
            }
            for (Tag child:children) {
                TreeNode childNode = new TreeNode();
                childNode.setTag(child);
                ptr.addToChildren(childNode);
                queue.offer(childNode);
            }
        }

        return root;
    }
}
