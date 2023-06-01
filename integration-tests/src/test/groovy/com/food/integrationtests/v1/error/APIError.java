package com.food.integrationtests.v1.error;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class APIError {
    private int status;
    private List<String> errors;
}
