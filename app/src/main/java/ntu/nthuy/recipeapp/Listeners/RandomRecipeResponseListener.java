package ntu.nthuy.recipeapp.Listeners;

import ntu.nthuy.recipeapp.Model.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiResponse response, String message);
    void didError(String message);
}
