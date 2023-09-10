// Authentication Service class - contains all methods for realizing authentication-related services
package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.InvitationDao;
import com.yanqihe.mathsearch.domain.InvitationCode;
import com.yanqihe.mathsearch.domain.User;
import com.yanqihe.mathsearch.enums.UserFailureCode;
import com.yanqihe.mathsearch.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;


@Service
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private InvitationDao invitationDao;

    // Method for checking whether a user is current logged in - retrieving the user object from database by their email and token and checking whether their token expiration date attribute has passed the current time
    public Boolean hasValidCredential(String email, String token) {
        // retrieving the user object by their email and token
        User userByEmailAndToken = userService.getUserByEmailAndToken(email, token);
        // checking whether the token expiration date attribute is passed the current time
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return userByEmailAndToken != null && currentTime.before(userByEmailAndToken.getTokenExpire());
    }

    // Method for checking whether an invitation code is valid - retrieving the invitation code object database by its code and checking whether its expiration date attribute has passed the current time
    public Boolean validateInvitationCode(String code) {
        // retrieving the invitation code object database by its code
        InvitationCode getByCode = invitationDao.findByCode(code);
        //  checking whether its expiration date attribute has passed the current time
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if (getByCode == null || getByCode.getInvitationExpire().before(currentTime)) {
            return false;
        }
        return true;
    }

    // Method for deleting invalid invitation codes in the database
    public void deleteInvalidInvitations() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        invitationDao.deleteCodes(currentTime.toString().split(" ")[0]);
    }

    // Method for creating a new invitation code that is valid for 10 minutes and inserting it into the database
    public String createNewInvitationCode(Integer adminId) {
        InvitationCode invitationCode = new InvitationCode();
        String code = UUID.randomUUID().toString();
        Timestamp expire = Timestamp.valueOf(
                new Timestamp(System.currentTimeMillis())
                        .toLocalDateTime()
                        .plusMinutes(10));
        invitationCode.setInvitationCode(code);
        invitationCode.setInvitationExpire(expire);
        invitationCode.setAdminId(adminId);
        invitationDao.insert(invitationCode);
        return code;
    }

    // Method for logging in a user by checking their email and password - update the user's token and token expiration date if user is valid
    public String login(String email, String password) throws AuthException {
        User userByEmail = userService.getUserByEmail(email);
        if (userByEmail.getPassword().equalsIgnoreCase(password) &&
                userByEmail.getEmail().equalsIgnoreCase(email)) {

            String token = UUID.randomUUID().toString();
            Timestamp expire = Timestamp.valueOf(
                    new Timestamp(System.currentTimeMillis())
                    .toLocalDateTime()
                    .plusYears(1));

            userByEmail.setTokenExpire(expire);
            userByEmail.setToken(token);
            userService.updateLoginInfo(userByEmail);
            return token;
        } else {
            throw new AuthException(UserFailureCode.AUTH_FAILURE.getCode(), UserFailureCode.AUTH_FAILURE.getMessage());
        }
    }

    // Method for logging out a user by updating the token expiration date of their token to expire (equal to current time)
    public void setExpire(User user) {
        Timestamp expire = Timestamp.valueOf(
                new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        user.setTokenExpire(expire);
        userService.updateLoginInfo(user);
    }
}
