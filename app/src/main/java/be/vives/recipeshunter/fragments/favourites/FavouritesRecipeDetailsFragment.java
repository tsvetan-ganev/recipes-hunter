package be.vives.recipeshunter.fragments.favourites;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.services.Promise;
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

    private ArrayAdapter<String> mAdapter;

    public FavouritesRecipeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS, mRecipeDetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_recipe_details, container, false);

        getActivity().setTitle(getString(R.string.details));

        mRecipeDetails = mListener.getRecipeDetails();

        if (mRecipeDetails.getIngredients() == null && savedInstanceState != null) {
            mRecipeDetails = savedInstanceState.getParcelable(Constants.BUNDLE_ITEM_RECIPE_DETAILS);
        }

        mImageView = (ImageView) view.findViewById(R.id.recipe_details_image);
        mTitleTextView = (TextView) view.findViewById(R.id.recipe_details_title);
        mIngredientsListView = (ListView) view.findViewById(R.id.recipe_details_ingredients_list);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.recipe_details_publisher_name);
        mOpenSourceUrlFab = (FloatingActionButton) view.findViewById(R.id.recipe_details_open_in_browser_fab);
        mSocialRankTextView = (TextView) view.findViewById(R.id.recipe_details_social_rank);

        // set up list view header
        addHeaderToIngredientsListView(inflater);

        // set up list view content
        setUpIngredientsListViewContent(inflater);

        // set the known properties
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

        // set up the async task
        mAsyncTask = new GetIngredientsByRecipeIdAsyncTask(getActivity(), mRecipeDetails.getId());
        mAsyncTask.delegate = new Promise<List<String>, Exception>() {
            @Override
            public void resolve(List<String> result) {
                if (result != null) {
                    mRecipeDetails.setIngredients(result);
                    mAdapter.addAll(result);
                    mAdapter.notifyDataSetChanged();
                }

                LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
            }

            @Override
            public void reject(Exception error) {
                Snackbar.make(getView(), getString(R.string.ingredients_cannot_be_loaded), Snackbar.LENGTH_LONG).show();
            }
        };

        if (mRecipeDetails.getIngredients() == null) {
            mAsyncTask.execute();
        }

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

    private void addHeaderToIngredientsListView(LayoutInflater inflater) {
        View listViewHeader = inflater.inflate(R.layout.list_header, null);
        TextView listViewHeaderTextView = (TextView) listViewHeader.findViewById(R.id.list_view_header);
        listViewHeaderTextView.setText(R.string.ingredients_list_view_header);
        mIngredientsListView.addHeaderView(listViewHeader);
    }

    private void setUpIngredientsListViewContent(LayoutInflater inflater) {
        List<String> ingredients;
        if (mRecipeDetails.getIngredients() != null) {
            ingredients = mRecipeDetails.getIngredients();
        } else {
            ingredients = new ArrayList<>();
        }
        mAdapter = new ArrayAdapter<>(inflater.getContext(),
                R.layout.list_item_string,
                ingredients);
        mIngredientsListView.setAdapter(mAdapter);

        mIngredientsListView.setOnItemClickListener(null);
        LayoutUtils.setListViewHeightBasedOnItems(mIngredientsListView);
    }

    public interface FavouritesRecipeDetailsListener {
        RecipeDetailsViewModel getRecipeDetails();
    }
}
