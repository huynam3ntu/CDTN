package ntu.nthuy.recipeapp.Listeners;

import java.util.List;

import ntu.nthuy.recipeapp.Model.SimilarRecipesResponse;

public interface SimilarRecipesListener {
    void fetch(List<SimilarRecipesResponse> response, String message);
    void err(String message);
}
