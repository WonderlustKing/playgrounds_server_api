package com.playgrounds.api.user.validator;

import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by christos on 3/11/17.
 */

@Component
public class RateValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Rate.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Rate rate = (Rate) target;
        if (userIdExist(rate.getUser()) == null) {
            errors.reject("invalid.user", "User not exists");
        }
    }

    private String userIdExist(String userId) {
        try {
            return userService.userExist(userId).getId();
        } catch (UserNotFoundException e) {
            return null;
        }
    }
}
