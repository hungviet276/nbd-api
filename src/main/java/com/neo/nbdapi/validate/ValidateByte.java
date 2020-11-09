package com.neo.nbdapi.validate;

import com.neo.nbdapi.anotation.validate.ValidByte;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidateByte implements ConstraintValidator<ValidByte, String> {

    private int length;

    public void initialize(final ValidByte validByte) {
        this.length = validByte.length();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.getBytes().length > length)
        return false;
        return  true;
    }
}
