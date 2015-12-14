package be.vives.recipeshunter.models;

import java.net.MalformedURLException;
import java.util.List;
import java.net.URL;

/**
 * Model class for a single recipe.
 */
public class Recipe {

    private String id;
    private String title;
    private String publisherName;
    private URL sourceUrl;
    private URL imageUrl;
    private int socialRank;
    private List<String> ingredients;

    public Recipe() {
        this.title = "";
        this.publisherName = "";
        this.socialRank = 0;
    }

    public Recipe(String title, String publisherName, String sourceUrl) {
        this.title = title;
        this.publisherName = publisherName;
        try {
            this.sourceUrl = new URL(sourceUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.socialRank = 0;
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

    public URL getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(URL sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
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
        result.append(this.getSourceUrl().toString());

        return result.toString();
    }
}
