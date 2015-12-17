package be.vives.recipeshunter.data.local.dao;

import java.util.List;

import be.vives.recipeshunter.data.entities.IngredientEntity;

public interface IngredientDAO extends GenericDAO<Integer, IngredientEntity> {
    List<String> findAllByRecipeId(String recipeId);
}
