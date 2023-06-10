package ntu.nthuy.recipeapp.Listeners;

import java.util.ArrayList;

import ntu.nthuy.recipeapp.Model.InstructionsReponse;

public interface InstructionsListener {
    void fetch(ArrayList<InstructionsReponse> reponse, String message);
    void err(String message);
}
