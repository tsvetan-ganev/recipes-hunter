package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.AddFavouriteRecipeAsyncTask;
import be.vives.recipeshunter.data.services.AsyncResponse;
import be.vives.recipeshunter.data.services.DownloadRecipeDetailsAsyncTask;
import be.vives.recipeshunter.data.viewmodels.RecipeAdditionalInfoViewModel;
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

    // interaction listener for MainActivity
    private RecipeDetailsFragmentListener mListener;

    // download recipe details async task
    private DownloadRecipeDetailsAsyncTask mAsyncTask;

    private AsyncResponse<RecipeAdditionalInfoViewModel> mAsyncTaskDelegate;

    public RecipeDetailsFragment() {
        mImageOptions = new DisplayImageOptions.Builder().build();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
            Log.d(this.getClass().getSimpleName(), "onActivityCreated: " + mCurrentRecipe.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE, mCurrentRecipe);
        outState.putString(Constants.BUNDLE_ITEM_LAST_FRAGMENT_VISITED, Constants.FRAGMENT_MAIN_RECIPE_DETAILS);

        Log.d(this.getClass().getSimpleName(), "onSaveInstanceState: " + mCurrentRecipe.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentRecipe = mListener.getSelectedRecipe();

        if (mCurrentRecipe == null && savedInstanceState != null) {
            mCurrentRecipe = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_SELECTED_RECIPE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        mCurrentRecipe = mListener.getSelectedRecipe();

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

        // set up ingredients list header
        View listViewHeader = inflater.inflate(R.layout.list_header, null);
        listViewHeader.setClickable(false);
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText("Ingredients");
        mIngredientsListView.addHeaderView(listViewHeader);

        // set up ingredients list view
        mAdapter = new ArrayAdapter<>(
                inflater.getContext(), R.layout.list_item_string, new ArrayList<String>());
        mIngredientsListView.setAdapter(mAdapter);
        mIngredientsListView.setOnItemClickListener(null);

        // set up the async task
        mAsyncTask = new DownloadRecipeDetailsAsyncTask(mCurrentRecipe.getId());
        mAsyncTaskDelegate = new AsyncResponse<RecipeAdditionalInfoViewModel>() {
            @Override
            public void resolve(RecipeAdditionalInfoViewModel result) {
                onRecipeDetailsDownloaded(result);
            }
        };
        mAsyncTask.delegate = mAsyncTaskDelegate;
        mAsyncTask.execute();

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private boolean isRecipeInFavourites(RecipeEntity recipe) {
        return getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_APPEND)
                .getBoolean(recipe.getId(), false);
    }

    private void onRecipeDetailsDownloaded(final RecipeAdditionalInfoViewModel recipeAdditionalInfo) {
        if (recipeAdditionalInfo.getIngredients() == null) {
            recipeAdditionalInfo.setIngredients(new ArrayList<String>());
        }

        mAdapter.addAll(recipeAdditionalInfo.getIngredients());
        mAdapter.notifyDataSetChanged();

        final RecipeDetailsViewModel recipeDetailsViewModel =
                new RecipeDetailsViewModel(mCurrentRecipe, recipeAdditionalInfo);

        LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);

        setFabOnClickListener(recipeDetailsViewModel);
        setShowInstructionsButtonOnClickListener(recipeAdditionalInfo);
    }

    private void setFabOnClickListener(final RecipeDetailsViewModel recipeDetailsViewModel) {
        if (!isRecipeInFavourites(mCurrentRecipe)) {
            mAddToFavouritesFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddFavouriteRecipeAsyncTask addToFavsAsyncTask = new AddFavouriteRecipeAsyncTask(
                            getContext(), recipeDetailsViewModel);
                    addToFavsAsyncTask.delegate = new AsyncResponse<Boolean>() {
                        @Override
                        public void resolve(Boolean result) {
                            mAddToFavouritesFab.setOnClickListener(null);
                            mAddToFavouritesFab.setImageDrawable(getActivity()
                                    .getResources()
                                    .getDrawable(R.drawable.ic_favorite_white_24dp));
                        }
                    };
                    addToFavsAsyncTask.execute();

                }
            });
        }
    }

    private void setShowInstructionsButtonOnClickListener(final RecipeAdditionalInfoViewModel recipeAdditionalInfo) {
        // set URL for the "view instructions" button
        mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlInBrowser(Uri.parse(recipeAdditionalInfo.getSourceUrl()));
            }

            public void openUrlInBrowser(Uri url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    public interface RecipeDetailsFragmentListener {
        RecipeEntity getSelectedRecipe();
    }
}
