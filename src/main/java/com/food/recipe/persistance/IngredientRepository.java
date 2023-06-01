package com.food.recipe.persistance;

import com.food.recipe.persistance.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Integer>,
                                        JpaSpecificationExecutor<IngredientEntity> {
    Optional<IngredientEntity> findByName(String name);
}
