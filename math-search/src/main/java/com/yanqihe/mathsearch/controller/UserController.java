// User Controller class - contains all available HTTP calls for user-related services
package com.yanqihe.mathsearch.controller;

import com.yanqihe.mathsearch.common.ResultResponse;
import com.yanqihe.mathsearch.domain.User;
import com.yanqihe.mathsearch.exception.UserException;
import com.yanqihe.mathsearch.service.AuthService;
import com.yanqihe.mathsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.yanqihe.mathsearch.enums.UserFailureCode.NO_VALID_ACCOUNT;

// Enabling cross-origin for running of the frontend server (Reference: https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    // Register a new user in the database
    @PostMapping("/register")
    public ResultResponse<User> registerUser(@RequestBody User user) {
        ResultResponse<User> resultResponse = new ResultResponse<>();
        try {
            userService.createAccount(user);
            resultResponse.setData(user);
        } catch (UserException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Retrieving the information of a user by their email (id, username, password, account type, token, token expiration date)
    @GetMapping("/infoEmail/{email}")
    public ResultResponse<User> getByEmail(@PathVariable("email") String email) {
        ResultResponse<User> resultResponse = new ResultResponse<>();
        resultResponse.setData(userService.getUserByEmail(email));
        return resultResponse;
    }

    // Retrieving the information of a user by their id (id, username, password, account type, token, token expiration date)
    @GetMapping("/infoId/{id}")
    public ResultResponse<User> getById(@PathVariable("id") Integer id) {
        ResultResponse<User> resultResponse = new ResultResponse<>();
        resultResponse.setData(userService.getUserById(id));
        return resultResponse;
    }

    // Resetting (updating) the password of a user in the database
    @PostMapping("/resetPassword/{newPassword}")
    public ResultResponse<Boolean> resetPassword(@RequestBody User inUser, @PathVariable("newPassword") String newPassword) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        User u = userService.getUserByEmail(inUser.getEmail());
        if (u != null) {
            u.setPassword(newPassword);
            userService.updatePassword(u);
            // set tokenExpire to force user to login again after changing their password
            authService.setExpire(u);
            resultResponse.setData(true);
        }
        else {
            resultResponse.setCode(NO_VALID_ACCOUNT.getCode());
            resultResponse.setMessage(NO_VALID_ACCOUNT.getMessage());
            resultResponse.setData(false);
        }
        return resultResponse;
    }

    // Deleting a user in the database
    @PostMapping("/delete")
    public ResultResponse<Boolean> deleteUser(@RequestBody User user)  {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        User byId = userService.getUserById(user.getId());
        if (byId != null) {
            userService.deleteUser(user.getId());
            resultResponse.setData(true);
        } else {
            resultResponse.setCode(NO_VALID_ACCOUNT.getCode());
            resultResponse.setMessage(NO_VALID_ACCOUNT.getMessage());
            resultResponse.setData(false);
        }
        return resultResponse;
    }
}


