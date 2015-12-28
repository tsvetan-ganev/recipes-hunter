package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.IngredientEntity;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.local.dao.IngredientDAO;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.IngredientDAOImpl;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;

public class AddFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, RecipeEntity> {
    private final RecipeDAO mRecipeDao;
    private final IngredientDAO mIngredientDao;
    private final Context mContext;
    private final RecipeDetailsViewModel mRecipe;

    private Exception mError;

    public Promise<RecipeEntity, Exception> delegate;

    public AddFavouriteRecipeAsyncTask(Context context, RecipeDetailsViewModel recipe) {
        mContext = context;
        mRecipe = recipe;
        mRecipeDao = new RecipeDAOImpl(context);
        mIngredientDao = new IngredientDAOImpl(context);
    }

    @Override
    protected RecipeEntity doInBackground(Void... params) {
        // convert from view model to entity
        RecipeEntity recipe = new RecipeEntity();

        recipe.setId(mRecipe.getId());
        recipe.setTitle(mRecipe.getTitle());
        recipe.setPublisherName(mRecipe.getPublisherName());
        recipe.setSourceUrl(mRecipe.getSourceUrl());
        recipe.setImageUrl(mRecipe.getImageUrl());
        recipe.setSocialRank(mRecipe.getSocialRank());

        try {
            insertRecipeEntity(recipe);
            insertRecipeIngredients(mRecipe);

            // mark the recipe as saved to favourites
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                    Constants.PREFERENCES_FAVOURITE_RECIPES, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(mRecipe.getId(), true).apply();
        } catch (Exception ex) {
            this.mError = ex;
        } finally {
            mRecipeDao.close();
            mIngredientDao.close();
        }

        return recipe;
    }

    @Override
    protected void onPostExecute(RecipeEntity result) {
        if (delegate == null) {
            throw new IllegalStateException("Delegate should be initialized for the task to execute.");
        }

        if (mError == null) {
            delegate.resolve(result);
        } else {
            delegate.reject(mError);
        }
    }

    private void insertRecipeEntity(RecipeEntity recipe) {
        mRecipeDao.open();
        mRecipeDao.insert(recipe);
        mRecipeDao.close();
    }

    private void insertRecipeIngredients(RecipeDetailsViewModel recipe) {
        mIngredientDao.open();

        if (recipe.getIngredients() == null) {
            throw new IllegalArgumentException("Recipe ingredients cannot be null.");
        }

        for (String ingredientName :
                recipe.getIngredients()) {
            IngredientEntity ingredient = new IngredientEntity();
            ingredient.setName(ingredientName);
            ingredient.setRecipeId(recipe.getId());
            mIngredientDao.insert(ingredient);
        }
        mIngredientDao.close();
    }
}
