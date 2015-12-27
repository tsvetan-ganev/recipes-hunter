package be.vives.recipeshunter.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.fragments.main.RecipeDetailsFragment;
import be.vives.recipeshunter.fragments.main.RecipeDetailsFragment.RecipeDetailsFragmentListener;
import be.vives.recipeshunter.fragments.main.RecipeListFragment;
import be.vives.recipeshunter.fragments.main.RecipeListFragment.RecipesListFragmentListener;
import be.vives.recipeshunter.fragments.main.RecipeSearchFragment;
import be.vives.recipeshunter.fragments.main.RecipeSearchFragment.OnSearchSubmitFragmentListener;

public class MainActivity extends AppCompatActivity
        implements RecipesListFragmentListener,
        OnSearchSubmitFragmentListener,
        RecipeDetailsFragmentListener {

    private MenuItem mConnectionStatus;

    // data
    private String mSearchQuery;
    private RecipeEntity mSelectedRecipe;

    // fragment state
    private Fragment mFragment;
    private String mLastFragmentTag;

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

        setContentView(R.layout.layout_container);

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
        mFragment = new RecipeListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment, mLastFragmentTag)
                .commit();
    }

    @Override
    public void navigateToDetailsFragment() {
        mLastFragmentTag = Constants.FRAGMENT_MAIN_RECIPE_DETAILS;
        mFragment = new RecipeDetailsFragment();

        setTitle("Details");
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment, mLastFragmentTag)
                .commit();
    }

    private void navigateToSearch() {
        mLastFragmentTag = Constants.FRAGMENT_MAIN_SEARCH;
        mFragment = new RecipeSearchFragment();

        setTitle(getResources().getString(R.string.app_name));
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(mLastFragmentTag)
                .replace(R.id.fragment_placeholder, mFragment, mLastFragmentTag)
                .commit();
    }
}
