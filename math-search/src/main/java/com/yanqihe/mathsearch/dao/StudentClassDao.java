// Student classes DAO (data access object) interface - contains all necessary queries for student class-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.StudentClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentClassDao {
    // Mybatis mapping - XML approach
    // Query for inserting a new student class to the database
    int insert(StudentClass studentClass);
    // Query for retrieving a student class by its id from the database
    StudentClass findById(@Param("id") Integer id);
    // Query for retrieving all student classes related to a teacher id from the database (all classes created by a user)
    StudentClass[] findClassesByTeacherId(@Param("teacherId") Integer teacherId);
    // Query for retrieving a student class by its name and teacher id from the database
    StudentClass findByNameAndTeacherId(@Param("name") String name, @Param("teacherId") Integer teacherId);
    // Query for updating the name of a student class in the database
    void updateClassName(StudentClass studentClass);
    // Query for deleting a student class in the database
    void delete(StudentClass studentClass);
}
