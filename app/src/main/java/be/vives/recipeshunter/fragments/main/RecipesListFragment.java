package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.RecipesRecycleListAdapter;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.DownloadRecipesAsyncTask;
import be.vives.recipeshunter.data.services.Promise;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class RecipesListFragment extends Fragment {

    // widgets
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private RecipesRecycleListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private CardView mCardViewListContainer;
    private LinearLayout mNoConnectionError;

    // data
    private String mSearchQuery;
    private ArrayList<RecipeEntity> mRecipesList;

    // interaction listener for MainActivity
    private RecipesListFragmentListener mListener;

    // endless scrolling variables
    private int mCurrentPage = 1;
    private boolean mIsLoading = false;
    private boolean mIsEndReached = false;

    // async task for downloading recipes data
    private DownloadRecipesAsyncTask mAsyncTask;
    private Promise<List<RecipeEntity>, Exception> mAsyncTaskDelegate;

    public RecipesListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RecipesListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RecipesListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mListener.getQueryString() != null) {
            mSearchQuery = mListener.getQueryString();
        }

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
            mRecipesList = savedInstanceState.getParcelableArrayList(Constants.BUNDLE_ITEM_RECIPES_LIST);
            mCurrentPage = savedInstanceState.getInt(Constants.BUNDLE_ITEM_CURRENT_PAGE, 1);
        }

        mIsEndReached = false;
        if (mRecipesList == null) {
            mRecipesList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        // widgets set up
        mProgressBar = (ProgressBar) view.findViewById(R.id.recipes_list_progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipes_list_recycler_view);
        mNoConnectionError = (LinearLayout) view.findViewById(R.id.recipes_list_no_connection_error);
        mCardViewListContainer = (CardView) view.findViewById(R.id.recipes_list_recycler_view_card_container);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecipesRecycleListAdapter(mRecipesList);
        mRecyclerView.setAdapter(mAdapter);

        // prepare the async task
        mAsyncTask = new DownloadRecipesAsyncTask(mSearchQuery);
        mAsyncTaskDelegate = new Promise<List<RecipeEntity>, Exception>() {
            @Override
            public void resolve(List<RecipeEntity> result) {
                if (result.isEmpty()) {
                    mIsEndReached = true;
                    mIsLoading = false;
                    return;
                }

                int currentItemsCount = mRecipesList.size() - 1;
                mRecipesList.addAll(result);
                mAdapter.notifyItemRangeInserted(currentItemsCount, result.size());
                mCurrentPage += 1;
                mIsLoading = false;
                hideProgressBar();
                updateConnectionStatusInToolbar();
            }

            @Override
            public void reject(Exception error) {
                mIsLoading = false;
                hideProgressBar();

                if (mRecipesList.isEmpty()) {
                    mNoConnectionError.setVisibility(View.VISIBLE);
                    mCardViewListContainer.setVisibility(View.GONE);
                } else {
                    Snackbar.make(view, "Server couldn't be reached.", Snackbar.LENGTH_SHORT).show();
                }

                updateConnectionStatusInToolbar();
            }
        };
        mAsyncTask.delegate = mAsyncTaskDelegate;

        addOnItemClickListenerToRecycleView(mRecyclerView);
        addOnScrolledToBottomListenerToRecycleView(mRecyclerView);

        if (taskCanBeExecuted()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAsyncTask.execute();
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.BUNDLE_ITEM_SEARCH_QUERY, mSearchQuery);
        outState.putInt(Constants.BUNDLE_ITEM_CURRENT_PAGE, mCurrentPage);
        outState.putParcelableArrayList(Constants.BUNDLE_ITEM_RECIPES_LIST, mRecipesList);

        outState.putString(Constants.BUNDLE_ITEM_LAST_FRAGMENT_VISITED, Constants.FRAGMENT_MAIN_RECIPES_LIST);
    }

    private void addOnItemClickListenerToRecycleView(RecyclerView recyclerView) {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                mListener.setRecipe(mRecipesList.get(position));
                mListener.navigateToDetailsFragment();
            }
        });
    }

    private void addOnScrolledToBottomListenerToRecycleView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)
                        && !mIsLoading && !mIsEndReached) {
                    onScrolledToBottom();
                }
            }
        });
    }

    private void onScrolledToBottom() {
        String newQuery = mSearchQuery + "&page=" + Integer.toString(mCurrentPage);

        if (!mIsLoading) {
            mIsLoading = true;
            mAsyncTask = new DownloadRecipesAsyncTask(newQuery);
            mAsyncTask.delegate = mAsyncTaskDelegate;
            Log.d(getClass().getSimpleName(), "onScrolledToBottom: p." + mCurrentPage);
            mAsyncTask.execute();
        }
    }

    private boolean taskCanBeExecuted() {
        return (mAsyncTask.getStatus() == AsyncTask.Status.PENDING
                && mSearchQuery != null
                && !mIsEndReached
                && mRecipesList.isEmpty());
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void updateConnectionStatusInToolbar() {
        if (isAdded()) {
            getActivity().invalidateOptionsMenu();
        }
    }

    public interface RecipesListFragmentListener {
        String getQueryString();

        void setRecipe(RecipeEntity recipe);

        void navigateToDetailsFragment();
    }
}
