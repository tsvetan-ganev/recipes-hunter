package be.vives.recipeshunter.data.local.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.data.entities.IngredientEntity;
import be.vives.recipeshunter.data.local.DbContract;
import be.vives.recipeshunter.data.local.AppDb;
import be.vives.recipeshunter.data.local.dao.IngredientDAO;

public class IngredientDAOImpl extends AppDb implements IngredientDAO {
    private static final String WHERE_ID_EQUALS = DbContract.INGREDIENT_ID
            + " = ";

    private static final String WHERE_RECIPE_ID_EQUALS = DbContract.INGREDIENT_RECIPE_ID
            + " = ";

    public IngredientDAOImpl(Context context) {
        super(context);
    }

    @Override
    public List<IngredientEntity> findAll() {
        List<IngredientEntity> ingredients = new ArrayList<>();
        Cursor result = database.query(
                DbContract.INGREDIENT_TABLE,
                new String[]{
                        DbContract.INGREDIENT_ID,
                        DbContract.INGREDIENT_NAME,
                        DbContract.INGREDIENT_RECIPE_ID
                }, null, null, null, null, null
        );

        while (result.moveToNext()) {
            IngredientEntity ingredient = new IngredientEntity();
            ingredient.setId(result.getInt(0));
            ingredient.setName(result.getString(1));
            ingredient.setRecipeId(result.getString(2));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    @Override
    public List<String> findAllByRecipeId(String recipeId) {
        List<String> ingredients = new ArrayList<>();
        Cursor result = database.query(
                DbContract.INGREDIENT_TABLE,
                new String[] {
                        DbContract.INGREDIENT_NAME,
                },
                WHERE_RECIPE_ID_EQUALS + String.format("'%s'", recipeId),
                null, null, null, null
        );

        while (result.moveToNext()) {
            ingredients.add(result.getString(0));
        }

        return ingredients;
    }

    @Override
    public IngredientEntity findById(Integer id) {
        Cursor result = database.query(
                DbContract.INGREDIENT_TABLE,
                new String[] {
                        DbContract.INGREDIENT_ID,
                        DbContract.INGREDIENT_NAME,
                        DbContract.INGREDIENT_RECIPE_ID
                },
                WHERE_ID_EQUALS + id,
                null, null, null, null
        );

        if (!result.moveToFirst()) {
            return null;
        }

        IngredientEntity ingredient = new IngredientEntity();
        ingredient.setId(result.getInt(0));
        ingredient.setName(result.getString(1));
        ingredient.setRecipeId(result.getString(2));

        return ingredient;
    }

    @Override
    public void insert(IngredientEntity entity) {
        ContentValues values = new ContentValues();
        values.put(DbContract.INGREDIENT_ID, entity.getId());
        values.put(DbContract.INGREDIENT_NAME, entity.getName());
        values.put(DbContract.INGREDIENT_RECIPE_ID, entity.getRecipeId());

        database.insert(DbContract.INGREDIENT_TABLE ,null, values);
    }
}
