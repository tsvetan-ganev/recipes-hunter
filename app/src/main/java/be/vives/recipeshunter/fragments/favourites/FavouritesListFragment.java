package be.vives.recipeshunter.fragments.favourites;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.SwipeableRecipesRecyclerListAdapter;
import be.vives.recipeshunter.adapters.interactivity.OnItemDismissedListener;
import be.vives.recipeshunter.adapters.interactivity.TouchCallback;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.GetFavouriteRecipesAsyncTask;
import be.vives.recipeshunter.data.services.Promise;
import be.vives.recipeshunter.data.services.RemoveFavouriteRecipeAsyncTask;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class FavouritesListFragment extends Fragment {

    // data
    private ArrayList<RecipeEntity> mFavouriteRecipes;

    // UI widgets
    private ProgressBar mProgressBar;
    private TextView mEmptyListMessage;
    private RecyclerView mRecyclerView;
    private SwipeableRecipesRecyclerListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    // interaction listeners
    private FavouritesListFragmentListener mListener;
    private ItemTouchHelper mItemSwipeListener;

    // async task
    private GetFavouriteRecipesAsyncTask mAsyncTask;

    public FavouritesListFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.BUNDLE_ITEM_FAVOURITE_RECIPES, mFavouriteRecipes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFavouriteRecipes = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_ITEM_FAVOURITE_RECIPES);
        } else {
            mFavouriteRecipes = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        getActivity().setTitle(getString(R.string.favourite_recipes));

        // set up UI widgets
        mProgressBar = (ProgressBar) view.findViewById(R.id.favourites_list_progress_bar);
        mEmptyListMessage = (TextView) view.findViewById(R.id.favourites_list_empty_message);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favourites_list_recycler_view);

        mProgressBar.setVisibility(View.VISIBLE);
        if (mFavouriteRecipes.isEmpty()) {
            displayEmptyListMessage();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new SwipeableRecipesRecyclerListAdapter(mFavouriteRecipes);
        mRecyclerView.setAdapter(mAdapter);

        // set up async task
        mAsyncTask = new GetFavouriteRecipesAsyncTask(getActivity());
        mAsyncTask.delegate = new Promise<List<RecipeEntity>, Exception>() {
            @Override
            public void resolve(final List<RecipeEntity> result) {
                mFavouriteRecipes.addAll(result);
                mAdapter.notifyItemRangeInserted(0, result.size());
                mProgressBar.setVisibility(View.GONE);

                if (!result.isEmpty()) {
                    hideEmptyListMessage();
                }
            }

            public void reject(Exception error) {
                Snackbar.make(view, R.string.favourtes_couldnt_be_loaded, Snackbar.LENGTH_LONG).show();
            }
        };

        if (mFavouriteRecipes.isEmpty()) {
            mAsyncTask.execute();
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        // UI listeners
        setOnItemClickedListener(mRecyclerView);
        setOnItemDismissedListener(mAdapter);

        TouchCallback callback = new TouchCallback(mAdapter);
        mItemSwipeListener = new ItemTouchHelper(callback);
        mItemSwipeListener.attachToRecyclerView(mRecyclerView);

        return view;
    }

    private void hideEmptyListMessage() {
        mEmptyListMessage.setVisibility(View.GONE);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FavouritesListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + FavouritesListFragmentListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setOnItemDismissedListener(SwipeableRecipesRecyclerListAdapter adapter) {
        adapter.delegate = new OnItemDismissedListener<RecipeEntity>() {
            @Override
            public void remove(final RecipeEntity entity) {
                RemoveFavouriteRecipeAsyncTask removeFromFavouritesAsync =
                        new RemoveFavouriteRecipeAsyncTask(getContext(), entity);

                removeFromFavouritesAsync.delegate = new Promise<RecipeEntity, Exception>() {
                    @Override
                    public void resolve(RecipeEntity result) {
                        Snackbar.make(getView(),
                                result.getTitle() + getString(R.string.removed_from_favourites),
                                Snackbar.LENGTH_LONG)
                                .show();

                        if (mFavouriteRecipes.isEmpty()) {
                            displayEmptyListMessage();
                        }
                    }

                    @Override
                    public void reject(Exception error) {
                        Snackbar.make(getView(),
                                getString(R.string.couldnt_remove_recipe_from_db),
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                };

                removeFromFavouritesAsync.execute();
            }
        };
    }

    private void displayEmptyListMessage() {
        mEmptyListMessage.setVisibility(View.VISIBLE);
    }

    private void setOnItemClickedListener(RecyclerView recyclerView) {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                mListener.setRecipe(mFavouriteRecipes.get(position));
                mListener.navigateFromFavouritesList();
            }
        });
    }

    public interface FavouritesListFragmentListener {
        void setRecipe(RecipeEntity recipe);

        void navigateFromFavouritesList();
    }
}
