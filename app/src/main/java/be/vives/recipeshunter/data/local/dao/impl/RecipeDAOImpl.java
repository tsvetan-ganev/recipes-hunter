package be.vives.recipeshunter.data.local.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.data.local.DbContract;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.AppDb;
import be.vives.recipeshunter.data.entities.RecipeEntity;

public class RecipeDAOImpl extends AppDb implements RecipeDAO {
    private static final String WHERE_ID_EQUALS = DbContract.RECIPE_ID
            + " =?";

    public RecipeDAOImpl(Context context) {
        super(context);
    }

    @Override
    public List<RecipeEntity> findAll() {
        List<RecipeEntity> recipes = new ArrayList<>();
        Cursor result = database.query(
                DbContract.RECIPE_TABLE,
                new String[] {
                        DbContract.RECIPE_ID,
                        DbContract.RECIPE_TITLE,
                        DbContract.RECIPE_PUBLISHER_NAME,
                        DbContract.RECIPE_IMG_URL,
                        DbContract.RECIPE_SOCIAL_RANK,
                        DbContract.RECIPE_SRC_URL
                },
                null, null, null, null, null);

        while(result.moveToNext()) {
            RecipeEntity recipe = new RecipeEntity();
            recipe.setId(result.getString(0));
            recipe.setTitle(result.getString(1));
            recipe.setPublisherName(result.getString(2));
            recipe.setImageUrl(result.getString(3));
            recipe.setSocialRank(result.getInt(4));
            recipe.setSourceUrl(result.getString(5));

            recipes.add(recipe);
        }

        return recipes;
    }

    @Override
    public RecipeEntity findById(String id) {
        Cursor result = database.query(DbContract.RECIPE_TABLE,
                new String[] {
                    DbContract.RECIPE_ID,
                    DbContract.RECIPE_TITLE,
                    DbContract.RECIPE_PUBLISHER_NAME,
                    DbContract.RECIPE_IMG_URL,
                    DbContract.RECIPE_SOCIAL_RANK,
                    DbContract.RECIPE_SRC_URL
                },
                WHERE_ID_EQUALS,
                new String[] { id },
                null, null,null);

        RecipeEntity recipe = null;
        if (result.moveToFirst()) {
            recipe = new RecipeEntity();
            recipe.setId(result.getString(0));
            recipe.setTitle(result.getString(1));
            recipe.setPublisherName(result.getString(2));
            recipe.setImageUrl(result.getString(3));
            recipe.setSocialRank(result.getInt(4));
            recipe.setSourceUrl(result.getString(5));
        }

        return recipe;
    }

    @Override
    public void insert(RecipeEntity entity) {
        ContentValues values = new ContentValues();
        values.put(DbContract.RECIPE_ID, entity.getId());
        values.put(DbContract.RECIPE_TITLE, entity.getTitle());
        values.put(DbContract.RECIPE_PUBLISHER_NAME, entity.getPublisherName());
        values.put(DbContract.RECIPE_IMG_URL, entity.getImageUrl());
        values.put(DbContract.RECIPE_SOCIAL_RANK, entity.getSocialRank());
        values.put(DbContract.RECIPE_SRC_URL, entity.getSourceUrl());

        database.insert(DbContract.RECIPE_TABLE, null, values);
    }

    @Override
    public void delete(String id) {
        database.delete(DbContract.RECIPE_TABLE, WHERE_ID_EQUALS, new String[] { id });
    }
}
