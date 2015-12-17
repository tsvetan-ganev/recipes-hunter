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

import be.vives.recipeshunter.data.viewmodels.RecipeAdditionalInfoViewModel;

public class DownloadRecipeDetailsAsyncTask extends AsyncTask<URL, Void, RecipeAdditionalInfoViewModel> {
    private final String API_KEY = "9e6a705f76e8cd129b7692e570294410";

    private final String API_ENDPOINT = "http://food2fork.com/api/";

    private final String mBaseUrl = API_ENDPOINT + "get?key=" + API_KEY + "&rId=";

    private final String mRecipeId;

    public DownloadRecipeDetailsAsyncTask(String recipeId) {
        mRecipeId = recipeId;
    }

    @Override
    protected RecipeAdditionalInfoViewModel doInBackground(URL... params) {
        OkHttpClient http = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mBaseUrl + mRecipeId)
                .addHeader("Accept", "application/json")
                .build();

        Response res = null;
        RecipeAdditionalInfoViewModel detailsViewModel = new RecipeAdditionalInfoViewModel();
        try {
            res = http.newCall(request).execute();
            JSONObject jsonRecipe = new JSONObject(res.body()
                    .string())
                    .getJSONObject("recipe");

            List<String> ingredients = new ArrayList<>();
            JSONArray ingredientsJsonArr = jsonRecipe.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsJsonArr.length(); i++) {
                String ingredient = ingredientsJsonArr.getString(i);

                if (ingredient != null && !ingredient.isEmpty()) {
                    ingredients.add(HtmlEscape.unescapeHtml(ingredient));
                }
            }

            detailsViewModel.setIngredients(ingredients);
            detailsViewModel.setSourceUrl(jsonRecipe.getString("source_url"));
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return detailsViewModel;
    }
}
