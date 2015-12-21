package be.vives.recipeshunter.fragments.favourites;


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
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.local.dao.IngredientDAO;
import be.vives.recipeshunter.data.local.dao.impl.IngredientDAOImpl;
import be.vives.recipeshunter.data.services.AsyncResponse;
import be.vives.recipeshunter.data.services.GetFavouriteRecipesAsyncTask;
import be.vives.recipeshunter.data.services.GetIngredientsByRecipeIdAsyncTask;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.utils.LayoutUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesRecipeDetailsFragment extends Fragment implements AsyncResponse<List<String>> {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPublisherNameTextView;
    private ListView mIngredientsListView;
    private Button mOpenSourceUrlButton;
    private TextView mSocialRankTextView;

    private FavouritesRecipeDetailsListener mListener;
    private GetIngredientsByRecipeIdAsyncTask mAsyncTask;

    private Bundle mSavedInstance;

    public FavouritesRecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_recipe_details, container, false);

        mSavedInstance = savedInstanceState;
        final RecipeDetailsViewModel recipeDetails = mListener.getRecipeDetails();

        mAsyncTask = new GetIngredientsByRecipeIdAsyncTask(getActivity(), recipeDetails.getId());
        mAsyncTask.delegate = this;
        mAsyncTask.execute();

        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlButton = (Button) view.findViewById(R.id.recipe_details_source_url);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);


        mTitleTextView.setText(recipeDetails.getTitle());
        mPublisherNameTextView.setText(recipeDetails.getPublisherName());
        mSocialRankTextView.setText(recipeDetails.getSocialRank() + " / 100");
        ImageLoader.getInstance().displayImage(recipeDetails.getImageUrl(), mImageView, DisplayImageOptions.createSimple());

        // set URL for the "view instructions" button
        mOpenSourceUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlInBrowser(Uri.parse(recipeDetails.getSourceUrl()));
            }

            public void openUrlInBrowser(Uri url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FavouritesRecipeDetailsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void resolve(List<String> result) {
        // ingredients list view
        View listViewHeader = this.getLayoutInflater(mSavedInstance).inflate(R.layout.list_header, null);
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText("Ingredients");

        mIngredientsListView.setAdapter(new ArrayAdapter<>(
                getContext(), R.layout.list_item_string, result));
        mIngredientsListView.setOnItemClickListener(null);
        mIngredientsListView.addHeaderView(listViewHeader);
        LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
    }

    public interface FavouritesRecipeDetailsListener {
        RecipeDetailsViewModel getRecipeDetails();
    }
}
