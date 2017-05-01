package com.playgrounds.api.user.validator;

import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;



@Component
@ComponentScan
public class RateValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Rate.class.isAssignableFrom(clazz);
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
            return userService.findUser(userId).getId();
        } catch (UserNotFoundException e) {
            return null;
        }
    }
}
