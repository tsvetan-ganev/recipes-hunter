package be.vives.recipeshunter.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.models.Recipe;

public class RecipesRecycleListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recipe> mRecipesData;
    private DisplayImageOptions mImageOptions;

    public RecipesRecycleListAdapter(List<Recipe> recipes) {
        mRecipesData = recipes;
        mImageOptions = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public RecipesRecycleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_recipe, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.mTitleTextView.setText(mRecipesData.get(position).getTitle());
        vh.mPublisherNameTextView.setText(mRecipesData.get(position).getPublisherName());
        vh.mSocialRankTextView.setText(Integer.toString(mRecipesData.get(position).getSocialRank()) + "/100");

        ImageLoader.getInstance().displayImage(
                String.valueOf(mRecipesData.get(position).getImageUrl()),
                vh.mImageView,
                mImageOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        vh.mImageLoadingSpinner.setVisibility(View.VISIBLE);
                        vh.mImageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        vh.mImageLoadingSpinner.setVisibility(View.GONE);
                        vh.mImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mRecipesData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public ImageView mImageView;
        public TextView mPublisherNameTextView;
        public TextView mSocialRankTextView;
        public ProgressBar mImageLoadingSpinner;

        public ViewHolder(View view) {
            super(view);

            mTitleTextView = (TextView) view.findViewById(R.id.list_item_recipe_title);
            mImageView = (ImageView) view.findViewById(R.id.list_item_recipe_image);
            mPublisherNameTextView = (TextView) view.findViewById(R.id.list_item_recipe_publisher_name);
            mSocialRankTextView = (TextView) view.findViewById(R.id.list_item_recipe_social_rank);
            mImageLoadingSpinner = (ProgressBar) view.findViewById(R.id.list_item_recipe_loading_spinner);
        }
    }
}
