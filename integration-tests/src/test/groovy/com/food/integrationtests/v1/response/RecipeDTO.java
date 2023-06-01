package com.food.integrationtests.v1.response;


import com.food.integrationtests.v1.request.RecipeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RecipeDTO {
    public Integer id;
    public String name;
    public RecipeType type;
    public Integer servings;
    public String instructions;
    public List<String> ingredients;
    public Author author;
}
