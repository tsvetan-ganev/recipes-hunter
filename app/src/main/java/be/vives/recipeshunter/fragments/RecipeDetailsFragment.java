package be.vives.recipeshunter.fragments;

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

import java.util.concurrent.ExecutionException;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.activities.FavouritesActivity;
import be.vives.recipeshunter.activities.MainActivity;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.rest.DownloadRecipeDetailsAsyncTask;
import be.vives.recipeshunter.data.viewmodels.RecipeAdditionalInfoViewModel;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.utils.LayoutUtils;

public class RecipeDetailsFragment extends Fragment {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPublisherNameTextView;
    private ListView mIngredientsListView;
    private Button mOpenSourceUrlButton;
    private Button mAddToFavouritesButton;
    private TextView mSocialRankTextView;

    private DisplayImageOptions mImageOptions;

    private RecipeEntity mCurrentRecipe;

    private RecipeDetailsFragmentListener mListener;

    public RecipeDetailsFragment() {
        mImageOptions = new DisplayImageOptions.Builder().build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

//        currentRecipeEntity = mListener.getSelectedRecipe()
        mCurrentRecipe = mListener.getSelectedRecipe();

        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlButton = (Button) view.findViewById(R.id.recipe_details_source_url);
        mAddToFavouritesButton = (Button) view.findViewById(R.id.recipe_details_favs_button);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        final RecipeAdditionalInfoViewModel recipeAdditionalInfoViewModel;
        try {
            // Download recipe details
            recipeAdditionalInfoViewModel = new DownloadRecipeDetailsAsyncTask(mCurrentRecipe.getId()).execute().get();

            // Populate the details view model
            final RecipeDetailsViewModel recipeDetailsViewModel =
                    new RecipeDetailsViewModel(mCurrentRecipe, recipeAdditionalInfoViewModel);

            // set up text views
            mTitleTextView.setText(recipeDetailsViewModel.getTitle());
            mPublisherNameTextView.setText(recipeDetailsViewModel.getPublisherName());
            mSocialRankTextView.setText(recipeDetailsViewModel.getSocialRank() +  " / 100");
            ImageLoader.getInstance().displayImage(recipeDetailsViewModel.getImageUrl(), mImageView, mImageOptions);

            // set up ingredients list view
            View listViewHeader = this.getLayoutInflater(savedInstanceState).inflate(R.layout.list_header, null);
            TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
            listViewHeaderTextView.setText("Ingredients");
            mIngredientsListView.addHeaderView(listViewHeader);
            mIngredientsListView.setAdapter(new ArrayAdapter<>(
                    getContext(), R.layout.list_item_string, recipeDetailsViewModel.getIngredients()));
            mIngredientsListView.setOnItemClickListener(null);
            LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);

            // set URL for the "view instructions" button
            mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openUrlInBrowser(Uri.parse(recipeDetailsViewModel.getSourceUrl()));
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
                    Intent intent = new Intent(getContext(), FavouritesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("recipe_id", recipeDetailsViewModel.getId());
                    bundle.putString("recipe_title", recipeDetailsViewModel.getTitle());
                    bundle.putString("recipe_publisher_name", recipeDetailsViewModel.getPublisherName());
                    bundle.putString("recipe_img_url", recipeDetailsViewModel.getImageUrl());
                    bundle.putInt("recipe_social_rank", recipeDetailsViewModel.getSocialRank());
                    bundle.putString("recipe_src_url", recipeDetailsViewModel.getSourceUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

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
