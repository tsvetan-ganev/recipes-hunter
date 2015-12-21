package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private Bundle mSavedInstance;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        mSavedInstance = savedInstanceState;

        mCurrentRecipe = mListener.getSelectedRecipe();

        mAsyncTask = new DownloadRecipeDetailsAsyncTask(mCurrentRecipe.getId());
        // TODODOTODO
        mAsyncTaskDelegate = new AsyncResponse<RecipeAdditionalInfoViewModel>() {
            @Override
            public void resolve(RecipeAdditionalInfoViewModel result) {
                final RecipeAdditionalInfoViewModel recipeAdditionalInfoViewModel;

                // Download recipe details
                recipeAdditionalInfoViewModel = result;

                if (recipeAdditionalInfoViewModel.getIngredients() == null) {
                    recipeAdditionalInfoViewModel.setIngredients(new ArrayList<String>());
                }

                // set up ingredients list header
                View listViewHeader = getLayoutInflater(mSavedInstance).inflate(R.layout.list_header, null);
                TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
                listViewHeaderTextView.setText("Ingredients");
                mIngredientsListView.addHeaderView(listViewHeader);

                // set up ingredients list view
                mIngredientsListView.setAdapter(new ArrayAdapter<>(
                        getContext(), R.layout.list_item_string, recipeAdditionalInfoViewModel.getIngredients()));
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
                        Bundle bundle = new Bundle();
                        bundle.putString("recipe_id", recipeDetailsViewModel.getId());
                        bundle.putString("recipe_title", recipeDetailsViewModel.getTitle());
                        bundle.putString("recipe_publisher_name", recipeDetailsViewModel.getPublisherName());
                        bundle.putString("recipe_img_url", recipeDetailsViewModel.getImageUrl());
                        bundle.putInt("recipe_social_rank", recipeDetailsViewModel.getSocialRank());
                        bundle.putString("recipe_src_url", recipeDetailsViewModel.getSourceUrl());
                        bundle.putStringArrayList("recipe_ingredients", (ArrayList<String>) recipeAdditionalInfoViewModel.getIngredients());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        mAsyncTask.delegate = mAsyncTaskDelegate;
        mAsyncTask.execute();

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

    public interface RecipeDetailsFragmentListener {
        RecipeEntity getSelectedRecipe();
    }
}
