package be.vives.recipeshunter.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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

    private static int mBgColor = 0;

    public RecipeViewHolder(View view) {
        super(view);

        if (mBgColor == 0) {
            mBgColor = ContextCompat.getColor(view.getContext(), R.color.card_view_bg_dark);
        }

        mTitleTextView = (TextView) view.findViewById(R.id.list_item_recipe_title);
        mImageView = (ImageView) view.findViewById(R.id.list_item_recipe_image);
        mPublisherNameTextView = (TextView) view.findViewById(R.id.list_item_recipe_publisher_name);
        mSocialRankTextView = (TextView) view.findViewById(R.id.list_item_recipe_social_rank);
        mImageLoadingSpinner = (ProgressBar) view.findViewById(R.id.list_item_recipe_loading_spinner);
    }

    @Override
    public void onItemSelected() {
        CardView cardView = (CardView) itemView;
        cardView.setCardBackgroundColor(Color.RED);
        Toast.makeText(itemView.getContext(), itemView.getResources().getString(R.string.swipe_to_remove_hint) , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemReleased() {
        CardView cardView = (CardView) itemView;
        cardView.setCardBackgroundColor(mBgColor);
    }
}
