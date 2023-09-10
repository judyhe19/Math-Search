// Problems DAO (data access object) interface - contains all necessary queries for problem-related services

package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.Problem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProblemDao {
    // Mybatis mapping - XML approach

    // Query for inserting a new problem to the database
    int insert(Problem problem);
    // Query for retrieving a problem in the database by its id
    Problem findByProblemId(@Param("id") Integer problemId);
    // Query for retrieving a problem in the database by its owner (teacher/user) id
    Problem[] findProblemsByOwnerId(@Param("ownerId") Integer ownerId);
    // Query for retrieving a problem in the database by its question text and owner (teacher/user) id
    Problem findProblemByQuestionText(@Param("questionText") String questionText, @Param("ownerId") Integer ownerId);
    // Query for searching for a problem in the database by its question text and owner (teacher/user) id
    Problem[] fullTextSearchByQuestionText(@Param("questionText") String questionText, @Param("ownerId") Integer ownerId);
    void updateProblem(Problem problem);

    // Mybatis mapping - Annotations approach

    // Query for deleting a problem in the database by its id
    @Delete("delete from `Problems` where id = #{id};")
    void deleteProblem(@Param("id") Integer id);
}
