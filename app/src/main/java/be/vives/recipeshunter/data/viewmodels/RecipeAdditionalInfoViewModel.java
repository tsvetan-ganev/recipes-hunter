package be.vives.recipeshunter.data.viewmodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RecipeAdditionalInfoViewModel implements Parcelable {
  private List<String> ingredients;
  private String sourceUrl;

  public RecipeAdditionalInfoViewModel() {
  }

  public List<String> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<String> ingredients) {
    this.ingredients = ingredients;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  // Parcelable interface implementation
  @Override
  public int describeContents() {
    return 1;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeStringList(this.ingredients);
    dest.writeString(this.sourceUrl);
  }


  protected RecipeAdditionalInfoViewModel(Parcel in) {
    this.ingredients = in.createStringArrayList();
    this.sourceUrl = in.readString();
  }

  public static final Parcelable.Creator<RecipeAdditionalInfoViewModel> CREATOR = new Parcelable.Creator<RecipeAdditionalInfoViewModel>() {
    public RecipeAdditionalInfoViewModel createFromParcel(Parcel source) {
      return new RecipeAdditionalInfoViewModel(source);
    }

    public RecipeAdditionalInfoViewModel[] newArray(int size) {
      return new RecipeAdditionalInfoViewModel[size];
    }
  };
}
