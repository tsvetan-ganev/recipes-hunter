package be.vives.recipeshunter.adapters;

import java.util.ArrayList;
import java.util.List;

import be.vives.recipeshunter.adapters.interactivity.OnItemDismissedListener;
import be.vives.recipeshunter.adapters.interactivity.SwipeableItemsAdapter;
import be.vives.recipeshunter.data.entities.RecipeEntity;

public class SwipeableRecipesRecyclerListAdapter extends RecipesRecycleListAdapter implements SwipeableItemsAdapter<RecipeEntity> {
    private List<RecipeEntity> mRemovedRecipes;

    public OnItemDismissedListener delegate;

    public SwipeableRecipesRecyclerListAdapter(List<RecipeEntity> recipes) {
        super(recipes);
        mRemovedRecipes = new ArrayList<>();
    }

    @Override
    public RecipeEntity onItemDismiss(int position) {
        RecipeEntity removedRecipe = removeItemAt(position);
        mRemovedRecipes.add(removedRecipe);

        delegate.remove(removedRecipe);

        return removedRecipe;
    }

    private RecipeEntity removeItemAt(int position) {
        RecipeEntity removedRecipe = mRecipesData.remove(position);
        notifyItemRemoved(position);

        return removedRecipe;
    }

}
