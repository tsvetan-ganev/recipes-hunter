package be.vives.recipeshunter.fragments.favourites;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.RecipesRecycleListAdapter;
import be.vives.recipeshunter.data.local.dao.impl.RecipeDAOImpl;
import be.vives.recipeshunter.data.entities.RecipeEntity;
import be.vives.recipeshunter.utils.ItemClickSupport;

public class FavouritesListFragment extends Fragment {
    private RecipeDAOImpl mRecipeDao;
    private List<RecipeEntity> mFavouriteRecipes;

    private RecyclerView mRecyclerView;
    private RecipesRecycleListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private FavouritesListFragmentListener mListener;

    public FavouritesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeDao = new RecipeDAOImpl(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favourites_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.favourites_list_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // get data from the DB
        mRecipeDao.open();
        mFavouriteRecipes = getFavouriteRecipesList();
        mRecipeDao.close();

        mAdapter = new RecipesRecycleListAdapter(mFavouriteRecipes);
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.d("Item Clicked: ", mFavouriteRecipes.get(position).toString());
                mListener.setRecipe(mFavouriteRecipes.get(position));
                mListener.navigateToDetailsFragment();
            }
        });

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

    private List<RecipeEntity> getFavouriteRecipesList() {
         return mRecipeDao.findAll();
    }

    public interface FavouritesListFragmentListener {
        void setRecipe(RecipeEntity recipe);
        void navigateToDetailsFragment();
    }
}
