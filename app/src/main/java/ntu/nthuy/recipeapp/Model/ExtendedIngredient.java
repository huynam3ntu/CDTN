package ntu.nthuy.recipeapp.Model;

public class ExtendedIngredient {
    public int id;
    public String image;
    public String name;
    public String original;

    public ExtendedIngredient() {
    }

    public ExtendedIngredient(int id, String image, String name, String original) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.original = original;
    }
}
