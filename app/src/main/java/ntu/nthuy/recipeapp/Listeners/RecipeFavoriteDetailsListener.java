package ntu.nthuy.recipeapp.Listeners;

import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public interface RecipeFavoriteDetailsListener {
    void didFetch(RecipeDetailsResponse response);
}
