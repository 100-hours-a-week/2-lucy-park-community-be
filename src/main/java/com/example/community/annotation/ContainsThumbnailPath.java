package com.example.community.annotation;

import com.example.community.validator.ThumbnailPathValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ThumbnailPathValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainsThumbnailPath {
    String message() default "이미지 URL에는 '/uploads/thumbnail'이 포함되어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
