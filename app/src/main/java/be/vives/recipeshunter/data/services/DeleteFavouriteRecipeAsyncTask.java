package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.os.AsyncTask;

import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;

public class DeleteFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, Void> {
    private final RecipeDAO mRecipeDao;
    private final String mRecipeId;

    public DeleteFavouriteRecipeAsyncTask(Context context, String recipeId) {
        mRecipeDao = new RecipeDAOImpl(context);
        mRecipeId = recipeId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mRecipeDao.open();
        mRecipeDao.delete(mRecipeId);
        mRecipeDao.close();

        return null;
    }
}
