package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;

public class DeleteFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, Void> {
    private final RecipeDAO mRecipeDao;
    private final String mRecipeId;
    private final Context mContext;

    public Promise<Void, Exception> delegate;

    public DeleteFavouriteRecipeAsyncTask(Context context, String recipeId) {
        mRecipeDao = new RecipeDAOImpl(context);
        mRecipeId = recipeId;
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mRecipeDao.open();
        mRecipeDao.delete(mRecipeId);
        mContext.getSharedPreferences(Constants.PREFERENCES_FAVOURITE_RECIPES, Context.MODE_PRIVATE)
                .edit()
                .remove(mRecipeId)
                .apply();
        mRecipeDao.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        delegate.resolve(result);
    }
}
