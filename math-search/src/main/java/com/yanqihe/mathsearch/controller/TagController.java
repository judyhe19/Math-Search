// Tag Controller class - contains all available HTTP calls for tag-related services
package com.yanqihe.mathsearch.controller;

import com.yanqihe.mathsearch.common.ResultResponse;
import com.yanqihe.mathsearch.domain.Tag;
import com.yanqihe.mathsearch.domain.TagTreeRelation;
import com.yanqihe.mathsearch.domain.Tree;
import com.yanqihe.mathsearch.domain.TreeNode;
import com.yanqihe.mathsearch.enums.TagFailureCode;
import com.yanqihe.mathsearch.exception.TagException;
import com.yanqihe.mathsearch.exception.TreeException;
import com.yanqihe.mathsearch.service.TagService;
import com.yanqihe.mathsearch.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

// Enabling cross-origin for running of the frontend server (Reference: https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    TagService tagService;
    @Autowired
    TreeService treeService;

    // Add (create new) tag to the database (related to a specific user's id)
    // The tag may be a root tag or a nested tag under a specified parent tag
    @PostMapping("/add/{parentTagId}")
    public ResultResponse<Tag> addTag(@RequestBody Tag tag, @PathVariable("parentTagId") Integer parentTagId) {
        ResultResponse<Tag> resultResponse = new ResultResponse<>();
        try {
            if (parentTagId > 0) {
                if (tagService.tagExists(parentTagId)) {
                    tagService.createTag(tag);
                    Integer tagId = tagService.findByNameAndUserId(tag.getName(), tag.getUserId()).getId();
                    Tree tree = treeService.findByFirstTagId(parentTagId, tag.getUserId());
                    Integer treeId;
                    if (tree == null) {
                        // parent tag is not the root tag of a tag tree. It is a nested tag. Hence, search the tag tree relations table in database for where child_tag_id = parentTagId and obtain the tree id.
                        treeId = tagService.findRelationByChildTagId(parentTagId).getTreeId();
                    }
                    else {
                        treeId = tree.getId();
                    }
                    tagService.createTagTreeRelation(treeId, parentTagId, tagId);
                }
                else {
                    resultResponse.setCode(TagFailureCode.PARENT_TAG_NOT_EXIST.getCode());
                    resultResponse.setMessage(TagFailureCode.PARENT_TAG_NOT_EXIST.getMessage());
                    return resultResponse;
                }
            }
            else {
                tagService.createTag(tag);
                Integer tagId = tagService.findByNameAndUserId(tag.getName(), tag.getUserId()).getId();
                Tree tree = new Tree();
                tree.setFirstTagId(tagId);
                tree.setUserId(tag.getUserId());
                treeService.createTree(tree);
            }
            resultResponse.setData(tag);
        } catch (TagException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        } catch (TreeException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Update the name of a tag of the user in the database
    @PostMapping("/updateTagName/{newName}")
    public ResultResponse<Tag> updateTagName(@RequestBody Tag tag, @PathVariable("newName") String newName) {
        ResultResponse<Tag> resultResponse = new ResultResponse<>();
        Tag foundTag = tagService.findByNameAndUserId(tag.getName(), tag.getUserId());
        if (foundTag != null) {
            tag.setId(foundTag.getId());
            tag.setName(newName);
            tagService.updateTag(tag);
            resultResponse.setData(tag);
        } else {
            resultResponse.setCode(TagFailureCode.TAG_NOT_EXIST.getCode());
            resultResponse.setMessage(TagFailureCode.TAG_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Move a tag of the user
    // The tag may be moved and nested under a different tag or become a root tag
    @PostMapping("/moveTag/{targetTagId}")
    public ResultResponse<Tag> moveTag(@RequestBody Tag tag, @PathVariable("targetTagId") Integer targetTagId) throws TreeException {
        ResultResponse<Tag> resultResponse = new ResultResponse<>();
        Tag oldTag = tagService.findByNameAndUserId(tag.getName(), tag.getUserId());
        if (tagService.tagExists(oldTag)) {
            // Update the parent of the tag to the target tag if moved under another tag. If turned into a separate root tag, then create new tree with root tag as the current tag.
            if (targetTagId > 0) {
                // Update the tree for the tag and all its nested tags
                tagService.moveTag(oldTag, targetTagId);
                // Update parent for the tag
                tagService.updateParentForChildTag(targetTagId, oldTag.getId());

                // turn current tag into a child tag and delete its own tree if it was a root tag of a tree
                if (tagService.findRelationByChildTagId(oldTag.getId()) == null) {
                    // turn tag into a child tag by creating new Tag Tree Relation
                    Integer newTreeId = tagService.findRelationsByAnyTagId(oldTag.getId()).getTreeId();
                    tagService.createTagTreeRelation(newTreeId, targetTagId, oldTag.getId());

                    // Delete old tree
                    treeService.deleteTreeByFirstTagId(oldTag.getId());
                }
            }
            else {
                // Create new tree with current tag being the root tag
                Tree tree = new Tree();
                tree.setFirstTagId(oldTag.getId());
                tree.setUserId(oldTag.getUserId());
                treeService.createTree(tree);
                // Delete the relationship indicating that the current tag is nested under another tag
                tagService.deleteTagTreeRelationByChildTagId(oldTag.getId());
                // Move all nested tags (if any) under the current tag under the newly created tag tree
                tagService.moveTag(oldTag, oldTag.getId());
            }



            Tag newTag = tagService.findByNameAndUserId(tag.getName(), tag.getUserId());
            resultResponse.setData(newTag);
        } else {
            resultResponse.setCode(TagFailureCode.TAG_NOT_EXIST.getCode());
            resultResponse.setMessage(TagFailureCode.TAG_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Search for tags belonging to a user by the tag's name
    @GetMapping("/search/{userId}/{name}")
    public ResultResponse<Tag[]> searchTagsByName(@PathVariable("name") String name, @PathVariable("userId") Integer userId) {
        ResultResponse<Tag[]> resultResponse = new ResultResponse<>();
        resultResponse.setData(tagService.fullTextSearchByNameAndUserId(name, userId));
        return resultResponse;
    }

    // Delete a user's tag from the database
    @PostMapping("/delete")
    public ResultResponse<String> deleteTag(@RequestBody Tag tag) {
        ResultResponse<String> resultResponse = new ResultResponse<>();
        Tag foundTag = tagService.findByNameAndUserId(tag.getName(), tag.getUserId());
        try {
            if (tagService.tagExists(foundTag)) {
                resultResponse.setData(tagService.deleteTag(foundTag.getId(), foundTag.getUserId()));
            } else {
                resultResponse.setCode(TagFailureCode.TAG_NOT_EXIST.getCode());
                resultResponse.setMessage(TagFailureCode.TAG_NOT_EXIST.getMessage());
            }
        } catch (TagException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Check if there are tags nested under a tag of the user
    @GetMapping("/checkForNestedTag/{tagId}")
    public ResultResponse<Boolean> checkForNestedTag (@PathVariable("tagId") Integer tagId) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();

        TagTreeRelation[] childTagsRelations = tagService.findRelationsByParentTagId(tagId);
        if (childTagsRelations.length > 0) {
            resultResponse.setData(true);
        }
        else {
            resultResponse.setData(false);
        }
        return resultResponse;
    }

    // Retrieve all tags nested under a tag of the user
    @GetMapping("/getNestedTags/{tagId}")
    public ResultResponse<Tag[]> getNestedTags (@PathVariable("tagId") Integer tagId) {
        ResultResponse<Tag[]> resultResponse = new ResultResponse<>();
        TagTreeRelation[] childTagsRelations = tagService.findRelationsByParentTagId(tagId);
        Tag[] tags = new Tag[childTagsRelations.length];
        for (int i = 0; i < childTagsRelations.length; i++) {
            tags[i] = tagService.findById(childTagsRelations[i].getChildTagId());
        }
        resultResponse.setData(tags);
        return resultResponse;
    }

    // Retrieve all root tags (tags that do not have any parent tags) of the user
    @GetMapping("/getRootTags/{userId}")
    public ResultResponse<Tag[]> getRootTags (@PathVariable("userId") Integer userId) {
        ResultResponse<Tag[]> resultResponse = new ResultResponse<>();
        Tree[] trees = treeService.getAllTrees(userId);
        Tag[] tags = new Tag[trees.length];
        for (int i = 0; i < trees.length; i++) {
            tags[i] = tagService.findById(trees[i].getFirstTagId());
        }
        resultResponse.setData(tags);
        return resultResponse;
    }

    // Retrieve all tag trees of the user.
    // Tag trees are a structure indicating the relationships between tags (root and nested tags) of the user
    @GetMapping("/getTagTrees/{userId}")
    public ResultResponse<ArrayList<TreeNode>> getTagTrees (@PathVariable("userId") Integer userId) {
        ResultResponse<ArrayList<TreeNode>> resultResponse = new ResultResponse<>();
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        Tree[] trees = treeService.getAllTrees(userId);
        // TagContoller ->getLevelOne tags -> build treeNode for each tag -> return treeNode list to front end
        for (int i = 0; i < trees.length; i++) {
            Tag rootTag = tagService.findById(trees[i].getFirstTagId());
            treeNodes.add(treeService.getTreeNode(rootTag.getId()));
        }

        // TreeNode[] treeNodesArray = new TreeNode[treeNodes.size()];
        // resultResponse.setData(treeNodes.toArray(treeNodesArray));
        resultResponse.setData(treeNodes);
        return resultResponse;


    }

    // Retrieve the information of a tag of the user, the name of the tag, by its id
    @GetMapping("/info/{tagId}")
    public ResultResponse<Tag> tagInfo (@PathVariable("tagId") Integer tagId) {
        ResultResponse<Tag> resultResponse = new ResultResponse<>();
        Tag tag = tagService.findById(tagId);
        resultResponse.setData(tag);
        return resultResponse;
    }

}
