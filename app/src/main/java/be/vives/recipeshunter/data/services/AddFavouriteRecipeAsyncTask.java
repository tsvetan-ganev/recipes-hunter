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

public class AddFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, Boolean> {
    private final RecipeDAO mRecipeDao;
    private final IngredientDAO mIngredientDao;
    private final Context mContext;
    private final RecipeDetailsViewModel mRecipe;

    public AsyncResponse<Boolean> delegate;

    public AddFavouriteRecipeAsyncTask(Context context, RecipeDetailsViewModel recipe) {
        mContext = context;
        mRecipe = recipe;
        mRecipeDao = new RecipeDAOImpl(context);
        mIngredientDao = new IngredientDAOImpl(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // convert from view model to entity
        RecipeEntity recipe = new RecipeEntity();

        recipe.setId(mRecipe.getId());
        recipe.setTitle(mRecipe.getTitle());
        recipe.setPublisherName(mRecipe.getPublisherName());
        recipe.setSourceUrl(mRecipe.getSourceUrl());
        recipe.setImageUrl(mRecipe.getImageUrl());
        recipe.setSocialRank(mRecipe.getSocialRank());

        // insert the recipe
        mRecipeDao.open();
        mRecipeDao.insert(recipe);
        mRecipeDao.close();

        // insert its ingredients
        mIngredientDao.open();
        for (String ingredientName :
                mRecipe.getIngredients()) {
            IngredientEntity ingredient = new IngredientEntity();
            ingredient.setName(ingredientName);
            ingredient.setRecipeId(recipe.getId());
            mIngredientDao.insert(ingredient);
        }
        mIngredientDao.close();

        // mark the recipe as saved to favourites
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.APP_NAME, Context.MODE_APPEND);
        sharedPreferences.edit().putBoolean(mRecipe.getId(), true).commit();

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (delegate != null) {
            delegate.resolve(result);
        }
    }
}
