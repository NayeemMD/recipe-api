package com.food.recipe.persistance;

import com.food.recipe.persistance.entity.RecipeIngredientEntity;
import com.food.recipe.persistance.entity.RecipeIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredientEntity, RecipeIngredientId>,
                                                        JpaSpecificationExecutor<RecipeIngredientEntity> {
    List<RecipeIngredientEntity> findAllByRecipeId(Integer id);
    List<RecipeIngredientEntity> findAllByIngredientId(Integer id);
}
