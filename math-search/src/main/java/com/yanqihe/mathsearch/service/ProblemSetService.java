// Problem Set Service class - contains all methods for realizing problem set-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.ProblemSetDao;
import com.yanqihe.mathsearch.domain.ProblemSet;
import com.yanqihe.mathsearch.enums.QuestionFailureCode;
import com.yanqihe.mathsearch.exception.ProblemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProblemSetService {
    @Autowired
    private ProblemSetDao problemSetDao;

    // Create a new problem set
    public ProblemSet createProblemSet(ProblemSet problemSet) throws ProblemException {
        ProblemSet byNameAndOwner = problemSetDao.findSetByNameAndOwner(problemSet.getName(), problemSet.getOwnerId());
        if (byNameAndOwner != null) {
            throw new ProblemException(QuestionFailureCode.PS_EXISTS.getCode(), QuestionFailureCode.PS_EXISTS.getMessage());
        }
        else {
            problemSetDao.insert(problemSet);
        }
        return problemSetDao.findSetByNameAndOwner(problemSet.getName(), problemSet.getOwnerId());
    }
    // Retrieve/find a problem set by its id
    public ProblemSet findById(Integer id) {
        return problemSetDao.findById(id);
    }
    // Retrieve/find all problem sets related to a student class
    public ProblemSet[] findProblemSetsByClassId (Integer classId) {
        return problemSetDao.findProblemSetsByClassId(classId);
    }
    // Retrieve/find all problem sets related to a user (teacher)
    public ProblemSet[] getSetsByOwnerId (Integer ownerId) {
        return problemSetDao.getSetsByOwnerId(ownerId);
    }
    // Retrieve/find a problem set by its name and owner (teacher/user) id
    public ProblemSet findSetByNameAndOwner (String name, Integer ownerId) {
        return problemSetDao.findSetByNameAndOwner(name, ownerId);
    }
    // Update the student class related to a problem set
    public void updateClassForSet(ProblemSet problemSet) {
        problemSetDao.updateClassForSet(problemSet);
    }
    // Delete a problem set
    public void delete(ProblemSet problemSet) {
        problemSetDao.delete(problemSet);
    }

}
