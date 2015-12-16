package be.vives.recipeshunter.data.viewmodels;

import java.util.List;

public class RecipeAdditionalInfoViewModel {
  private List<String> ingredients;
  private String sourceUrl;

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
}
