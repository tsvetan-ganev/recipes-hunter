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
import be.vives.recipeshunter.data.DownloadRecipeDetailsAsyncTask;
import be.vives.recipeshunter.models.Recipe;
import be.vives.recipeshunter.utils.LayoutUtils;

public class RecipeDetailsFragment extends Fragment {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPublisherNameTextView;
    private ListView mIngredientsListView;
    private Button mOpenSourceUrlButton;
    private TextView mSocialRankTextView;

    private DisplayImageOptions mImageOptions;

    private Recipe mCurrentRecipe;
    private RecipeDetailsFragmentListener mListener;

    public RecipeDetailsFragment() {
        mImageOptions = new DisplayImageOptions.Builder().build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        mCurrentRecipe = mListener.getSelectedRecipe();

        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlButton = (Button) view.findViewById(R.id.recipe_details_source_url);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        // todo: reuse image
        mTitleTextView.setText(mCurrentRecipe.getTitle());
        mPublisherNameTextView.setText(mCurrentRecipe.getPublisherName());
        mSocialRankTextView.setText(Integer.toString(mCurrentRecipe.getSocialRank()) + " / 100");
        ImageLoader.getInstance().displayImage(mCurrentRecipe.getImageUrl().toString(), mImageView, mImageOptions);

        mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openUrlInBrowser(Uri.parse(mCurrentRecipe.getSourceUrl().toString()));
            }

            public void openUrlInBrowser(Uri url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        Recipe recipeDetails;
        try {
            // Download recipe details
            recipeDetails = new DownloadRecipeDetailsAsyncTask(mCurrentRecipe.getId()).execute().get();

            // Add ingredients list view header
            View listViewHeader = this.getLayoutInflater(savedInstanceState).inflate(R.layout.list_header, null);
            TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
            listViewHeaderTextView.setText("Ingredients");
            mIngredientsListView.addHeaderView(listViewHeader);

            mIngredientsListView.setAdapter(new ArrayAdapter<>(
                    getContext(), R.layout.list_item_string, recipeDetails.getIngredients()));

            mIngredientsListView.setOnItemClickListener(null);

            LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
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
            throw new ClassCastException(activity.toString()
                    + " must implement " + RecipeDetailsFragmentListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface RecipeDetailsFragmentListener {
        Recipe getSelectedRecipe();
    }
}
