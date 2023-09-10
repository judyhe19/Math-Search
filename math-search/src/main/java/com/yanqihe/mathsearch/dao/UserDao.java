// User DAO (data access object) interface
// - contains all necessary queries for User-related services
package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {
    // Mybatis mapping - XML approach
    // Query for inserting a new user (register) to the database
    int insert(User user);
    // Query for retrieving a user from the database by the user's id
    User findById(@Param("id") Integer id);
    // Query for retrieving a user from the database by the user's token and email
    User findByCred(@Param("email") String email, @Param("token") String token);
    // Query for retrieving a user from the database by the user's email
    User findByEmail(@Param("email") String email);
    // Query for updating the information of a user
    void updateLoginInfo(User user);
    // Query for updating the password of a user
    void updatePassword(User user);
    // Query for updating the username of a user
    void updateUsername(User user);

    // Mybatis mapping - Annotations approach
    // Query for deleting a user from the database
    @Delete("delete from `Users` where id = #{id};")
    void deleteUser(@Param("id") Integer id);
}


