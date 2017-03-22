package com.playgrounds.api.user.validator;

import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by christos on 3/14/17.
 */
public class CheckRateValidator implements ConstraintValidator<UserExist, String>
{
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UserExist constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (userIdExist(value) == null) {
            return false;
        }
        return true;
    }

    private String userIdExist(String userId) {
        try {
            return userService.userExist(userId).getId();
        } catch (UserNotFoundException e) {
            return null;
        }
    }
}
