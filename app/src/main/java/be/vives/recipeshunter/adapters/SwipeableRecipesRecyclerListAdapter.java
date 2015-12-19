package be.vives.recipeshunter.adapters;

import java.util.List;

import be.vives.recipeshunter.adapters.interactivity.SwipeableItemsAdapter;
import be.vives.recipeshunter.data.entities.RecipeEntity;

/**
 * Created by tsetso on 19.12.15.
 */
public class SwipeableRecipesRecyclerListAdapter extends RecipesRecycleListAdapter implements SwipeableItemsAdapter {

    public SwipeableRecipesRecyclerListAdapter(List<RecipeEntity> recipes) {
        super(recipes);
    }

    @Override
    public void onItemDismiss(int position) {
        removeItemAt(position);
    }

    private void removeItemAt(int position) {
        mRecipesData.remove(position);
        notifyItemRemoved(position);
    }
}
