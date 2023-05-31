package ntu.nthuy.recipeapp.Listeners;

import java.util.List;

import ntu.nthuy.recipeapp.Model.InstructionsReponse;

public interface InstructionsListener {
    void didFetch(List<InstructionsReponse> reponse, String message);
    void didError(String message);
}
