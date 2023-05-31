package ntu.nthuy.recipeapp.Listeners;

import java.util.List;

import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;

public interface OnFavoriteRecipesLoadedListener {
    void onFavoriteRecipesLoaded(List<RecipeDetailsResponse> favoriteRecipes);
}
