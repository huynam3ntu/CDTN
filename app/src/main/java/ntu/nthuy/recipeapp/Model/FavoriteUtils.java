package ntu.nthuy.recipeapp.Model;

public class FavoriteUtils {
    public int id;
    public String title;
    public String image;
    public String summary;
    public String note;

    public FavoriteUtils() {
    }

    public FavoriteUtils(int id, String title, String image, String summary, String note) {
        this.id = id;
        this.title = title;
        this.image = image;
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

    @Override
    public String toString() {
        return "FavoriteUtils{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", summary='" + summary + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}