package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.AddFavouriteRecipeAsyncTask;
import be.vives.recipeshunter.data.services.DownloadRecipeIngredientsAsyncTask;
import be.vives.recipeshunter.data.services.Promise;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.utils.LayoutUtils;

public class RecipeDetailsFragment extends Fragment {

    // widgets
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPublisherNameTextView;
    private ListView mIngredientsListView;
    private Button mOpenSourceUrlButton;
    private FloatingActionButton mAddToFavouritesFab;
    private TextView mSocialRankTextView;

    private ArrayAdapter<String> mAdapter;

    // data
    private DisplayImageOptions mImageOptions;
    private RecipeEntity mCurrentRecipe;
    private ArrayList<String> mIngredientsList;

    // interaction listener for MainActivity
    private RecipeDetailsFragmentListener mListener;

    // download recipe details async task
    private DownloadRecipeIngredientsAsyncTask mDownloadIngredientsAsyncTask;

    private AddFavouriteRecipeAsyncTask mAddToFavouritesAsyncTask;

    private Promise<List<String>, Exception> mAsyncTaskDelegate;

    public RecipeDetailsFragment() {
        mImageOptions = new DisplayImageOptions.Builder().build();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RecipeDetailsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
            mIngredientsList = savedInstanceState.getStringArrayList(Constants.BUNDLE_ITEM_INGREDIENTS_LIST);
        } else {
            mCurrentRecipe = mListener.getSelectedRecipe();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        getActivity().setTitle(getResources().getString(R.string.details));

        // set up UI widgets
        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlButton = (Button) view.findViewById(R.id.recipe_details_source_url);
        mAddToFavouritesFab = (FloatingActionButton) view.findViewById(R.id.recipe_details_add_to_favs_fab);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        // set fab
        if (isRecipeInFavourites(mCurrentRecipe)) {
            mAddToFavouritesFab.setImageDrawable(getActivity()
                    .getResources()
                    .getDrawable(R.drawable.ic_favorite_white_24dp));
        }

        // set the known recipe properties
        mTitleTextView.setText(mCurrentRecipe.getTitle());
        mPublisherNameTextView.setText(mCurrentRecipe.getPublisherName());
        mSocialRankTextView.setText(mCurrentRecipe.getSocialRank() + " / 100");
        ImageLoader.getInstance().displayImage(mCurrentRecipe.getImageUrl(), mImageView, mImageOptions);

        // set onclick listeners
        setShowInstructionsButtonOnClickListener(mCurrentRecipe);
        setFabOnClickListener();

        // set up ingredients list header
        View listViewHeader = inflater.inflate(R.layout.list_header, null);
        listViewHeader.setClickable(false);
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText("Ingredients");
        mIngredientsListView.addHeaderView(listViewHeader);

        // set up ingredients list view
        mAdapter = new ArrayAdapter<>(
                inflater.getContext(),
                R.layout.list_item_string,
                (mIngredientsList != null) ? mIngredientsList : new ArrayList<String>());
        mIngredientsListView.setAdapter(mAdapter);
        mIngredientsListView.setOnItemClickListener(null);
        LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);

        if (mIngredientsList != null) {
            mIngredientsListView.setVisibility(View.VISIBLE);
        }

        // set up the async task
        mDownloadIngredientsAsyncTask = new DownloadRecipeIngredientsAsyncTask(mCurrentRecipe.getId());
        mAsyncTaskDelegate = new Promise<List<String>, Exception>() {
            @Override
            public void resolve(List<String> result) {
                updateIngredientsListView(result);
                updateConnectionStatusInToolbar();
            }

            @Override
            public void reject(Exception error) {
                updateConnectionStatusInToolbar();

                Snackbar.make(view,
                        "Could not download recipe ingredients.",
                        Snackbar.LENGTH_LONG)
                        .show();

                if (isRecipeInFavourites(mCurrentRecipe)) {
                    return;
                }

                mAddToFavouritesFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View fab) {
                        Snackbar.make(view, "Can't add to favourites because ingredients are missing.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        };
        mDownloadIngredientsAsyncTask.delegate = mAsyncTaskDelegate;

        if (mIngredientsList == null && taskCanBeExecuted()) {
            Log.d(getClass().getSimpleName(), "onCreateView: Executing task.");
            mDownloadIngredientsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        } else {
            Log.d(getClass().getSimpleName(), "onCreateView: " + String.valueOf(mIngredientsList));
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE, mCurrentRecipe);
        outState.putStringArrayList(Constants.BUNDLE_ITEM_INGREDIENTS_LIST, mIngredientsList);
    }

    private boolean isRecipeInFavourites(RecipeEntity recipe) {
        SharedPreferences preferences = getContext().getSharedPreferences(
                Constants.PREFERENCES_FAVOURITE_RECIPES, Context.MODE_PRIVATE);
        return preferences.getBoolean(recipe.getId(), false);
    }

    private void updateIngredientsListView(final List<String> ingredients) {
        if (ingredients == null) {
            return;
        }
        mIngredientsList = (ArrayList<String>) ingredients;
        mAdapter.addAll(mIngredientsList);
        mAdapter.notifyDataSetChanged();
        LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
        mIngredientsListView.setVisibility(View.VISIBLE);
    }

    private void setFabOnClickListener() {
        if (isRecipeInFavourites(mCurrentRecipe)) {
            return;
        }

        mAddToFavouritesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIngredientsList == null || mIngredientsList.isEmpty()) {
                    Snackbar.make(getView(),
                            "Cannot add to favourites, because ingredients data is missing."
                            ,Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                final RecipeDetailsViewModel recipeDetails = new RecipeDetailsViewModel(mCurrentRecipe, mIngredientsList);
                mAddToFavouritesAsyncTask = new AddFavouriteRecipeAsyncTask(
                        getContext(), recipeDetails);

                mAddToFavouritesAsyncTask.delegate = new Promise<Boolean, Exception>() {
                    @Override
                    public void resolve(Boolean result) {
                        mAddToFavouritesFab.setOnClickListener(null);
                        mAddToFavouritesFab.setClickable(false);
                        mAddToFavouritesFab.setImageDrawable(getActivity()
                                .getResources()
                                .getDrawable(R.drawable.ic_favorite_white_24dp));

                        Snackbar.make(getView(),
                                recipeDetails.getTitle() + " added to favourites.",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void reject(Exception error) {
                        Snackbar.make(getView(),
                                "Couldn't add recipe to favourites.",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                };
                mAddToFavouritesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });
    }

    private void setShowInstructionsButtonOnClickListener(final RecipeEntity recipe) {
        // set URL for the "view instructions" button
        mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlInBrowser(Uri.parse(recipe.getSourceUrl()));
            }

            public void openUrlInBrowser(Uri url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void updateConnectionStatusInToolbar() {
        if (isAdded()) {
            getActivity().invalidateOptionsMenu();
        }
    }

    private boolean taskCanBeExecuted() {
        return (mDownloadIngredientsAsyncTask.getStatus() == AsyncTask.Status.PENDING);
    }

    public interface RecipeDetailsFragmentListener {
        RecipeEntity getSelectedRecipe();
    }
}
