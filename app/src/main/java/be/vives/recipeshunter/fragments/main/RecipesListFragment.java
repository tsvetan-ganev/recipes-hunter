package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.RecipesRecycleListAdapter;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.DownloadRecipesAsyncTask;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class RecipesListFragment extends Fragment {
    private String mSearchQuery = "";
    private List<RecipeEntity> mRecipesData;

    private RecipesListFragmentListener mListener;

    private RecyclerView mRecyclerView;
    private RecipesRecycleListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int mCurrentPage = 1;
    private boolean mIsLoading = false;
    private boolean mIsEndReached = false;

    public RecipesListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchQuery = mListener.getQueryString();
        mRecipesData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipes_list_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        try {
            if (mRecipesData.size() == 0) {
                mRecipesData = new DownloadRecipesAsyncTask(mSearchQuery).execute().get();
            }
            mAdapter = new RecipesRecycleListAdapter(mRecipesData);
            mRecyclerView.setAdapter(mAdapter);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                mListener.setRecipe(mRecipesData.get(position));
                mListener.navigateToDetailsFragment();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !mIsLoading) {
                    onScrolledToBottom();
                }
            }
        });

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

    private void onScrolledToBottom() {
        android.util.Log.d("RecyclerView", "End is reached at page " + Integer.toString(mCurrentPage));
        android.util.Log.d("RecyclerView", "Number of items: " + mRecipesData.size());

        if (mIsEndReached) {
            return;
        }

        String newQuery = mSearchQuery + "&page=" + Integer.toString(++mCurrentPage);
        try {
            mIsLoading = true;
            List<RecipeEntity> newlyLoadedRecipes = new DownloadRecipesAsyncTask(newQuery).execute().get();
            mRecipesData.addAll(newlyLoadedRecipes);
            mIsEndReached = newlyLoadedRecipes.isEmpty() ? true : false;

            if (!mIsEndReached) {
                mAdapter.notifyDataSetChanged();
            }
       } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        } finally {
            mIsLoading = false;
        }
    }

    public interface RecipesListFragmentListener {
        String getQueryString();
        void setRecipe(RecipeEntity recipe);
        void navigateToDetailsFragment();
    }
}