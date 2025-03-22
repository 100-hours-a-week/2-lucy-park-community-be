package com.example.community.validator;

import com.example.community.annotation.ContainsThumbnailPath;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ThumbnailPathValidator implements ConstraintValidator<ContainsThumbnailPath, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 또는 공백은 NotBlank가 처리하므로 여기선 검사 안 함
        if (value == null) return true;

        return value.contains("/uploads/thumbnail");
    }
}
