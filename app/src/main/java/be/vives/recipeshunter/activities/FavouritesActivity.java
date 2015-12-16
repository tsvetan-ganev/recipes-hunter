package be.vives.recipeshunter.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.fragments.FavouritesAddRecipeFragment;

import static be.vives.recipeshunter.fragments.FavouritesAddRecipeFragment.*;

public class FavouritesActivity extends AppCompatActivity
        implements OnRecipeAddedToFavouritesListener {

    RecipeDetailsViewModel mLatestFavouriteRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Bundle passedBundle = getIntent().getExtras();

        mLatestFavouriteRecipe = new RecipeDetailsViewModel();
        if (passedBundle != null) {
            mLatestFavouriteRecipe.setId(passedBundle.getString("recipe_id"));
            mLatestFavouriteRecipe.setTitle(passedBundle.getString("recipe_title"));
            mLatestFavouriteRecipe.setPublisherName(passedBundle.getString("recipe_publisher_name"));
            mLatestFavouriteRecipe.setSourceUrl(passedBundle.getString("recipe_src_url"));
            mLatestFavouriteRecipe.setImageUrl(passedBundle.getString("recipe_img_url"));
            mLatestFavouriteRecipe.setSocialRank(passedBundle.getInt("recipe_social_rank"));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, new FavouritesAddRecipeFragment())
                .commit();
    }

    @Override
    public void setFavouriteRecipe(RecipeDetailsViewModel recipe) {
        mLatestFavouriteRecipe = recipe;
    }

    @Override
    public RecipeDetailsViewModel getFavouriteRecipe() {
        return mLatestFavouriteRecipe;
    }
}
