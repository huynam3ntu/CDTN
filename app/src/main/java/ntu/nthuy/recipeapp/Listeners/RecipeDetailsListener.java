package ntu.nthuy.recipeapp.Listeners;

import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);
    void didError(String message);
}
