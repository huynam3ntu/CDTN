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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
