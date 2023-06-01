package com.food.recipe.dto.v1.error;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class APIError {
    private int status;
    private List<String> errors;
}
