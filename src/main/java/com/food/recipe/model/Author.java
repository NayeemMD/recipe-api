package com.food.recipe.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder(setterPrefix = "with")
@Getter
@EqualsAndHashCode
@ToString
public class Author {
    private String name;
    private String email;
}
