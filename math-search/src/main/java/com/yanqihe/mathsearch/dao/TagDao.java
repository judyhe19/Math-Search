// Tag DAO (data access object) interface - contains all necessary queries for tag-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.Tag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TagDao {
    // Mybatis mapping - XML approach
    // Query for inserting a new tag to the database
    int insert(Tag tag);
    // Query for retrieving a tag by its id from the database
    Tag findById(@Param("id") Integer id);
    // Query for retrieving all tags related to a user id from the database (all tags created by a user)
    Tag[] findTagsByUserId(@Param("userId") Integer userId);
    // Query for retrieving a tag by its name and owner (teacher/user) id from the database
    Tag findByName(@Param("name") String name, @Param("userId") Integer userId);
    // Query for searching for a tag in the database by its name and owner (teacher/user) id
    Tag[] fullTextSearchByNameAndUserId(@Param("name") String name, @Param("userId") Integer userId);
    // Query for updating the name of a tag in the database
    void updateTag(Tag tag);

    // Mybatis mapping - Annotations approach
    // Query for deleting a tag in the database
    @Delete("delete from `Tags` where id = #{id};")
    void deleteTag(@Param("id") Integer id);
}
