package be.vives.recipeshunter.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

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

    private MenuItem mConnectionStatus;

    // data
    // TODO: restore recipes list state
    private String mSearchQuery;
    private RecipeEntity mSelectedRecipe;
    private ArrayList<String> mIngredientsList;

    // fragment state
    private Fragment mFragment;
    private String mLastFragmentTag;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.BUNDLE_ITEM_SEARCH_QUERY, mSearchQuery);
        outState.putParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE, mSelectedRecipe);
        outState.putStringArrayList(Constants.BUNDLE_ITEM_INGREDIENTS_LIST, mIngredientsList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
        mSelectedRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
        mIngredientsList = savedInstanceState.getStringArrayList(Constants.BUNDLE_ITEM_INGREDIENTS_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // ImageLoader init
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        }

        if (savedInstanceState == null) {
            if (mSelectedRecipe != null) {
                setRecipe(mSelectedRecipe);
                navigateToDetailsFragment();
                return;
            }

            if (mSearchQuery != null) {
                setSearchQuery(mSearchQuery);
                navigateFromSearchSubmitFragment();
                return;
            } else {
                navigateToSearch();
            }
        } else {
            mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
            mSelectedRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
            mIngredientsList = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_INGREDIENTS_LIST);
            mFragment = getSupportFragmentManager().findFragmentByTag(mLastFragmentTag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mConnectionStatus = menu.findItem(R.id.menu_item_connection_status);
        updateConnectionStatus();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        // unset the selected recipe or the search query
        if (mSelectedRecipe != null) {
            mSelectedRecipe = null;
            mIngredientsList = null;
        } else {
            mSearchQuery = null;
        }

        super.onBackPressed();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void updateConnectionStatus() {
        if (isDeviceOnline()) {
            mConnectionStatus.setTitle("Connected");
            mConnectionStatus.setIcon(getResources().getDrawable(R.drawable.ic_online_white_24dp));
        } else {
            mConnectionStatus.setTitle("Offline");
            mConnectionStatus.setIcon(getResources().getDrawable(R.drawable.ic_offline_24dp));
        }
    }

    // Data transfer methods
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

    // Navigation methods
    @Override
    public void navigateFromSearchSubmitFragment() {
        mLastFragmentTag = Constants.FRAGMENT_MAIN_RECIPES_LIST;
        mFragment = new RecipesListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment)
                .commit();
    }

    @Override
    public void navigateToDetailsFragment() {
        mLastFragmentTag = Constants.FRAGMENT_MAIN_RECIPE_DETAILS;
        mFragment = new RecipeDetailsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(Constants.FRAGMENT_MAIN_RECIPE_DETAILS)
                .replace(R.id.fragment_placeholder, mFragment)
                .commit();
    }

    private void navigateToSearch() {
        mLastFragmentTag = Constants.FRAGMENT_MAIN_SEARCH;
        mFragment = new SearchRecipesFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment)
                .commit();
    }
}
