package com.food.recipe.persistance;

import com.food.recipe.persistance.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer>, JpaSpecificationExecutor<RecipeEntity> {
    Optional<RecipeEntity> findByNameAndAuthorId(String name, Integer authorId);
    List<RecipeEntity> findAllByAuthorId(Integer authorId);

}
