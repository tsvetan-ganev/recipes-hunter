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

    public AsyncResponse<List<String>> delegate;

    public GetIngredientsByRecipeIdAsyncTask(Context context, String recipeId) {
        this.mRecipeId = recipeId;
        mIngredientDAO = new IngredientDAOImpl(context);
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        if (mRecipeId == null || mRecipeId.isEmpty()) {
            return Collections.emptyList();
        }

        mIngredientDAO.open();
        List<String> result = mIngredientDAO.findAllByRecipeId(mRecipeId);
        mIngredientDAO.close();

        return (result != null) ? result : new ArrayList<String>();
    }

    @Override
    protected void onPostExecute(List<String> result) {
        if (this.delegate != null) {
            delegate.resolve(result);
        }
    }
}
