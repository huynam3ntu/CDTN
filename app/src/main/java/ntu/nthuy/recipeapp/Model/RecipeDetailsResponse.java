package ntu.nthuy.recipeapp.Model;

import java.util.ArrayList;

public class RecipeDetailsResponse {
    public int id;
    public String title;
    public String image;
    public String sourceName;
    public ArrayList<ExtendedIngredient> extendedIngredients;
    public ArrayList<InstructionsReponse> instructionsReponses;
    public String summary;
    public String note;

    public RecipeDetailsResponse() {
    }

    public RecipeDetailsResponse(int id, String title, String image, String sourceName, ArrayList<ExtendedIngredient> extendedIngredients, ArrayList<InstructionsReponse> instructionsReponses, String summary, String note) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.sourceName = sourceName;
        this.extendedIngredients = extendedIngredients;
        this.instructionsReponses = instructionsReponses;
        this.summary = summary;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public ArrayList<ExtendedIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(ArrayList<ExtendedIngredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public ArrayList<InstructionsReponse> getInstructionsReponses() {
        return instructionsReponses;
    }

    public void setInstructionsReponses(ArrayList<InstructionsReponse> instructionsReponses) {
        this.instructionsReponses = instructionsReponses;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
