package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;

public class GetFavouriteRecipesAsyncTask extends AsyncTask<Void, Integer, List<RecipeEntity>> {
    private RecipeDAO mRecipeDAO;

    public Promise<List<RecipeEntity>, Exception> delegate;

    public GetFavouriteRecipesAsyncTask(Context context) {
        mRecipeDAO = new RecipeDAOImpl(context);
    }

    @Override
    protected List<RecipeEntity> doInBackground(Void... params) {
        mRecipeDAO.open();
        List<RecipeEntity> result =  mRecipeDAO.findAll();
        mRecipeDAO.close();

        return result;
    }

    @Override
    protected void onPostExecute(List<RecipeEntity> recipeEntities) {
        delegate.resolve(recipeEntities);
    }
}
