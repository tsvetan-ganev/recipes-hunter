package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.os.AsyncTask;

import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;

public class DeleteFavouriteRecipeAsyncTask extends AsyncTask<Void, Integer, Void> {
    private final RecipeDAO mRecipeDao;
    private final String mRecipeId;
    private final Context mContext;

    public AsyncResponse<Void> delegate;

    public DeleteFavouriteRecipeAsyncTask(Context context, String recipeId) {
        mRecipeDao = new RecipeDAOImpl(context);
        mRecipeId = recipeId;
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mRecipeDao.open();
        mRecipeDao.delete(mRecipeId);
        mContext.getSharedPreferences(Constants.APP_NAME, Context.MODE_APPEND)
                .edit()
                .remove(mRecipeId);
        mRecipeDao.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        delegate.resolve(result);
    }
}
