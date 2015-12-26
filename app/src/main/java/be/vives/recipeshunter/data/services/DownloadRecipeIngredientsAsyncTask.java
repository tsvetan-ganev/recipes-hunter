package be.vives.recipeshunter.data.services;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unbescape.html.HtmlEscape;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.data.Constants;

public class DownloadRecipeIngredientsAsyncTask extends AsyncTask<URL, Void, List<String>> {
    private final String mBaseUrl = Constants.API_ENDPOINT + "get?key=" + Constants.API_KEY + "&rId=";

    private final String mRecipeId;

    private Exception mError;

    public Promise<List<String>, Exception> delegate;

    public DownloadRecipeIngredientsAsyncTask(String recipeId) {
        mRecipeId = recipeId;
    }

    @Override
    protected List<String> doInBackground(URL... params) {
        OkHttpClient http = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mBaseUrl + mRecipeId)
                .addHeader("Accept", "application/json")
                .build();

        List<String> ingredients = new ArrayList<>();
        Response res = null;
        try {
            res = http.newCall(request).execute();
            JSONObject jsonRecipe = new JSONObject(res.body()
                    .string())
                    .getJSONObject("recipe");

            JSONArray ingredientsJsonArr = jsonRecipe.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsJsonArr.length(); i++) {
                String ingredient = ingredientsJsonArr.getString(i);

                if (ingredient != null && !ingredient.isEmpty()) {
                    ingredients.add(HtmlEscape.unescapeHtml(ingredient));
                }
            }
        } catch (IOException | JSONException ex) {
            mError = ex;
        }

        return ingredients;
    }

    @Override
    protected void onPostExecute(List<String> result) {
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
