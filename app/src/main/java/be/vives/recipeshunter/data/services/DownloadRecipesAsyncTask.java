package be.vives.recipeshunter.data.services;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.unbescape.html.HtmlEscape;

import be.vives.recipeshunter.data.entities.RecipeEntity;

public class DownloadRecipesAsyncTask extends AsyncTask<URL, Integer, List<RecipeEntity>> {
    private final String API_KEY = "9e6a705f76e8cd129b7692e570294410";

    private final String API_ENDPOINT = "http://food2fork.com/api/";

    private final String mBaseUrl = API_ENDPOINT + "search?key=" + API_KEY + "&q=";

    private final String mQuery;

    public DownloadRecipesAsyncTask(String query) {
        mQuery = query;
    }

    @Override
    protected List<RecipeEntity> doInBackground(URL... params) {
        List<RecipeEntity> recipes = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(mBaseUrl + mQuery)
                .addHeader("Accept", "application/json")
                .build();

        Response res = null;
        try {
            res = client.newCall(req).execute();
            JSONObject jsonObj = new JSONObject(res.body().string());
            JSONArray jsonData = jsonObj.getJSONArray("recipes");
            JSONObject currentJsonRecipe;

            for (int i = 0; i < jsonData.length(); i++) {
                RecipeEntity currentRecipe = new RecipeEntity();
                currentJsonRecipe = jsonData.getJSONObject(i);
                currentRecipe.setId(currentJsonRecipe.getString("recipe_id"));
                currentRecipe.setTitle(HtmlEscape.unescapeHtml(currentJsonRecipe.getString("title")));
                currentRecipe.setPublisherName(HtmlEscape.unescapeHtml(currentJsonRecipe.getString("publisher")));
                currentRecipe.setImageUrl(currentJsonRecipe.getString("image_url"));
                currentRecipe.setSocialRank(currentJsonRecipe.getInt("social_rank"));

                recipes.add(currentRecipe);
                if (isCancelled()) break;
            }
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return recipes;
    }
}
