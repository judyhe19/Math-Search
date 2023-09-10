// Invitation Code DAO (data access object) interface - contains all necessary queries for invitation code-related services

package com.yanqihe.mathsearch.dao;

import com.yanqihe.mathsearch.domain.InvitationCode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface InvitationDao {
    // Mybatis mapping - XML approach

    // Query for inserting a new invitation code to the database
    int insert(InvitationCode code);

    // Query for retrieving an invitation code by its code (for checking its expiration date)
    InvitationCode findByCode(@Param("invitationCode") String invitationCode);


    // Mybatis mapping - Annotations approach

    // Query for retrieving a valid invitation code (for registering a new user)
    @Select("SELECT invitation_code FROM Invitation_Codes WHERE DATE(invitation_expire) > #{currentTime} ORDER BY invitation_expire ASC; ")
    void getValidCode(@Param("currentTime") String currentTime);

    // Query for deleting an expired invitation code from the database
    @Delete("DELETE FROM Invitation_Codes WHERE DATE(invitation_expire) < #{currentTime};")
    void deleteCodes(@Param("currentTime") String currentTime);
}
