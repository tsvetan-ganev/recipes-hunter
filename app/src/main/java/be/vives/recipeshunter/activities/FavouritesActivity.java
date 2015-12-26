package be.vives.recipeshunter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.fragments.favourites.FavouritesListFragment;
import be.vives.recipeshunter.fragments.favourites.FavouritesListFragment.FavouritesListFragmentListener;
import be.vives.recipeshunter.fragments.favourites.FavouritesRecipeDetailsFragment;
import be.vives.recipeshunter.fragments.favourites.FavouritesRecipeDetailsFragment.FavouritesRecipeDetailsListener;

public class FavouritesActivity extends AppCompatActivity implements
        FavouritesListFragmentListener,
        FavouritesRecipeDetailsListener {

    // data
    RecipeDetailsViewModel mRecipeDetails;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS, mRecipeDetails);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // init image loader
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        }

        // check for recipe to be displayed in details
        if (savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
        }

        if (mRecipeDetails != null) {
            navigateToDetailsFragment();
        } else {
            navigateToFavouritesListFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (mRecipeDetails != null) {
            mRecipeDetails = null;
        }

        super.onBackPressed();
    }

    @Override
    public void setRecipe(RecipeEntity recipe) {
        mRecipeDetails = new RecipeDetailsViewModel();

        mRecipeDetails.setId(recipe.getId());
        mRecipeDetails.setTitle(recipe.getTitle());
        mRecipeDetails.setPublisherName(recipe.getPublisherName());
        mRecipeDetails.setSourceUrl(recipe.getSourceUrl());
        mRecipeDetails.setSocialRank(recipe.getSocialRank());
        mRecipeDetails.setImageUrl(recipe.getImageUrl());
    }

    @Override
    public RecipeDetailsViewModel getRecipeDetails() {
        return mRecipeDetails;
    }

    @Override
    public void navigateToDetailsFragment() {
        getSupportFragmentManager().popBackStack(Constants.FRAGMENT_FAVOURITES_RECIPE_DETAILS, RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(Constants.FRAGMENT_FAVOURITES_RECIPE_DETAILS)
                .replace(R.id.fragment_favourites_placeholder, new FavouritesRecipeDetailsFragment())
                .commit();
    }

    private void navigateToFavouritesListFragment() {
        getSupportFragmentManager().popBackStack(Constants.FRAGMENT_FAVOURITES_RECIPE_DETAILS, RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_favourites_placeholder, new FavouritesListFragment())
                .commit();
    }
}
