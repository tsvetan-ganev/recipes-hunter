package be.vives.recipeshunter.data.local.dao;

import be.vives.recipeshunter.data.entities.RecipeEntity;

public interface RecipeDAO extends GenericDAO<String, RecipeEntity> {
    void delete(RecipeEntity entity);
}
