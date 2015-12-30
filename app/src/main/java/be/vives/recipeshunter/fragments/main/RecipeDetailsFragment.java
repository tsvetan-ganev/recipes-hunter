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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.AddFavouriteRecipeAsyncTask;
import be.vives.recipeshunter.data.services.RemoveFavouriteRecipeAsyncTask;
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
    private ProgressBar mProgressBar;

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

    private RemoveFavouriteRecipeAsyncTask mRemoveFavouriteRecipeAsyncTask;

    private Promise<RecipeEntity, Exception> mAddedToFavouritesDelegate = new Promise<RecipeEntity, Exception>() {
            @Override
            public void resolve(RecipeEntity result) {
                mAddToFavouritesFab.setImageDrawable(getActivity()
                        .getResources()
                        .getDrawable(R.drawable.ic_favorite_white_24dp));

                Snackbar.make(getView(),
                        result.getTitle() + getString(R.string.added_to_favourites),
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void reject(Exception error) {
                Snackbar errorMessage = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
                if (error.getMessage().contains("ingredients cannot be null")) {
                    errorMessage.setText(getString(R.string.cannot_add_to_favourites));
                } else {
                    errorMessage.setText("Couldn't add recipe to favourites.");
                }

                errorMessage.show();
            }
    };

    private Promise<RecipeEntity, Exception> mRemovedFavouriteRecipeDelegate = new Promise<RecipeEntity, Exception>() {
        @Override
        public void resolve(RecipeEntity result) {
            setFabOnClickListener();
            mAddToFavouritesFab.setImageDrawable(getActivity()
                    .getResources()
                    .getDrawable(R.drawable.ic_favorite_border_white_24_dp));
            Snackbar.make(getView(), result.getTitle() + getString(R.string.removed_from_favourites), Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void reject(Exception error) {
            Snackbar.make(getView(), getString(R.string.couldnt_remove_recipe_from_db), Snackbar.LENGTH_LONG).show();
        }
    };

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

        getActivity().setTitle(getString(R.string.details));

        // set up UI widgets
        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlButton = (Button) view.findViewById(R.id.recipe_details_source_url);
        mAddToFavouritesFab = (FloatingActionButton) view.findViewById(R.id.recipe_details_add_to_favs_fab);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);
        mProgressBar = (ProgressBar) view.findViewById(R.id.recipe_details_progress_bar);

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
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText(R.string.ingredients_list_view_header);
        mIngredientsListView.addHeaderView(listViewHeader);

        // set up ingredients list view
        mAdapter = new ArrayAdapter<>(
                inflater.getContext(),
                R.layout.list_item_string,
                (mIngredientsList != null) ? mIngredientsList : new ArrayList<String>());
        mIngredientsListView.setAdapter(mAdapter);
        mIngredientsListView.setOnItemClickListener(null);

        if (mIngredientsList != null) {
            mProgressBar.setVisibility(View.GONE);
            mIngredientsListView.setVisibility(View.VISIBLE);
        }

        // set up the async task
        mDownloadIngredientsAsyncTask = new DownloadRecipeIngredientsAsyncTask(mCurrentRecipe.getId());
        mDownloadIngredientsAsyncTask.delegate = new Promise<List<String>, Exception>() {
            @Override
            public void resolve(List<String> result) {
                hideProgressBar();
                updateIngredientsListView(result);
                updateConnectionStatusInToolbar();
            }

            @Override
            public void reject(Exception error) {
                hideProgressBar();
                updateConnectionStatusInToolbar();
                Snackbar.make(view,
                        R.string.cannot_download_recipe_ingredients,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        };

        if (mIngredientsList == null && taskCanBeExecuted()) {
            mDownloadIngredientsAsyncTask.execute();
        }

        return view;
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
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
        mAddToFavouritesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecipeInFavourites(mCurrentRecipe)) {
                    mRemoveFavouriteRecipeAsyncTask = new RemoveFavouriteRecipeAsyncTask(getContext(), mCurrentRecipe);
                    mRemoveFavouriteRecipeAsyncTask.delegate = mRemovedFavouriteRecipeDelegate;
                    mRemoveFavouriteRecipeAsyncTask.execute();
                } else {
                    final RecipeDetailsViewModel recipeDetails = new RecipeDetailsViewModel(mCurrentRecipe, mIngredientsList);

                    mAddToFavouritesAsyncTask = new AddFavouriteRecipeAsyncTask(
                            getContext(), recipeDetails);
                    mAddToFavouritesAsyncTask.delegate = mAddedToFavouritesDelegate;
                    mAddToFavouritesAsyncTask.execute();
                }
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
