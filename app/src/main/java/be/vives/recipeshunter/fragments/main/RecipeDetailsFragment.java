package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import be.vives.recipeshunter.activities.FavouritesActivity;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
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
    private Button mAddToFavouritesButton;
    private TextView mSocialRankTextView;

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
        mAddToFavouritesButton = (Button) view.findViewById(R.id.recipe_details_favs_button);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        // set the known recipe properties
        mTitleTextView.setText(mCurrentRecipe.getTitle());
        mPublisherNameTextView.setText(mCurrentRecipe.getPublisherName());
        mSocialRankTextView.setText(mCurrentRecipe.getSocialRank() + " / 100");
        ImageLoader.getInstance().displayImage(mCurrentRecipe.getImageUrl(), mImageView, mImageOptions);

        // set up the async task
        mAsyncTask = new DownloadRecipeDetailsAsyncTask(mCurrentRecipe.getId());
        mAsyncTaskDelegate = new AsyncResponse<RecipeAdditionalInfoViewModel>() {
            @Override
            public void resolve(RecipeAdditionalInfoViewModel result) {
                final RecipeAdditionalInfoViewModel recipeAdditionalInfoViewModel = result;

                if (recipeAdditionalInfoViewModel.getIngredients() == null) {
                    recipeAdditionalInfoViewModel.setIngredients(new ArrayList<String>());
                }

                // set up ingredients list header
                View listViewHeader = inflater.inflate(R.layout.list_header, null);
                TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
                listViewHeaderTextView.setText("Ingredients");
                mIngredientsListView.addHeaderView(listViewHeader);

                // set up ingredients list view
                mIngredientsListView.setAdapter(new ArrayAdapter<>(
                        inflater.getContext(), R.layout.list_item_string, recipeAdditionalInfoViewModel.getIngredients()));
                mIngredientsListView.setOnItemClickListener(null);
                LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);

                // set URL for the "view instructions" button
                mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openUrlInBrowser(Uri.parse(recipeAdditionalInfoViewModel.getSourceUrl()));
                    }

                    public void openUrlInBrowser(Uri url) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, url);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });

                mAddToFavouritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final RecipeDetailsViewModel recipeDetailsViewModel =
                                new RecipeDetailsViewModel(mCurrentRecipe, recipeAdditionalInfoViewModel);

                        Intent intent = new Intent(getContext(), FavouritesActivity.class);
                        intent.putExtras(createRecipeBundle(recipeDetailsViewModel));

                        startActivity(intent);
                    }
                });
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

    private Bundle createRecipeBundle(RecipeDetailsViewModel recipeDetailsViewModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS, recipeDetailsViewModel);

        return bundle;
    }

    public interface RecipeDetailsFragmentListener {
        RecipeEntity getSelectedRecipe();
    }
}
