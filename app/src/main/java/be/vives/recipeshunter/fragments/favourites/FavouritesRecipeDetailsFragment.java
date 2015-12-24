package be.vives.recipeshunter.fragments.favourites;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.services.AsyncResponse;
import be.vives.recipeshunter.data.services.GetIngredientsByRecipeIdAsyncTask;
import be.vives.recipeshunter.data.viewmodels.RecipeDetailsViewModel;
import be.vives.recipeshunter.utils.LayoutUtils;

public class FavouritesRecipeDetailsFragment extends Fragment {

    // UI Widgets
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPublisherNameTextView;
    private ListView mIngredientsListView;
    private FloatingActionButton mOpenSourceUrlFab;
    private TextView mSocialRankTextView;

    // interactions listeners
    private FavouritesRecipeDetailsListener mListener;
    private GetIngredientsByRecipeIdAsyncTask mAsyncTask;

    // data
    private RecipeDetailsViewModel mRecipeDetails;
    private List<String> mIngredientsList;
    private Bundle mSavedInstance;

    private ArrayAdapter<String> mAdapter;

    public FavouritesRecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS, mRecipeDetails);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
        }

        if (mRecipeDetails != null && mRecipeDetails.getIngredients() == null) {
            mRecipeDetails.setIngredients(new ArrayList<String>());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_recipe_details, container, false);

        mSavedInstance = savedInstanceState;
        if (mRecipeDetails == null) {
            mRecipeDetails = mListener.getRecipeDetails();
        }

        mAsyncTask = new GetIngredientsByRecipeIdAsyncTask(getActivity(), mRecipeDetails.getId());
        mAsyncTask.delegate = new AsyncResponse<List<String>>() {
            @Override
            public void resolve(List<String> result) {
                if (result != null) {
                    mRecipeDetails.setIngredients(result);
                    mAdapter.addAll(result);
                    mAdapter.notifyDataSetChanged();
                }

                LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
            }
        };
        mAsyncTask.execute();

        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlFab = (FloatingActionButton) view.findViewById(R.id.recipe_details_open_in_browser_fab);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        // set up list view header
        View listViewHeader = inflater.inflate(R.layout.list_header, null);
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText("Ingredients");
        mIngredientsListView.addHeaderView(listViewHeader);

        // set up list view content
        mAdapter = new ArrayAdapter<>(inflater.getContext(),
                R.layout.list_item_string,
                new ArrayList<String>());
        mIngredientsListView.setAdapter(mAdapter);

        mIngredientsListView.setOnItemClickListener(null);

        mTitleTextView.setText(mRecipeDetails.getTitle());
        mPublisherNameTextView.setText(mRecipeDetails.getPublisherName());
        mSocialRankTextView.setText(mRecipeDetails.getSocialRank() + " / 100");
        ImageLoader.getInstance().displayImage(mRecipeDetails.getImageUrl(), mImageView, DisplayImageOptions.createSimple());

        // set URL for the "view instructions" button
        mOpenSourceUrlFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlInBrowser(Uri.parse(mRecipeDetails.getSourceUrl()));
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

    public interface FavouritesRecipeDetailsListener {
        RecipeDetailsViewModel getRecipeDetails();
    }
}
