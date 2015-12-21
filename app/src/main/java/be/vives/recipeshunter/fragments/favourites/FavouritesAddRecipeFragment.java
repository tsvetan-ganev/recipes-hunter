package be.vives.recipeshunter.fragments.favourites;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.entities.IngredientEntity;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.local.dao.IngredientDAO;
import be.vives.recipeshunter.data.local.dao.RecipeDAO;
import be.vives.recipeshunter.data.local.dao.impl.IngredientDAOImpl;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;

public class FavouritesAddRecipeFragment extends Fragment {

    RecipeDetailsViewModel mTransferedRecipe;
    OnRecipeAddedToFavouritesListener mListener;
    private RecipeDAO mRecipeDao;
    private IngredientDAO mIngredientDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeDao = new RecipeDAOImpl(getActivity());
        mIngredientDao = new IngredientDAOImpl(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTransferedRecipe = mListener.getFavouriteRecipe();
        final RecipeEntity recipe = new RecipeEntity();

        mRecipeDao.open();

        // // TODO: 20.12.15 Handle insertion of duplicate entries
        // if already exists -> handle it somehow
        if (mRecipeDao.findById(mTransferedRecipe.getId()) != null) {
            mRecipeDao.close();
            return null;
        }

        recipe.setId(mTransferedRecipe.getId());
        recipe.setTitle(mTransferedRecipe.getTitle());
        recipe.setPublisherName(mTransferedRecipe.getPublisherName());
        recipe.setSourceUrl(mTransferedRecipe.getSourceUrl());
        recipe.setImageUrl(mTransferedRecipe.getImageUrl());
        recipe.setSocialRank(mTransferedRecipe.getSocialRank());
        mRecipeDao.insert(recipe);

        // insert recipe ingredients
        mIngredientDao.open();
        for (String ingredientName :
                mTransferedRecipe.getIngredients()) {
            IngredientEntity ingredient = new IngredientEntity();
            ingredient.setName(ingredientName);
            ingredient.setRecipeId(recipe.getId());
            mIngredientDao.insert(ingredient);
        }
        mIngredientDao.close();

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
        RecipeDetailsViewModel getFavouriteRecipe();

        void navigateToFavouritesList();
    }
}
