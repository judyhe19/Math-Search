// Problem Service class - contains all methods for realizing problem-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.ProblemDao;
import com.yanqihe.mathsearch.dao.ProblemRelationsDao;
import com.yanqihe.mathsearch.dao.ProblemSetDao;
import com.yanqihe.mathsearch.dao.TagDao;
import com.yanqihe.mathsearch.domain.*;
import com.yanqihe.mathsearch.enums.QuestionFailureCode;
import com.yanqihe.mathsearch.enums.TagFailureCode;
import com.yanqihe.mathsearch.exception.ProblemException;
import com.yanqihe.mathsearch.exception.TagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProblemService {

    @Autowired
    private ProblemDao problemDao;
    @Autowired
    private ProblemRelationsDao problemRelationsDao;
    @Autowired
    private ProblemSetDao problemSetDao;
    @Autowired
    private TagDao tagDao;


    // Methods for managing Problems
    // Create a new problem
    public void createProblem(Problem problem) throws ProblemException {
        Problem byQuestion = problemDao.findProblemByQuestionText(problem.getQuestionText(), problem.getOwnerId());
        if (byQuestion != null) {
            throw new ProblemException(QuestionFailureCode.QUESTION_EXISTS.getCode(), QuestionFailureCode.QUESTION_EXISTS.getMessage());
        }
        else {
            problemDao.insert(problem);
        }

    }
    // Retrieve/find a problem by its id
    public Problem findByProblemId(Integer problemId) {
        return problemDao.findByProblemId(problemId);
    }
    // Retrieve/find all problems related to an owner (teacher/user) id
    public Problem[] findProblemsByOwnerId(Integer id) {
        return problemDao.findProblemsByOwnerId(id);
    }
    // Retrieve/find a problem by its question text
    public Problem findProblemByQuestionText(String text, Integer ownerId) {
        return problemDao.findProblemByQuestionText(text, ownerId);
    }
    // Search for all problems with certain question text and owner (teacher/user) id
    public Problem[] fullTextSearchByQuestionText(String text, Integer ownerId) {
        return problemDao.fullTextSearchByQuestionText(text, ownerId);
    }
    // Search for all problems with certain question text and owner (teacher/user) id under a specific tag
    public Problem[] searchUnderTagByQuestionText(String text, Integer tagId) {
        ProblemTagRelation[] problemTagRelations = problemRelationsDao.findPTRelationsByTagId(tagId);
        ArrayList<Problem> problems = new ArrayList<>();
        for (ProblemTagRelation relation : problemTagRelations) {
            Integer problemId = relation.getProblemId();
            Problem problem = problemDao.findByProblemId(problemId);
            if (problem.getQuestionText().contains(text)) {
                problems.add(problem);
            }

        }

        return problems.toArray(new Problem[0]);
    }
    // Update a problem
    public void updateProblem(Problem problem) {
        problemDao.updateProblem(problem);
    }
    // Delete a problem
    public void deleteProblem(Integer id) {
        ProblemTagRelation problemTagRelation = problemRelationsDao.findPTRelationByProblemId(id);
        ProblemProblemSetRelation problemProblemSetRelation = problemRelationsDao.findPPSRelationByProblemId(id);
        if (problemTagRelation != null) {
            problemRelationsDao.deleteProblemUnderTag(problemTagRelation);
        }
        if (problemProblemSetRelation != null) {
            problemRelationsDao.deleteProblemInSet(problemProblemSetRelation);
        }
        problemDao.deleteProblem(id);
    }
    // Check whether a problem exists in the database
    public Boolean problemExist(Problem problem) {
        return problemDao.findByProblemId(problem.getId()) != null;
    }


    // Methods for managing Problem-Problem Set (PPS) Relationships
    // Create a new PPS relationship
    public void createPPSRelation(Problem problem, Integer setId) {
        ProblemProblemSetRelation problemProblemSetRelation = new ProblemProblemSetRelation();
        if (problem.getId() == null) {
            Integer problemId = problemDao.findProblemByQuestionText(problem.getQuestionText(), problem.getOwnerId()).getId();
            problemProblemSetRelation.setProblemId(problemId);
        }
        else {
            problemProblemSetRelation.setProblemId(problem.getId());
        }
        problemProblemSetRelation.setSetId(setId);
        problemRelationsDao.insertPPSRelation(problemProblemSetRelation);
    }
    // Retrieve/find all problems in a problem set using PPS relationships
    public Problem[] getProblemsInSet(ProblemSet problemSet) {
        ProblemProblemSetRelation[] ppsRelations = problemRelationsDao.getProblemsInSet(problemSet.getId());
        Problem[] problems = new Problem[ppsRelations.length];
        for (int i = 0; i < ppsRelations.length; i++) {
            problems[i] = problemDao.findByProblemId(ppsRelations[i].getProblemId());
        }
        return problems;
    }
    // Update the problem set of a problem (update PPS relationships)
    public void updateSetForProblem(ProblemProblemSetRelation problemProblemSetRelation) {
        problemRelationsDao.updateSetForProblem(problemProblemSetRelation);
    }
    // Delete a problem in a problem set (delete PPS relationship)
    public void deleteProblemInSet(ProblemProblemSetRelation problemProblemSetRelation) {
        problemRelationsDao.deleteProblemInSet(problemProblemSetRelation);
    }
    // Retrieve/find a PPS relationship by its id
    public ProblemProblemSetRelation findPPSRelationById(Integer id) {
        return problemRelationsDao.findPPSRelationById(id);
    }
    // Retrieve/find a PPS relationship by the problem's id
    public ProblemProblemSetRelation findPPSRelationByProblemId(Integer problemId) {
        return problemRelationsDao.findPPSRelationByProblemId(problemId);
    }
    // Retrieve/find a PPS relationship by the problem set's id
    public ProblemProblemSetRelation[] findPPSRelationsBySetId(Integer setId){
        return problemRelationsDao.findPPSRelationsBySetId(setId);
    }
    // Delete a PPS relationship
    public void deletePPS(Integer id){
        problemRelationsDao.deletePPS(id);
    }


    // Methods for managing Problem-Tag (PT) Relationships
    // Create a new PT relationship
    public void createPTRelation(Problem problem, Integer tagId) throws TagException {
        ProblemTagRelation problemTagRelation = new ProblemTagRelation();
        if (tagDao.findById(tagId) == null) {
            throw new TagException(TagFailureCode.TAG_NOT_EXIST.getCode(), TagFailureCode.TAG_NOT_EXIST.getMessage());
        }
        if (problem.getId() == null) {
            Integer problemId = problemDao.findProblemByQuestionText(problem.getQuestionText(), problem.getOwnerId()).getId();
            problemTagRelation.setProblemId(problemId);
        }
        else {
            problemTagRelation.setProblemId(problem.getId());
        }
        problemTagRelation.setTagId(tagId);
        problemRelationsDao.insertPTRelation(problemTagRelation);
    }
    // Retrieve/find all problems related to a tag using PT relationships
    public ArrayList<Problem> getProblemsUnderTag(Tag tag) {
        ProblemTagRelation[] problemTagRelations = problemRelationsDao.getProblemsUnderTag(tag.getId());
        ArrayList<Problem> problems = new ArrayList<Problem>();
        for (ProblemTagRelation relation : problemTagRelations) {
            problems.add(problemDao.findByProblemId(relation.getProblemId()));
        }
        return problems;
    }
    // Update the tag of a problem (update a PT relationship)
    public void updateTagForProblem(ProblemTagRelation problemTagRelation) {
        problemRelationsDao.updateTagForProblem(problemTagRelation);
    }
    // Delete a problem related to a tag (delete a PT relationship)
    public void deleteProblemUnderTag(ProblemTagRelation problemTagRelation) {
        problemRelationsDao.deleteProblemUnderTag(problemTagRelation);

    }
    // Retrieve/find a PT relationship by its id
    public ProblemTagRelation findPTRelationById(Integer id) {
        return problemRelationsDao.findPTRelationById(id);
    }
    // Retrieve/find a PT relationship by the problem's id
    public ProblemTagRelation findPTRelationByProblemId(Integer problemId) {
        return problemRelationsDao.findPTRelationByProblemId(problemId);
    }
    // Retrieve/find a PT relationship by the tag's id
    public ProblemTagRelation[] findPTRelationsByTagId(Integer tagId) {
        return problemRelationsDao.findPTRelationsByTagId(tagId);
    }
    // Delete a PT relationship
    public void deletePT(Integer id){
        problemRelationsDao.deletePT(id);
    }
}

