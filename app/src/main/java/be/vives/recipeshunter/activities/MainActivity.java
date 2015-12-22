package be.vives.recipeshunter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.fragments.main.RecipeDetailsFragment;
import be.vives.recipeshunter.fragments.main.RecipeDetailsFragment.RecipeDetailsFragmentListener;
import be.vives.recipeshunter.fragments.main.RecipesListFragment;
import be.vives.recipeshunter.fragments.main.RecipesListFragment.RecipesListFragmentListener;
import be.vives.recipeshunter.fragments.main.SearchRecipesFragment;
import be.vives.recipeshunter.fragments.main.SearchRecipesFragment.OnSearchSubmitFragmentListener;


public class MainActivity extends AppCompatActivity
        implements RecipesListFragmentListener,
        OnSearchSubmitFragmentListener,
        RecipeDetailsFragmentListener {

    private String mSearchQuery;
    private RecipeEntity mSelectedRecipe;
    // TODO: restore recipes list state

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.BUNDLE_ITEM_SEARCH_QUERY, mSearchQuery);
        outState.putParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE, mSelectedRecipe);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
        mSelectedRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ImageLoader init
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
            mSelectedRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
        } else {
            navigateToSearch();
            return;
        }

        if (mSelectedRecipe != null) {
            setRecipe(mSelectedRecipe);
            navigateToDetailsFragment();
            return;
        }

        if (mSearchQuery != null) {
            setSearchQuery(mSearchQuery);
            navigateFromSearchSubmitFragment();
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // unset the selected recipe
        if (mSelectedRecipe != null) {
            mSelectedRecipe = null;
        }

        super.onBackPressed();
    }

    // Listeners
    @Override
    public void setSearchQuery(String query) {
        mSearchQuery = query;
    }

    @Override
    public String getQueryString() {
        return mSearchQuery;
    }

    @Override
    public void setRecipe(RecipeEntity recipe) {
        mSelectedRecipe = recipe;
    }


    @Override
    public RecipeEntity getSelectedRecipe() {
        return mSelectedRecipe;
    }

    @Override
    public void navigateFromSearchSubmitFragment() {
        getSupportFragmentManager().popBackStack(Constants.FRAGMENT_MAIN_RECIPES_LIST, RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(Constants.FRAGMENT_MAIN_RECIPES_LIST)
                .replace(R.id.fragment_placeholder, new RecipesListFragment())
                .commit();
    }


    @Override
    public void navigateToDetailsFragment() {
        getSupportFragmentManager().popBackStack(Constants.FRAGMENT_MAIN_RECIPE_DETAILS, RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(Constants.FRAGMENT_MAIN_RECIPE_DETAILS)
                .replace(R.id.fragment_placeholder, new RecipeDetailsFragment())
                .commit();
    }

    private void navigateToSearch() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(Constants.FRAGMENT_MAIN_SEARCH)
                .replace(R.id.fragment_placeholder, new SearchRecipesFragment())
                .commit();
    }
}
