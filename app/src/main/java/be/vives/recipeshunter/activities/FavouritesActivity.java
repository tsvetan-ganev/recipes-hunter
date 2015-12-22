package be.vives.recipeshunter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.fragments.favourites.FavouritesAddRecipeFragment;
import be.vives.recipeshunter.fragments.favourites.FavouritesListFragment;
import be.vives.recipeshunter.fragments.favourites.FavouritesListFragment.FavouritesListFragmentListener;
import be.vives.recipeshunter.fragments.favourites.FavouritesRecipeDetailsFragment;
import be.vives.recipeshunter.fragments.favourites.FavouritesRecipeDetailsFragment.FavouritesRecipeDetailsListener;

import static be.vives.recipeshunter.fragments.favourites.FavouritesAddRecipeFragment.*;

public class FavouritesActivity extends AppCompatActivity implements
        OnRecipeAddedToFavouritesListener,
        FavouritesListFragmentListener,
        FavouritesRecipeDetailsListener {

    RecipeDetailsViewModel mToBeAddedToFavourites;
    RecipeDetailsViewModel mRecipeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle("Favourites");

        Bundle passedBundle = getIntent().getExtras();
        if (passedBundle != null) {
            mToBeAddedToFavourites = passedBundle.getParcelable("recipe_details");
        }
        navigateToFavouritesList();
    }

    @Override
    public RecipeDetailsViewModel getFavouriteRecipe() {
        return mToBeAddedToFavourites;
    }

    @Override
    public void navigateToFavouritesList() {
        if (mToBeAddedToFavourites != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_favourites_placeholder, new FavouritesAddRecipeFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_favourites_placeholder, new FavouritesListFragment())
                    .commit();
        }

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
    public void navigateToDetailsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_favourites_placeholder, new FavouritesRecipeDetailsFragment())
                .addToBackStack("fav_details")
                .commit();
    }

    @Override
    public RecipeDetailsViewModel getRecipeDetails() {
        return mRecipeDetails;
    }
}
