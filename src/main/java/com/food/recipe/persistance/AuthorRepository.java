package com.food.recipe.persistance;

import com.food.recipe.persistance.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer>,
                         JpaSpecificationExecutor<AuthorEntity> {
     Optional<AuthorEntity> findByNameAndEmail(String name, String email);
}
