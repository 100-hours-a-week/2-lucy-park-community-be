package com.example.community.dto.Wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WrapperResponse<T> {
    private String message;
    private T data;
}
