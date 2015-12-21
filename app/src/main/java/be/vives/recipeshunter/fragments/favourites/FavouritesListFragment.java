package be.vives.recipeshunter.fragments.favourites;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.SwipeableRecipesRecyclerListAdapter;
import be.vives.recipeshunter.adapters.interactivity.OnItemDismissedListener;
import be.vives.recipeshunter.adapters.interactivity.TouchCallback;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.data.services.AsyncResponse;
import be.vives.recipeshunter.data.services.DeleteFavouriteRecipeAsyncTask;
import be.vives.recipeshunter.data.services.GetFavouriteRecipesAsyncTask;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class FavouritesListFragment extends Fragment {
    private List<RecipeEntity> mFavouriteRecipes;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private SwipeableRecipesRecyclerListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private FavouritesListFragmentListener mListener;
    private ItemTouchHelper mItemSwipeListener;

    private GetFavouriteRecipesAsyncTask mAsyncTask;

    public FavouritesListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavouriteRecipes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.favourites_list_progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favourites_list_recycler_view);

        mAsyncTask = new GetFavouriteRecipesAsyncTask(getActivity());
        mAsyncTask.delegate = new AsyncResponse<List<RecipeEntity>>() {
            @Override
            public void resolve(List<RecipeEntity> result) {
                mFavouriteRecipes.addAll(result);
                // probably not a good solution
                mAdapter.notifyItemRangeInserted(0, result.size());

                mProgressBar.setVisibility(View.GONE);
            }
        };

        mProgressBar.setVisibility(View.VISIBLE);
        mAsyncTask.execute();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new SwipeableRecipesRecyclerListAdapter(mFavouriteRecipes);
        mRecyclerView.setAdapter(mAdapter);

        // UI listeners
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                mListener.setRecipe(mFavouriteRecipes.get(position));
                mFavouriteRecipes.clear();
                mListener.navigateToDetailsFragment();
            }
        });

        mAdapter.delegate = new OnItemDismissedListener<RecipeEntity>() {
            @Override
            public void remove(RecipeEntity entity) {
                new DeleteFavouriteRecipeAsyncTask(getContext(), entity.getId()).execute();
                Toast.makeText(getContext(), entity.getTitle() + " removed from favourites.", Toast.LENGTH_SHORT).show();
            }
        };

        TouchCallback callback = new TouchCallback(mAdapter);
        mItemSwipeListener = new ItemTouchHelper(callback);
        mItemSwipeListener.attachToRecyclerView(mRecyclerView);

        return view;
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

    public interface FavouritesListFragmentListener {
        void setRecipe(RecipeEntity recipe);
        void navigateToDetailsFragment();
    }
}
