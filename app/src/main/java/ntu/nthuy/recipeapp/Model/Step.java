package ntu.nthuy.recipeapp.Model;

import java.util.ArrayList;

public class Step {
    public int number;
    public String step;
    public ArrayList<Ingredient> ingredients;
    public ArrayList<Equipment> equipment;

    public Step() {
    }

    public Step(int number, String step) {
        this.number = number;
        this.step = step;
    }
}
