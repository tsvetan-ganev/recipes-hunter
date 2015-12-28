package be.vives.recipeshunter.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

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

// todo: fix navigation on back pressed
public class FavouritesActivity extends AppCompatActivity implements
        FavouritesListFragmentListener,
        FavouritesRecipeDetailsListener {

    // data
    RecipeDetailsViewModel mRecipeDetails;

    // fragment state
    Fragment mFragment;
    String mLastFragmentTag;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS, mRecipeDetails);
        outState.putString(Constants.LAST_FRAGMENT_TAG, mLastFragmentTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
            mLastFragmentTag = savedInstanceState.getString(Constants.LAST_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Log.d(getClass().getSimpleName(), "onCreate: " + getFragmentManager().getBackStackEntryCount());

        // init image loader
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        }

        if (savedInstanceState == null) {
            if (mRecipeDetails != null) {
                navigateFromFavouritesListFragment();
            } else {
                navigateToFavouritesListFragment();
            }
        } else {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
            mFragment = getSupportFragmentManager().findFragmentByTag(mLastFragmentTag);
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
    public void navigateFromFavouritesListFragment() {
        mFragment = new FavouritesRecipeDetailsFragment();
        mLastFragmentTag = Constants.FRAGMENT_FAVOURITES_RECIPE_DETAILS;

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment, mLastFragmentTag)
                .commit();
    }

    private void navigateToFavouritesListFragment() {
        mFragment = new FavouritesListFragment();
        mLastFragmentTag = Constants.FRAGMENT_FAVOURITES_RECIPES_LIST;

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment, mLastFragmentTag)
                .commit();
    }
}
