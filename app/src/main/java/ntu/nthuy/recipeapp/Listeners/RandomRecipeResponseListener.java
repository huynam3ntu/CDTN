package ntu.nthuy.recipeapp.Listeners;

import ntu.nthuy.recipeapp.Model.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void fetch(RandomRecipeApiResponse response, String message);
    void err(String message);
}
