package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;

public class RemoveFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, RecipeEntity> {
    private final RecipeDAO mRecipeDao;
    private final RecipeEntity mRecipe;
    private final Context mContext;
    private SQLiteException mError;

    public Promise<RecipeEntity, Exception> delegate;

    public RemoveFavouriteRecipeAsyncTask(Context context, RecipeEntity recipe) {
        mRecipeDao = new RecipeDAOImpl(context);
        mRecipe = recipe;
        mContext = context;
    }

    @Override
    protected RecipeEntity doInBackground(Void... params) {
        try {
            mRecipeDao.open();
            mRecipeDao.delete(mRecipe);
            mContext.getSharedPreferences(Constants.PREFERENCES_FAVOURITE_RECIPES, Context.MODE_PRIVATE)
                    .edit()
                    .remove(mRecipe.getId())
                    .apply();
        } catch (SQLiteException ex) {
            mError = ex;
        } finally {
            mRecipeDao.close();
        }

        return mRecipe;
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
}
