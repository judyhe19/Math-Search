// Tag-Tree Relationship DAO (data access object) interface - contains all necessary queries for Tag-Tree relation-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.TagTreeRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TagTreeRelationDao {
    // Mybatis mapping - XML approach

    // Query for inserting a new Tag-tree relation to the database
    int insert(TagTreeRelation tagTreeRelation);
    // Query for retrieving a Tag-tree relation from the database by tag id
    TagTreeRelation findRelationByAnyTagId(@Param("tagId") Integer tagId);
    // Query for retrieving all tags related to a parent tag from the database (all nested tags under a tag)
    TagTreeRelation[] findRelationsByParentTagId (@Param("tagId") Integer tagId);
    // Query for retrieving the parent tag of a tag from the database
    TagTreeRelation findRelationByChildTagId (@Param("tagId") Integer tagId);
    // Query for updating the tag tree of a child tag in the database (when a tag becomes moved and nested under another tag that is under a different root tag)
    void updateTreeForChildTag(@Param("treeId") Integer treeId, @Param("childTagId") Integer childTagId);
    // Query for updating the parent tag of a tag in the database (when a tag becomes moved and nested under another tag)
    void updateParentForChildTag(@Param("parentTagId") Integer parentTagId, @Param("childTagId") Integer childTagId);
    // Query for deleting a tag in the database
    void deleteByChildTagId(@Param("tagId") Integer tagId);
}
