package com.food.recipe.dto.v1.response;

import lombok.*;

@Setter
@Getter
@EqualsAndHashCode
@Builder
public class Author {
    private String name;
    private String email;
}
