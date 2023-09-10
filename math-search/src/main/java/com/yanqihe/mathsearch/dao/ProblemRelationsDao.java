// Problem Relations DAO (data access object) interface - contains all necessary queries for problem relation-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.ProblemProblemSetRelation;
import com.yanqihe.mathsearch.domain.ProblemTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProblemRelationsDao {

    // Queries for managing Problem-Problem set relation

    // Query for inserting a new Problem-Problem set relation to the database
    int insertPPSRelation(ProblemProblemSetRelation problemProblemSetRelation);
    // Query for retrieving Problem-Problem set relations from the database by problem set id
    ProblemProblemSetRelation[] getProblemsInSet (@Param("setId") Integer setId);
    // Query for retrieving a Problem-Problem set relation to the database by its id
    ProblemProblemSetRelation findPPSRelationById (@Param("id") Integer id);
    // Query for retrieving a Problem-Problem set relation to the database by its problem id
    ProblemProblemSetRelation findPPSRelationByProblemId (@Param("problemId") Integer problemId);
    // Query for retrieving Problem-Problem set relations from the database by problem set id
    ProblemProblemSetRelation[] findPPSRelationsBySetId(@Param("setId") Integer setId);
    // Query for updating the problem set for a problem in the database
    void updateSetForProblem(ProblemProblemSetRelation problemProblemSetRelation);
    // Query for deleting a problem in the problem set in the database
    void deleteProblemInSet(ProblemProblemSetRelation problemProblemSetRelation);
    // Query for deleting a Problem-Problem set relation in the database by its id
    void deletePPS(@Param("id") Integer id);


    // Queries for managing Problem-Tag relation

    // Query for inserting a new Problem-Tag relation to the database
    int insertPTRelation(ProblemTagRelation problemTagRelation);
    // Query for retrieving Problem-Tag relations from the database by tag id
    ProblemTagRelation[] getProblemsUnderTag (@Param("tagId") Integer tagId);
    // Query for retrieving a Problem-Tag relation to the database by its id
    ProblemTagRelation findPTRelationById (@Param("id") Integer id);
    // Query for retrieving a Problem-Tag relation to the database by its problem id
    ProblemTagRelation findPTRelationByProblemId (@Param("problemId") Integer problemId);
    // Query for retrieving Problem-Tag relations from the database by tag id
    ProblemTagRelation[] findPTRelationsByTagId(@Param("tagId") Integer tagId);
    // Query for updating the tag for a problem in the database
    void updateTagForProblem(ProblemTagRelation problemTagRelation);
    // Query for deleting a problem under a tag in the database
    void deleteProblemUnderTag (ProblemTagRelation problemTagRelation);
    // Query for deleting a Problem-Tag relation in the database by its id
    void deletePT(@Param("id") Integer id);
}
