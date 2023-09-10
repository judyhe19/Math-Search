// Tree DAO (data access object) interface - contains all necessary queries for Tree-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.Tree;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TreeDao {
    // Mybatis mapping - XML approach
    // Query for inserting a new tree (root tag) to the database
    int insert(Tree tree);
    // Query for retrieving a tree (root tag) from the database by its id
    Tree findById(@Param("id") Integer id);
    // Query for retrieving all trees (root tags) of a user (teacher) from the database by the user id
    Tree[] findTreesByUserId(@Param("userId") Integer userId);
    // Query for retrieving all trees (root tags) of a user (teacher) from the database by the user id
    Tree[] getAllTrees(@Param("userId") Integer userId);
    // Query for retrieving a tree by the id of its root tag (first tag)
    Tree findByFirstTagId(@Param("firstTagId") Integer firstTagId, @Param("userId") Integer userId);
    // Query for updating the root tag (first tag) of a tree
    void updateTreeRoot(Tree tree);

    // Mybatis mapping - Annotations approach
    // Query for deleting a tree in the database by its id
    @Delete("delete from `Trees` where id = #{id};")
    void deleteTree(@Param("id") Integer id);

    // Query for deleting a tree in the database by its root (first) tag's id
    @Delete("delete from `Trees` where first_tag_id = #{id};")
    void deleteTreeByFirstTagId(@Param("id") Integer id);
}
