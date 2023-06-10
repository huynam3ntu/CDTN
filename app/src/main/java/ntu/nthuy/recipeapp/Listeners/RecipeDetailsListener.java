package ntu.nthuy.recipeapp.Listeners;

import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void fetch(RecipeDetailsResponse response, String message);
    void err(String message);
}
