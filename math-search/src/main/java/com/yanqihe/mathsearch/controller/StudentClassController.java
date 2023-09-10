// Student Class Controller class - contains all available HTTP calls for student class-related services
package com.yanqihe.mathsearch.controller;

import com.yanqihe.mathsearch.common.ResultResponse;
import com.yanqihe.mathsearch.domain.Problem;
import com.yanqihe.mathsearch.domain.ProblemSet;
import com.yanqihe.mathsearch.domain.StudentClass;
import com.yanqihe.mathsearch.enums.StudentClassFailureCode;
import com.yanqihe.mathsearch.enums.UserFailureCode;
import com.yanqihe.mathsearch.exception.StudentClassException;
import com.yanqihe.mathsearch.service.ProblemService;
import com.yanqihe.mathsearch.service.ProblemSetService;
import com.yanqihe.mathsearch.service.StudentClassService;
import com.yanqihe.mathsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

// Enabling cross-origin for running of the frontend server (Reference: https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/class")
public class StudentClassController {
    @Autowired
    private StudentClassService studentClassService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProblemSetService problemSetService;

    // Add a new student class into the database (related to a specific user's id)
    @PostMapping("/add")
    public ResultResponse<StudentClass> addClass(@RequestBody StudentClass studentClass) {
        ResultResponse<StudentClass> resultResponse = new ResultResponse<>();
        if (studentClassService.findByNameAndTeacherId(studentClass.getName(), studentClass.getTeacherId()) != null) {
            resultResponse.setCode(StudentClassFailureCode.CLASS_N_EXISTS.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_N_EXISTS.getMessage());
            return resultResponse;
        }
        try {
            studentClassService.createStudentClass(studentClass);
            resultResponse.setData(studentClass);
        } catch (StudentClassException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Update a student class' name
    @PostMapping("/update/{newName}")
    public ResultResponse<StudentClass> updateClassName(@RequestBody StudentClass studentClass, @PathVariable("newName") String newName) {
        ResultResponse<StudentClass> resultResponse = new ResultResponse<>();
        if (studentClassService.classExists(studentClass)) {
            studentClass.setName(newName);
            studentClassService.updateClassName(studentClass);
            resultResponse.setData(studentClass);
        } else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Delete a student class of the user from the database
    @PostMapping("/delete")
    public ResultResponse<Boolean> deleteClass(@RequestBody StudentClass studentClass) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        if (studentClassService.classExists(studentClass)) {
            studentClassService.delete(studentClass);
            resultResponse.setData(true);
        } else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
            resultResponse.setData(false);
        }
        return resultResponse;
    }

    // Retrieve all student classes belonging to the user (teacher) from database
    @GetMapping("/getClasses/{teacherId}")
    public ResultResponse<StudentClass[]> getClassesByTeacherId(@PathVariable("teacherId") Integer teacherId) {
        ResultResponse<StudentClass[]> resultResponse = new ResultResponse<>();
        if (userService.getUserById(teacherId) != null) {
            StudentClass[] classes = studentClassService.findClassesByTeacherId(teacherId);
            resultResponse.setData(classes);
        }
        else {
            resultResponse.setCode(UserFailureCode.ACCOUNT_NOT_EXIST.getCode());
            resultResponse.setMessage(UserFailureCode.ACCOUNT_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Retrieve a student class' information (ex: name) using its id
    @GetMapping("/info/{classId}")
    public ResultResponse<StudentClass> getClassById(@PathVariable("classId") Integer classId) {
        ResultResponse<StudentClass> resultResponse = new ResultResponse<>();
        StudentClass studentClass = studentClassService.findById(classId);
        if (studentClass != null) {
            resultResponse.setData(studentClass);
        }
        else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Retrieve all problem sets labelled under a student class of the user
    @GetMapping("/findProblemSetsInClass/{classId}")
    public ResultResponse<ProblemSet[]> findProblemSetsInClass(@PathVariable("classId") Integer classId) {
        ResultResponse<ProblemSet[]> resultResponse = new ResultResponse<>();
        StudentClass studentClass = studentClassService.findById(classId);
        if (studentClass != null) {
            resultResponse.setData(problemSetService.findProblemSetsByClassId(classId));
        }
        else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Search for repeated problems across all problem sets labelled under a student class of the user
    @PostMapping("/searchForRepeatedProblemInClass/{classId}")
    public ResultResponse<ArrayList<ProblemSet>> searchForRepeatedProblemInClass(@RequestBody Problem problem, @PathVariable("classId") Integer classId) {
        ResultResponse<ArrayList<ProblemSet>> resultResponse = new ResultResponse<>();
        StudentClass studentClass = studentClassService.findById(classId);
        if (studentClass != null) {
            ProblemSet[] problemSets = problemSetService.findProblemSetsByClassId(classId);
            ArrayList<ProblemSet> repeatedProblemFoundSets = new ArrayList<>();
            for (ProblemSet problemSet : problemSets) {
                Problem[] problems = problemService.getProblemsInSet(problemSet);
                for (Problem p : problems) {
                    if (p.getQuestionText().equalsIgnoreCase(problem.getQuestionText()) && p.getOwnerId().compareTo(problem.getOwnerId()) == 0) {
                        repeatedProblemFoundSets.add(problemSet);
                    }
                }
            }
            resultResponse.setData(repeatedProblemFoundSets);
        }
        else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Add a problem set to a student class of the user - the user may label a set of problems by a class.
    @PostMapping("/addProblemSet/{classId}")
    public ResultResponse<Boolean> addProblemSet(@RequestBody ProblemSet problemSet, @PathVariable("classId") Integer classId) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        StudentClass studentClass = studentClassService.findById(classId);
        if (studentClass != null) {
            problemSet.setClassId(classId);
            problemSetService.updateClassForSet(problemSet);
            resultResponse.setData(true);
        }
        else {
            resultResponse.setCode(StudentClassFailureCode.CLASS_NOT_EXIST.getCode());
            resultResponse.setMessage(StudentClassFailureCode.CLASS_NOT_EXIST.getMessage());
            resultResponse.setData(false);

        }
        return resultResponse;
    }

}
