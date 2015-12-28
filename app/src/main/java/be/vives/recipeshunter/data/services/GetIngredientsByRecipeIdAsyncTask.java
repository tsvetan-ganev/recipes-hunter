package be.vives.recipeshunter.data.services;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.vives.recipeshunter.data.local.dao.IngredientDAO;
import be.vives.recipeshunter.data.local.dao.impl.IngredientDAOImpl;

public class GetIngredientsByRecipeIdAsyncTask extends AsyncTask<Void, Integer, List<String>> {
    private IngredientDAO mIngredientDAO;
    private final String mRecipeId;
    private Exception mError;

    public Promise<List<String>, Exception> delegate;

    public GetIngredientsByRecipeIdAsyncTask(Context context, String recipeId) {
        this.mRecipeId = recipeId;
        mIngredientDAO = new IngredientDAOImpl(context);
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        if (mRecipeId == null || mRecipeId.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = null;
        try {
            mIngredientDAO.open();
            result = mIngredientDAO.findAllByRecipeId(mRecipeId);
        } catch (Exception ex) {
            mError = ex;
        } finally {
            mIngredientDAO.close();
        }

        return (result != null) ? result : new ArrayList<String>();
    }

    @Override
    protected void onPostExecute(List<String> result) {
        if (this.delegate == null) {
            throw new IllegalStateException("Delegate must be initialized for the task to execute.");
        }

        if (mError == null) {
            delegate.resolve(result);
        } else {
            delegate.reject(mError);
        }
    }
}
