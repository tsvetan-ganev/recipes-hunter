package be.vives.recipeshunter.data;

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

import org.unbescape.html.HtmlEscape;

import be.vives.recipeshunter.models.Recipe;

public class DownloadRecipesAsyncTask extends AsyncTask<URL, Integer, ArrayList<Recipe>> {
    private final String API_KEY = "9e6a705f76e8cd129b7692e570294410";

    private final String API_ENDPOINT = "http://food2fork.com/api/";

    private final String mBaseUrl = API_ENDPOINT + "search?key=" + API_KEY + "&q=";

    private final String mQuery;

    private ArrayList<Recipe> mRecipes = new ArrayList<>();

    public DownloadRecipesAsyncTask(String query) {
        mQuery = query;
    }

    @Override
    protected ArrayList<Recipe> doInBackground(URL... params) {
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
                Recipe currentRecipe = new Recipe();
                currentJsonRecipe = jsonData.getJSONObject(i);
                currentRecipe.setId(currentJsonRecipe.getString("recipe_id"));
                currentRecipe.setTitle(HtmlEscape.unescapeHtml(currentJsonRecipe.getString("title")));
                currentRecipe.setPublisherName(HtmlEscape.unescapeHtml(currentJsonRecipe.getString("publisher")));
                currentRecipe.setSourceUrl(new URL(currentJsonRecipe.getString("source_url")));
                currentRecipe.setImageUrl(new URL(currentJsonRecipe.getString("image_url")));
                currentRecipe.setSocialRank(currentJsonRecipe.getInt("social_rank"));

                mRecipes.add(currentRecipe);
                if (isCancelled()) break;
            }
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return mRecipes;
    }
}
