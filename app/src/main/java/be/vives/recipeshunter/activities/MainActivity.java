package be.vives.recipeshunter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import be.vives.recipeshunter.R;
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

    private String mSearchQuery = "";
    private RecipeEntity mSelectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ImageLoader init
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        if (mSearchQuery == null || mSearchQuery.isEmpty()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_placeholder, new SearchRecipesFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_placeholder, new RecipesListFragment())
                    .addToBackStack("search")
                    .commit();
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

    // Listeners
    @Override
    public void setSearchQuery(String query) {
        mSearchQuery = query;
    }

    @Override
    public void navigateFromSearchSubmitFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, new RecipesListFragment())
                .addToBackStack("list")
                .commit();
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
    public void navigateToDetailsFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, new RecipeDetailsFragment())
                .addToBackStack("details")
                .commit();
    }

    @Override
    public RecipeEntity getSelectedRecipe() {
        return mSelectedRecipe;
    }
}
