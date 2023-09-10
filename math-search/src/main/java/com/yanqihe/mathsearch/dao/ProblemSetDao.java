// Problem Set DAO (data access object) interface - contains all necessary queries for problem set-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.ProblemSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProblemSetDao {

    // Mybatis mapping - XML approach
    // Query for inserting a new problem set to the database
    int insert(ProblemSet problemSet);
    // Query for retrieving a problem set by its id from the database
    ProblemSet findById(@Param("id") Integer id);
    // Query for retrieving all problem sets related to a class id from the database (all problem sets labelled by the class)
    ProblemSet[] findProblemSetsByClassId(@Param("classId") Integer classId);
    // Query for retrieving all problem sets related to an owner (teacher) id from the database (all problem sets belonging to a user)
    ProblemSet[] getSetsByOwnerId(@Param("ownerId") Integer ownerId);
    // Query for retrieving a problem set by its name and owner (teacher) id from the database
    ProblemSet findSetByNameAndOwner(@Param("name") String name, @Param("ownerId") Integer ownerId);
    // Query for updating the student class related to a problem set in the database
    void updateClassForSet(ProblemSet problemSet);
    // Query for deleting a problem set in the database
    void delete(ProblemSet problemSet);
}
