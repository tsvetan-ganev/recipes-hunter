package be.vives.recipeshunter.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.adapters.interactivity.SwipeableViewHolder;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements SwipeableViewHolder {
    public TextView mTitleTextView;
    public ImageView mImageView;
    public TextView mPublisherNameTextView;
    public TextView mSocialRankTextView;
    public ProgressBar mImageLoadingSpinner;

    public RecipeViewHolder(View view) {
        super(view);

        mTitleTextView = (TextView) view.findViewById(R.id.list_item_recipe_title);
        mImageView = (ImageView) view.findViewById(R.id.list_item_recipe_image);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.list_item_recipe_publisher_name);
        mSocialRankTextView = (TextView) view.findViewById(R.id.list_item_recipe_social_rank);
        mImageLoadingSpinner = (ProgressBar) view.findViewById(R.id.list_item_recipe_loading_spinner);
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.RED);
        Toast.makeText(itemView.getContext(), "Swipe to remove." , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemReleased() {
        itemView.setBackgroundColor(Color.BLUE);
    }
}
