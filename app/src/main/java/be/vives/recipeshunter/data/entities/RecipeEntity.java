package be.vives.recipeshunter.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeEntity implements Parcelable {
    private String id;
    private String title;
    private String publisherName;
    private String sourceUrl;
    private String imageUrl;
    private Integer socialRank;

    public RecipeEntity() {
        this.id = "";
        this.title = "";
        this.publisherName = "";
        this.sourceUrl = "";
        this.imageUrl = "";
        this.socialRank = 0;
    }

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

    public Integer getSocialRank() {
        return socialRank;
    }

    public void setSocialRank(Integer socialRank) {
        this.socialRank = socialRank;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id + " | ");
        sb.append(this.title + " | ");
        sb.append(this.publisherName + " | ");
        sb.append(this.socialRank);

        return sb.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.publisherName);
        dest.writeString(this.sourceUrl);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.socialRank);
    }

    protected RecipeEntity(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.publisherName = in.readString();
        this.sourceUrl = in.readString();
        this.imageUrl = in.readString();
        this.socialRank = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<RecipeEntity> CREATOR = new Creator<RecipeEntity>() {
        public RecipeEntity createFromParcel(Parcel source) {
            return new RecipeEntity(source);
        }

        public RecipeEntity[] newArray(int size) {
            return new RecipeEntity[size];
        }
    };
}
