// Tag Service class - contains all methods for realizing tag-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.TagDao;
import com.yanqihe.mathsearch.dao.TagTreeRelationDao;
import com.yanqihe.mathsearch.domain.*;
import com.yanqihe.mathsearch.enums.TagFailureCode;
import com.yanqihe.mathsearch.exception.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    @Autowired
    private TagDao tagDao;
    @Autowired
    private TagTreeRelationDao tagTreeRelationDao;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private TreeService treeService;

    // Create a new tag - the tag may be a nested tag or a root tag
    public void createTag(Tag tag) throws TagException {
        Tag t = tagDao.findByName(tag.getName(), tag.getUserId());
        if (t != null) {
            throw new TagException(TagFailureCode.TAG_EXISTS.getCode(), TagFailureCode.TAG_EXISTS.getMessage());
        }
        else {
            tagDao.insert(tag);
        }
    }

    // Retrieve/find a tag by its id
    public Tag findById(Integer id) {
        return tagDao.findById(id);
    }

    // Retrieve/find all tags related to a user
    public Tag[] findTagsByUserId(Integer userId) {
        return tagDao.findTagsByUserId(userId);
    }

    // Retrieve/find a tag by its name and user (teacher) id
    public Tag findByNameAndUserId(String name, Integer userId) {
        return  tagDao.findByName(name, userId);
    }

    // Search for a tag by its name and user (teacher) id
    public Tag[] fullTextSearchByNameAndUserId(String name, Integer userId) {
        return tagDao.fullTextSearchByNameAndUserId(name, userId);
    }

    // Update a tag
    public void updateTag(Tag tag) {
        tagDao.updateTag(tag);
    }

    // Move a tag
    // Includes: moving and nesting it under another tag in the same tag tree,
    // moving and nesting it in a different tag tree with a different root tag,
    // or turning it into a root tag (forming a new tag tree)
    public Tag moveTag(Tag tag, Integer targetTagId) {
        Integer targetTreeId;
        // Retrieve targetTreeId from Tag_Tree_Relationship table
        TagTreeRelation targetTreeRelation = tagTreeRelationDao.findRelationByChildTagId(targetTagId);
        if (targetTreeRelation != null) {
            // target tag is a child (nested) tag
            targetTreeId = targetTreeRelation.getTreeId();
        }
        else {
            // target tag is/has been turned into a root tag
            targetTreeId = treeService.findByFirstTagId(targetTagId, tag.getUserId()).getId();
        }

        // Does the tag have child tags?
        TagTreeRelation[] tagTreeRelations = tagTreeRelationDao.findRelationsByParentTagId(tag.getId());
        if (tagTreeRelations.length == 0) { // No child tag
            // Update tree for tag
            tagTreeRelationDao.updateTreeForChildTag(targetTreeId, tag.getId());

        }
        else {
            // Update the tree for all the tag's child tags first (update all nested tags below the tag)
            for (TagTreeRelation tagTreeRelation : tagTreeRelations) {
                Tag childTag = tagDao.findById(tagTreeRelation.getChildTagId());
                moveTag(childTag, targetTagId);
                tagTreeRelationDao.updateTreeForChildTag(targetTreeId, childTag.getId());
            }
            // Update tree for tag
            tagTreeRelationDao.updateTreeForChildTag(targetTreeId, tag.getId());
        }
        return tag;
    }

    // Delete a tag
    public String deleteTag(Integer tagId, Integer userId) throws TagException {
        if (tagTreeRelationDao.findRelationsByParentTagId(tagId).length == 0 && treeService.findByFirstTagId(tagId, userId) == null) {
            ProblemTagRelation[] problemTagRelations = problemService.findPTRelationsByTagId(tagId);
            if (problemTagRelations.length == 0) problemService.deletePT(tagId);
            else {
                for (ProblemTagRelation problemTagRelation : problemTagRelations) {
                    Integer problemId = problemTagRelation.getProblemId();
                    Problem problem = problemService.findByProblemId(problemId);
                    Integer parentTagId = tagTreeRelationDao.findRelationByChildTagId(tagId).getParentTagId();
                    Tag parentTag = tagDao.findById(parentTagId);
                    problemService.createPTRelation(problem, parentTagId);
                    problemService.deletePT(problemTagRelation.getId());
                }
            }
            tagTreeRelationDao.deleteByChildTagId(tagId);
        }
        else if (tagTreeRelationDao.findRelationsByParentTagId(tagId).length == 0 && treeService.findByFirstTagId(tagId, userId) != null){
            ProblemTagRelation[] problemTagRelations = problemService.findPTRelationsByTagId(tagId);
            if (problemTagRelations.length == 0) problemService.deletePT(tagId);
            else {
                for (ProblemTagRelation problemTagRelation : problemTagRelations) {
                    Integer problemId = problemTagRelation.getProblemId();
                    Problem problem = problemService.findByProblemId(problemId);
                    Integer parentTagId = tagTreeRelationDao.findRelationByChildTagId(tagId).getParentTagId();
                    Tag parentTag = tagDao.findById(parentTagId);
                    problemService.createPTRelation(problem, parentTagId);
                    problemService.deletePT(problemTagRelation.getId());
                }
            }
            tagTreeRelationDao.deleteByChildTagId(tagId);
            treeService.deleteTreeByFirstTagId(tagId);
        }
        else if (tagTreeRelationDao.findRelationsByParentTagId(tagId).length != 0) {
            return "CANNOT DELETE A TAG WITH CHILD TAGS";
        }
        tagDao.deleteTag(tagId);
        return "SUCCESS";
    }

    // Check whether a tag exits in database using its id
    public Boolean tagExists(Integer tagId){
        return tagDao.findById(tagId) != null;
    }

    // Check whether a tag exits in database using its object
    public Boolean tagExists(Tag tag){
        return tag != null;
    }

    // Create a new Tag-Tree relationship
    public void createTagTreeRelation(Integer treeId, Integer parentTagId, Integer childTagId) {
        TagTreeRelation tagTreeRelation = new TagTreeRelation();
        tagTreeRelation.setTreeId(treeId);
        tagTreeRelation.setChildTagId(childTagId);
        tagTreeRelation.setParentTagId(parentTagId);
        tagTreeRelationDao.insert(tagTreeRelation);
    }

    // Retrieve/find a tag-tree relationship by either a parent tag's id or a child tag's id
    public TagTreeRelation findRelationsByAnyTagId(Integer tagId) {
        return tagTreeRelationDao.findRelationByAnyTagId(tagId);
    }

    // Retrieve/find a tag-tree relationship by a child tag's id
    public TagTreeRelation findRelationByChildTagId(Integer tagId) {
        return tagTreeRelationDao.findRelationByChildTagId(tagId);
    }

    // Retrieve/find all tag-tree relationships by a parent tag (all nested tags under a parent tag)
    public TagTreeRelation[] findRelationsByParentTagId(Integer tagId) {
        return tagTreeRelationDao.findRelationsByParentTagId(tagId);
    }

    // Update the tree for a child tag (updating a tag-tree relationship)
    public void updateTreeForChildTag(Integer treeId, Integer childTagId) {
        tagTreeRelationDao.updateTreeForChildTag(treeId, childTagId);
    }

    // Update the parent tag of a tag
    public void updateParentForChildTag(Integer parentTagId, Integer childTagId) {
        tagTreeRelationDao.updateParentForChildTag(parentTagId, childTagId);
    }

    // Delete a tag-tree relationship using the child tag's id
    public void deleteTagTreeRelationByChildTagId(Integer tagId) {
        tagTreeRelationDao.deleteByChildTagId(tagId);
    }








}
