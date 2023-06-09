package ntu.nthuy.recipeapp.Listeners;

import java.util.ArrayList;

import ntu.nthuy.recipeapp.Model.InstructionsReponse;

public interface InstructionsListener {
    void didFetch(ArrayList<InstructionsReponse> reponse, String message);
    void didError(String message);
}
