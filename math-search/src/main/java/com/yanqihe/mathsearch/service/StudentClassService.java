// Student Class Service class - contains all methods for realizing student class-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.StudentClassDao;
import com.yanqihe.mathsearch.domain.StudentClass;
import com.yanqihe.mathsearch.enums.StudentClassFailureCode;
import com.yanqihe.mathsearch.exception.StudentClassException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudentClassService {
    @Autowired
    private StudentClassDao studentClassDao;

    // Create a new student class
    public void createStudentClass(StudentClass studentClass) throws StudentClassException {
       StudentClass byNameAndTeacherId = studentClassDao.findByNameAndTeacherId(studentClass.getName(), studentClass.getTeacherId());
        if (byNameAndTeacherId != null) {
            throw new StudentClassException(StudentClassFailureCode.CLASS_N_EXISTS.getCode(), StudentClassFailureCode.CLASS_N_EXISTS.getMessage());
        } else {
            studentClassDao.insert(studentClass);
        }
    }

    // Retrieve/find a student class by its id
    public StudentClass findById(Integer id) {
        return studentClassDao.findById(id);
    }
    // Retrieve/find all student classes related to a teacher (user)
    public StudentClass[] findClassesByTeacherId(Integer teacherId) {
        return studentClassDao.findClassesByTeacherId(teacherId);
    }
    // Retrieve/find a student class by its name and teacher (user) id
    public StudentClass findByNameAndTeacherId(String name, Integer teacherId) {
        return studentClassDao.findByNameAndTeacherId(name, teacherId);
    }
    // Update the name of a student class
    public void updateClassName(StudentClass studentClass) {
        studentClassDao.updateClassName(studentClass);
    }
    // Delete a student class
    public void delete(StudentClass studentClass) {
        studentClassDao.delete(studentClass);
    }
    // Check if a student class exists in database
    public Boolean classExists(StudentClass studentClass) {
        return studentClassDao.findById(studentClass.getId()) != null;
    }

}
