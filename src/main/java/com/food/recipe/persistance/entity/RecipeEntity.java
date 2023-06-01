package com.food.recipe.persistance.entity;

import com.food.recipe.model.RecipeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity(name = "recipe")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipeType type;

    @NotNull
    @Column(name = "servings", nullable = false)
    private Integer servings;

    @NotNull
    @Lob
    @Column(name = "instructions", nullable = false)
    private String instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private AuthorEntity author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeEntity recipe = (RecipeEntity) o;

        if (!name.equals(recipe.name)) return false;
        if (!type.equals(recipe.type)) return false;
        if (!servings.equals(recipe.servings)) return false;
        return author.equals(recipe.author);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + servings.hashCode();
        result = 31 * result + author.hashCode();
        return result;
    }
}