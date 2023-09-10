// User Service class - contains all methods for realizing user-related services

package com.yanqihe.mathsearch.service;

import com.yanqihe.mathsearch.dao.UserDao;
import com.yanqihe.mathsearch.domain.User;
import com.yanqihe.mathsearch.enums.UserFailureCode;
import com.yanqihe.mathsearch.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    // Create a new user (account)
    public void createAccount(User user) throws UserException {
        User byEmail = userDao.findByEmail(user.getEmail());
        if (byEmail != null) {
            throw new UserException(UserFailureCode.ACCOUNT_EXISTS.getCode(), UserFailureCode.ACCOUNT_EXISTS.getMessage());
        }

        if (user.getToken() == null) {
            user.setToken(UUID.randomUUID().toString());
        }
        userDao.insert(user);
    }

    // Retrieve/find a user by their id
    public User getUserById(Integer id) {
        return userDao.findById(id);
    }
    // Retrieve/find a user by their email and toke
    public User getUserByEmailAndToken(String email, String token) {
        return userDao.findByCred(email, token);
    }
    // Retrieve/find a user by their email
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
    // Retrieve/find a user's login information
    public void updateLoginInfo(User user) {
        userDao.updateLoginInfo(user);
    }
    // Update the password of a user
    public void updatePassword(User user) {
        userDao.updatePassword(user);
    }
    // Update the username of a user
    public void updateUsername(User user) {
        userDao.updateUsername(user);
    }
    // Delete a user
    public void deleteUser(Integer id) {
        userDao.deleteUser(id);
    }
}


