package funix.prm.project.model;

public class Video {

    private String id;
    private String title;
    private String description;
    private String thumbnailURL;

    public Video(String id, String title, String description, String thumbnailURL) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailURL = thumbnailURL;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
