package be.vives.recipeshunter.data.viewmodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import be.vives.recipeshunter.data.entities.RecipeEntity;

/**
 * Model class for a single recipe.
 */
public class RecipeDetailsViewModel {

    private String id;
    private String title;
    private String publisherName;
    private String sourceUrl;
    private String imageUrl;
    private int socialRank;
    private List<String> ingredients;

    public RecipeDetailsViewModel() {
        this.id = "";
        this.title = "";
        this.publisherName = "";
        this.sourceUrl = "";
        this.imageUrl = "";
        this.socialRank = 0;
    }

    public RecipeDetailsViewModel(RecipeEntity recipeEntity, RecipeAdditionalInfoViewModel additionalInfo) {
        this.id = recipeEntity.getId();
        this.title = recipeEntity.getTitle();
        this.publisherName = recipeEntity.getPublisherName();
        this.imageUrl = recipeEntity.getImageUrl();
        this.socialRank = recipeEntity.getSocialRank();
        this.ingredients = additionalInfo.getIngredients();
        this.sourceUrl = additionalInfo.getSourceUrl();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSocialRank() {
        return socialRank;
    }

    public void setSocialRank(int socialRank) {
        if (socialRank < 0 || socialRank > 100) {
            throw new IllegalArgumentException("Social rank cannot be less than 0 or bigger than 100");
        }

        this.socialRank = socialRank;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.id);
        result.append(System.getProperty("line.separator"));
        result.append(this.title);
        result.append(System.getProperty("line.separator"));
        result.append(this.publisherName);
        result.append(System.getProperty("line.separator"));
        result.append(this.getSourceUrl());
        result.append(System.getProperty("line.separator"));
        result.append(this.getImageUrl());
        result.append(System.getProperty("line.separator"));
        result.append(this.getSocialRank());

        return result.toString();
    }
}
