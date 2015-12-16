package be.vives.recipeshunter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;

public class FavouritesAddRecipeFragment extends Fragment {

    RecipeDetailsViewModel mNewlyAddedFavouriteRecipe;
    OnRecipeAddedToFavouritesListener mListener;
    private RecipeDAOImpl mRecipeDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeDao = new RecipeDAOImpl(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNewlyAddedFavouriteRecipe = mListener.getFavouriteRecipe();
        RecipeEntity recipe = new RecipeEntity();

        mRecipeDao.open();
        recipe.setId(mNewlyAddedFavouriteRecipe.getId());
        recipe.setTitle(mNewlyAddedFavouriteRecipe.getTitle());
        recipe.setPublisherName(mNewlyAddedFavouriteRecipe.getPublisherName());
        recipe.setSourceUrl(mNewlyAddedFavouriteRecipe.getSourceUrl());
        recipe.setImageUrl(mNewlyAddedFavouriteRecipe.getImageUrl());
        recipe.setSocialRank(mNewlyAddedFavouriteRecipe.getSocialRank());
        mRecipeDao.insert(recipe);

        mRecipeDao.close();

        getFragmentManager().beginTransaction()
            .replace(R.id.fragment_placeholder, new FavouritesListFragment())
            .commit();

        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRecipeAddedToFavouritesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    public interface OnRecipeAddedToFavouritesListener {
        void setFavouriteRecipe(RecipeDetailsViewModel recipe);
        RecipeDetailsViewModel getFavouriteRecipe();
    }
}
