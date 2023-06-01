CREATE TABLE author (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
);
CREATE TABLE recipe (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(1000) NOT NULL,
                        type VARCHAR(100)  NOT NULL,
                        servings INT NOT NULL,
                        instructions TEXT NOT NULL,
                        author_id INT,
                        FOREIGN KEY (author_id) REFERENCES Author(id)
);

CREATE TABLE ingredient (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL
);

CREATE TABLE recipe_ingredient (
                                  recipe_id INT,
                                  ingredient_id INT,
                                  FOREIGN KEY (recipe_id) REFERENCES Recipe(id),
                                  FOREIGN KEY (ingredient_id) REFERENCES Ingredient(id),
                                  PRIMARY KEY (recipe_id, ingredient_id)
);
