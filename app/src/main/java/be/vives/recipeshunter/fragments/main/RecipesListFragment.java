package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.RecipesRecycleListAdapter;
import be.vives.recipeshunter.data.Constants;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.AsyncResponse;
import be.vives.recipeshunter.data.services.DownloadRecipesAsyncTask;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class RecipesListFragment extends Fragment {

    // widgets
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private RecipesRecycleListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    // data
    private String mSearchQuery;
    private List<RecipeEntity> mRecipesList;

    // interaction listener for MainActivity
    private RecipesListFragmentListener mListener;

    // endless scrolling variables
    private int mCurrentPage = 1;
    private boolean mIsLoading = false;
    private boolean mIsEndReached = false;

    // async task for downloading recipes data
    private DownloadRecipesAsyncTask mAsyncTask;
    private AsyncResponse<List<RecipeEntity>> mAsyncTaskDelegate;

    public RecipesListFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.BUNDLE_ITEM_SEARCH_QUERY, mSearchQuery);
        outState.putString(Constants.BUNDLE_ITEM_LAST_FRAGMENT_VISITED, Constants.FRAGMENT_MAIN_RECIPES_LIST);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(Constants.BUNDLE_ITEM_SEARCH_QUERY);
            mAsyncTask = new DownloadRecipesAsyncTask(mSearchQuery);
            mCurrentPage = 0;
            mIsEndReached = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mListener.getQueryString() != null) {
            mSearchQuery = mListener.getQueryString();
        }

        mRecipesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        // widgets set up
        mProgressBar = (ProgressBar) view.findViewById(R.id.recipes_list_progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipes_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (mRecipesList.size() > 0) {
            mAdapter = new RecipesRecycleListAdapter(mRecipesList);
            mRecyclerView.setAdapter(mAdapter);
        }

        mAsyncTask = new DownloadRecipesAsyncTask(mSearchQuery);

        mAsyncTaskDelegate = new AsyncResponse<List<RecipeEntity>>() {
            @Override
            public void resolve(List<RecipeEntity> result) {
                if (result.isEmpty()) {
                    mIsEndReached = true;
                    mIsLoading = false;
                    return;
                }

                mRecipesList.addAll(result);

                mCurrentPage += 1;

                if (mAdapter == null) {
                    mAdapter = new RecipesRecycleListAdapter(mRecipesList);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }

                mIsLoading = false;
                mProgressBar.setVisibility(View.GONE);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                if (!recyclerView.canScrollVertically(1) && !mIsLoading) {
                    onScrolledToBottom();
                }
            }
        });
    }

    private void onScrolledToBottom() {
        if (mIsEndReached) {
            return;
        }

        String newQuery = mSearchQuery + "&page=" + Integer.toString(mCurrentPage);

        if (!mIsLoading) {
            mIsLoading = true;
            mAsyncTask = new DownloadRecipesAsyncTask(newQuery);
            mAsyncTask.delegate = mAsyncTaskDelegate;
            mAsyncTask.execute();
        }
    }

    private boolean taskCanBeExecuted() {
        return (mAsyncTask.getStatus() == AsyncTask.Status.PENDING
                && mSearchQuery != null
                && mRecipesList.isEmpty());
    }

    public interface RecipesListFragmentListener {
        String getQueryString();

        void setRecipe(RecipeEntity recipe);

        void navigateToDetailsFragment();
    }
}
