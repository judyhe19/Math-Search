// Problem Controller class - contains all available HTTP calls for problem-related services
package com.yanqihe.mathsearch.controller;

import com.yanqihe.mathsearch.common.ResultResponse;
import com.yanqihe.mathsearch.domain.*;
import com.yanqihe.mathsearch.enums.QuestionFailureCode;
import com.yanqihe.mathsearch.exception.ProblemException;
import com.yanqihe.mathsearch.exception.TagException;
import com.yanqihe.mathsearch.service.ProblemService;
import com.yanqihe.mathsearch.service.ProblemSetService;
import com.yanqihe.mathsearch.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

// Enabling cross-origin for running of the frontend server (Reference: https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/problem")
public class ProblemController {
    @Autowired
    private ProblemService problemService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ProblemSetService problemSetService;

    // Add (create) a new problem to the database under a specific tag (related to a user's id)
    @PostMapping("/add/{tagId}")
    public ResultResponse<Problem> addProblem(@RequestBody Problem problem, @PathVariable("tagId") Integer tagId) {
        ResultResponse<Problem> resultResponse = new ResultResponse<>();
        try {
            // Call service method for creating a new problem
            problemService.createProblem(problem);
            if (tagId == null) {
                resultResponse.setCode(QuestionFailureCode.NO_TAG_SELECTED.getCode());
                resultResponse.setMessage(QuestionFailureCode.NO_TAG_SELECTED.getMessage());
                return resultResponse;
            }
            else {
                problemService.createPTRelation(problem, tagId);
            }
            resultResponse.setData(problem);
        } catch (ProblemException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        } catch (TagException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Update a problem in the database, including its information and tag
    @PostMapping("/update/{newTagId}") // Must obtain the id of the problem first.
    public ResultResponse<Problem> updateProblem(@RequestBody Problem problem, @PathVariable("newTagId") Integer tagId) {
        ResultResponse<Problem> resultResponse = new ResultResponse<>();
        if (problemService.problemExist(problem)) {
            ProblemTagRelation ppt = new ProblemTagRelation();
            ppt.setProblemId(problem.getId());
            ppt.setTagId(tagId);
            problemService.updateProblem(problem);
            problemService.updateTagForProblem(ppt);
            resultResponse.setData(problem);
        } else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Delete a problem in the database
    @PostMapping("/delete") // Must obtain the id of the problem first.
    public ResultResponse<Boolean> deleteProblem(@RequestBody Problem problem) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        if (problemService.problemExist(problem)) {
            problemService.deleteProblem(problem.getId());
            resultResponse.setData(true);
        } else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
            resultResponse.setData(false);
        }
        return resultResponse;
    }

    // Retrieve a problem's information by its question text and owner id
    // Primarily used to obtain the id of a problem
    @PostMapping("/findByQuestionAndOwner")
    public ResultResponse<Problem> findByQuestion(@RequestBody Problem problem) {
        ResultResponse<Problem> resultResponse = new ResultResponse<>();
        Problem p = problemService.findProblemByQuestionText(problem.getQuestionText(), problem.getOwnerId());
        if (p != null) {
            resultResponse.setData(problemService.findProblemByQuestionText(problem.getQuestionText(), problem.getOwnerId()));
        }
        else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
        }

        return resultResponse;
    }

    // Retrieve a problem's information by its id (Must obtain the id of the problem first).
    @GetMapping("/selectProblem/{problemId}")
    public ResultResponse<Problem> selectProblemById(@PathVariable("problemId") Integer problemId) {
        ResultResponse<Problem> resultResponse = new ResultResponse<>();
        Problem p = problemService.findByProblemId(problemId);
        if (p != null) {
            resultResponse.setData(p);
        }
        else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Retrieve a problem's tag using its id (Must obtain the id of the problem first).
    @GetMapping("/getTagOfProblem/{problemId}")
    public ResultResponse<Tag> getTagOfProblem(@PathVariable("problemId") Integer problemId) {
        ResultResponse<Tag> resultResponse = new ResultResponse<>();
        ProblemTagRelation p = problemService.findPTRelationByProblemId(problemId);
        Tag t = tagService.findById(p.getTagId());
        if (t != null) {
            resultResponse.setData(t);
        }
        else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
        }
        return resultResponse;
    }

