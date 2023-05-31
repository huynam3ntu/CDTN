package ntu.nthuy.recipeapp.Listeners;

import java.util.List;

import ntu.nthuy.recipeapp.Model.SimilarRecipesResponse;

public interface SimilarRecipesListener {
    void didFetch(List<SimilarRecipesResponse> response, String message);
    void didError(String message);
}
