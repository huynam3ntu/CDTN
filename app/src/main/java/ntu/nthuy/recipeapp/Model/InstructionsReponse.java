package ntu.nthuy.recipeapp.Model;

import java.util.ArrayList;

public class InstructionsReponse {
    public String name;
    public ArrayList<Step> steps;

    public InstructionsReponse() {
    }

    public InstructionsReponse(String name, ArrayList<Step> steps) {
        this.name = name;
        this.steps = steps;
    }
}