    // Search for problems belonging to a user by question text
    @GetMapping("/searchALL/{ownerId}/{text}")
    public ResultResponse<Problem[]> searchALLProblemsByText(@PathVariable("text") String text, @PathVariable("ownerId") Integer ownerId) {
        ResultResponse<Problem[]> resultResponse = new ResultResponse<>();
        resultResponse.setData(problemService.fullTextSearchByQuestionText(text, ownerId));
        return resultResponse;
    }

    // Search problems under a specific tag, including in its nested tags, if any.
    @GetMapping("/searchUnderTag/{tagId}/{text}")
    public ResultResponse<Problem[]> searchUnderTag(@PathVariable("text") String text, @PathVariable("tagId") Integer tagId) {
        ResultResponse<Problem[]> resultResponse = new ResultResponse<>();
        resultResponse.setData(problemService.searchUnderTagByQuestionText(text, tagId));
        return resultResponse;
    }

    // Retrieve all problems under a tag, including those under its nested tags (if any)
    @PostMapping("/getProblemsUnderTag")
    public ResultResponse<Problem[]> getProblemsUnderTag(@RequestBody Tag tag) {
        ResultResponse<Problem[]> resultResponse = new ResultResponse<>();
        ArrayList<Problem> problems = new ArrayList<>();
        // check for child tags
        TagTreeRelation[] childTagsRelations = tagService.findRelationsByParentTagId(tag.getId());
        if (childTagsRelations.length > 0) {
            for (TagTreeRelation childTagsRelation : childTagsRelations) {
                Integer childTagId = childTagsRelation.getChildTagId();
                Tag childTag = tagService.findById(childTagId);
                problems.addAll(Arrays.asList(getProblemsUnderTag(childTag).getData()));
            }
        }
        problems.addAll(problemService.getProblemsUnderTag(tag));
        resultResponse.setData(problems.toArray(new Problem[0]));
        return resultResponse;
    }

    // Create a new problem set
    @PostMapping("/createProblemSet")
    public ResultResponse<ProblemSet> createProblemSet(@RequestBody ProblemSet problemSet) {
        ResultResponse<ProblemSet> resultResponse = new ResultResponse<>();
        try {
            problemSetService.createProblemSet(problemSet);
            resultResponse.setData(problemSet);
        } catch (ProblemException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Retrieve all problem sets belonging to a user
    @GetMapping("/getProblemSets/{ownerId}")
    public ResultResponse<ProblemSet[]> getProblemsInSet(@PathVariable("ownerId") Integer ownerId) {
        ResultResponse<ProblemSet[]> resultResponse = new ResultResponse<>();
        resultResponse.setData(problemSetService.getSetsByOwnerId(ownerId));
        return resultResponse;
    }

    // Retrieve all problems in a problem set
    @PostMapping("/getProblemsInSet")
    public ResultResponse<Problem[]> getProblemsInSet(@RequestBody ProblemSet problemSet) {
        ResultResponse<Problem[]> resultResponse = new ResultResponse<>();
        resultResponse.setData(problemService.getProblemsInSet(problemSet));
        return resultResponse;
    }

    // Add a problem to a problem set (Must obtain the id of the problem first).
    @PostMapping("/addToProblemSet/{setId}")
    public ResultResponse<Problem[]> addToProblemSet(@RequestBody Problem problem, @PathVariable("setId") Integer setId) {
        ResultResponse<Problem[]> resultResponse = new ResultResponse<>();
        if (problemService.problemExist(problem)) {
            if (problemSetService.findById(setId) != null) {
                problemService.createPPSRelation(problem, setId);
                ProblemSet ps = problemSetService.findById(setId);
                resultResponse.setData(problemService.getProblemsInSet(ps));
            }
            else {
                resultResponse.setCode(QuestionFailureCode.PS_NOT_EXISTS.getCode());
                resultResponse.setMessage(QuestionFailureCode.PS_NOT_EXISTS.getMessage());
            }
        }
        else {
            resultResponse.setCode(QuestionFailureCode.QUESTION_NOT_EXIST.getCode());
            resultResponse.setMessage(QuestionFailureCode.QUESTION_NOT_EXIST.getMessage());
        }

        return resultResponse;
    }


}