// Authentication Controller class - contains all available HTTP calls for authentication-related services
package com.yanqihe.mathsearch.controller;

import com.yanqihe.mathsearch.common.ResultResponse;
import com.yanqihe.mathsearch.domain.User;
import com.yanqihe.mathsearch.enums.UserFailureCode;
import com.yanqihe.mathsearch.exception.AuthException;
import com.yanqihe.mathsearch.service.AuthService;
import com.yanqihe.mathsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Enabling cross-origin for running of the frontend server (Reference: https://howtodoinjava.com/spring-boot2/spring-cors-configuration/)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserController userController;

    // Authenticating a user - indicating whether the user is currently logged in by checking whether their token is valid (token expiration date should not be passed)
    @PostMapping("/doAuth")
    public ResultResponse<Boolean> auth(@RequestBody User user) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();
        resultResponse.setData(authService.hasValidCredential(user.getEmail(), user.getToken()));
        return resultResponse;
    }

    // Validate an invitation code that may be used to register a new user by checking whether its expiration date has passed
    @GetMapping("/validateInvitationCode/{invitationCode}")
    public ResultResponse<Boolean> validateInvitationCode(@PathVariable("invitationCode") String invitationCode) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();

        if (!authService.validateInvitationCode(invitationCode)) {
            resultResponse.setCode(UserFailureCode.INVALID_INVITATION.getCode());
            resultResponse.setMessage(UserFailureCode.INVALID_INVITATION.getMessage());
        }
        else {
            resultResponse.setData(authService.validateInvitationCode(invitationCode));
        }
        return resultResponse;
    }

    // Obtaining a new invitation code valid for 10 minutes
    @PostMapping("/getNewInvitationCode")
    public ResultResponse<String> getNewInvitationCode(@RequestBody User admin) {
        ResultResponse<String> resultResponse = new ResultResponse<>();
        if (!authService.hasValidCredential(admin.getEmail(), admin.getToken())) {
            resultResponse.setCode(UserFailureCode.ACCOUNT_LOGOUT.getCode());
            resultResponse.setMessage(UserFailureCode.ACCOUNT_LOGOUT.getMessage());
        }
        if (admin.getAccountType().equalsIgnoreCase("ADMIN")) {
            String code = authService.createNewInvitationCode(admin.getId());
            resultResponse.setData(code);
        }
        else {
            resultResponse.setCode(UserFailureCode.NOT_ADMIN.getCode());
            resultResponse.setMessage(UserFailureCode.NOT_ADMIN.getMessage());
        }
        return resultResponse;
    }

    // Logging in a user by updating their token and token expiration date
    @PostMapping("/loginUser")
    public ResultResponse<String> login(@RequestBody User user) {
        ResultResponse<String> resultResponse = new ResultResponse<>();
        // Retrieving user information from backend by their email
        User u = userService.getUserByEmail(user.getEmail());
        // Check if user exists in database
        try {
            // If user does not exist, assign appropriate error message to the result response object
            if (u == null) {
                resultResponse.setCode(UserFailureCode.NO_VALID_ACCOUNT.getCode());
                resultResponse.setMessage(UserFailureCode.NO_VALID_ACCOUNT.getMessage());
            }
            // If user exists, Login user
            else {
                resultResponse.setData(authService.login(user.getEmail(), user.getPassword()));
            }
        } catch (AuthException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Logging in the administrator by updating their token and token expiration date
    @PostMapping("/loginAdmin")
    public ResultResponse<String> loginAdmin(@RequestBody User user) {
        ResultResponse<String> resultResponse = new ResultResponse<>();
        // Retrieving user information from backend by their email
        User u = userService.getUserByEmail(user.getEmail());
        try {
            // If user is not found, assign appropriate error message to the result response object
            if (u == null) {
                resultResponse.setCode(UserFailureCode.NO_VALID_ACCOUNT.getCode());
                resultResponse.setMessage(UserFailureCode.NO_VALID_ACCOUNT.getMessage());
            }
            else if (!validateAdmin(u)) {
                // If user found but not admin, assign appropriate error message to the result response object
                resultResponse.setCode(UserFailureCode.NOT_ADMIN.getCode());
                resultResponse.setMessage(UserFailureCode.NOT_ADMIN.getMessage());
            }
            else {
                // If user found and is admin, login admin
                resultResponse.setData(authService.login(user.getEmail(), user.getPassword()));
            }
        } catch (AuthException e) {
            resultResponse.setCode(e.getCode());
            resultResponse.setMessage(e.getMessage());
        }
        return resultResponse;
    }

    // Validate if a user is the administrator by checking their account type attribute
    public Boolean validateAdmin(User user) {
        return user.getAccountType().equalsIgnoreCase("ADMIN");
    }

    // Logout a user by updating their token expiration date
    @PostMapping("/logout")
    public ResultResponse<Boolean> logout(@RequestBody User user) {
        ResultResponse<Boolean> resultResponse = new ResultResponse<>();

        if (authService.hasValidCredential(user.getEmail(), user.getToken())) {
            authService.setExpire(user);
            resultResponse.setData(true);
        }
        else  {
            resultResponse.setCode(UserFailureCode.NO_VALID_ACCOUNT.getCode());
            resultResponse.setMessage(UserFailureCode.NO_VALID_ACCOUNT.getMessage());
            resultResponse.setData(false);
        }
        return resultResponse;
    }


}
