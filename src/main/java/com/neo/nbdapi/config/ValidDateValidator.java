package com.neo.nbdapi.config;

import com.neo.nbdapi.utils.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author thanglv on 11/2/2020
 * @project NBD
 */

public class ValidDateValidator implements ConstraintValidator<ValidDate, String>{

    String dateFormat;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        dateFormat = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return DateUtils.isValid(value, dateFormat);
    }

}